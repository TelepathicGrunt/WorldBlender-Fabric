package com.telepathicgrunt.world_blender.dimension;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.telepathicgrunt.world_blender.the_blender.ConfigBlacklisting;
import com.telepathicgrunt.world_blender.the_blender.ConfigBlacklisting.BlacklistType;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;


public class WBDragonManager
{
	private static final Predicate<Entity> VALID_PLAYER = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maximumDistance(0.0D, 128.0D, 0.0D, 192.0D));
	private final ServerBossBar bossInfo = (ServerBossBar) (new ServerBossBar(new TranslatableText("entity.minecraft.ender_dragon"), BossBar.Color.PINK, BossBar.Style.PROGRESS)).setDragonMusic(true).setThickenFog(true);
	private final ServerWorld world;
	private final BlockPattern portalPattern;
	private int ticksSinceDragonSeen;
	private int aliveCrystals;
	private int ticksSinceCrystalsScanned;
	private int ticksSinceLastPlayerScan;
	private boolean dragonKilled;
	private boolean previouslyKilled;
	private UUID dragonUniqueId;
	private BlockPos exitPortalLocation;
	private WBDragonSpawnState respawnState;
	private boolean generatedInitialFight = false;
	private int respawnStateTicks;
	private EnderDragonEntity enderDragon;
	private List<EnderCrystalEntity> crystals;
	private boolean noCrystalAlive = false;


	public WBDragonManager(ServerWorld serverWorld)
	{
		this.world = serverWorld;
		WBWorldSavedData savedData = WBWorldSavedData.get(serverWorld);

		if (savedData.isDragonDataSaved())
		{
			this.dragonUniqueId = savedData.getDragonUUID();
			this.dragonKilled = savedData.isDragonKilled();
			
			this.previouslyKilled = savedData.isDragonPreviouslyKilled();
			this.generatedInitialFight = savedData.isInitialFightGenerated();
			if (savedData.isDragonRespawning())
			{
				this.respawnState = WBDragonSpawnState.START;
			}
			
			this.exitPortalLocation = savedData.getEndAltarPosition();
		}
		else
		{
			this.dragonKilled = true;
			this.previouslyKilled = true;
		}

		this.portalPattern = BlockPatternBuilder.start()
				.aisle( "       ", 
						"       ", 
						"       ", 
						"   #   ", 
						"       ", 
						"       ", 
						"       ")
				
				.aisle( "       ", 
						"       ", 
						"       ", 
						"   #   ", 
						"       ", 
						"       ", 
						"       ")
				
				.aisle( "       ", 
						"       ", 
						"       ", 
						"   #   ", 
						"       ", 
						"       ", 
						"       ")
				
				.aisle( "  ###  ", 
						" #   # ", 
						"#     #", 
						"#  #  #", 
						"#     #", 
						" #   # ", 
						"  ###  ")
				
				.aisle( "       ", 
						"  ###  ", 
						" ##### ", 
						" ##### ", 
						" ##### ", 
						"  ###  ", 
						"       ")
				.where('#', CachedBlockPosition.matchesBlockState(BlockPredicate.make(Blocks.BEDROCK))).build();

	}


	public void saveWBDragonData(World world) 
	{
		WBWorldSavedData.get(world).setDragonKilled(this.dragonKilled);
		WBWorldSavedData.get(world).setDragonPreviouslyKilled(this.previouslyKilled);
		WBWorldSavedData.get(world).setDragonRespawning(this.respawnState != null);
		WBWorldSavedData.get(world).setGeneratedInitialFight(this.generatedInitialFight);
		WBWorldSavedData.get(world).setDragonUUID(this.dragonUniqueId);
		WBWorldSavedData.get(world).setEndAltarPosition(this.exitPortalLocation);
		WBWorldSavedData.get(world).setDragonDataSaved(true);
		WBWorldSavedData.get(world).markDirty();
	}


	@SuppressWarnings("deprecation")
	public void tick()
	{
		this.bossInfo.setVisible(!this.dragonKilled);
		if (++this.ticksSinceLastPlayerScan >= 20)
		{
			this.updatePlayers();
			this.ticksSinceLastPlayerScan = 0;
		}

		if (!this.bossInfo.getPlayers().isEmpty())
		{
			this.world.getChunkManager().addTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
			boolean flag = this.isWorldOriginTicking();
			if(flag)
			{
				//make sure the ID matches a real entity and grabs that dragon.
				if(!this.dragonKilled && this.dragonUniqueId != null) 
				{
					this.enderDragon = (EnderDragonEntity) this.world.getEntity(this.dragonUniqueId);
					if(this.enderDragon == null)
					{
						this.dragonUniqueId = null;
						this.dragonKilled = true;
						saveWBDragonData(this.world);
					}
				}
				
				
				if (!this.generatedInitialFight)
				{
					this.generatePortalAndDragon();
					this.generatedInitialFight = true;
					saveWBDragonData(this.world);
				}
				
	
				if (this.respawnState != null)
				{
					if (this.crystals == null)
					{
						this.respawnState = null;
						this.tryRespawnDragon();
					}

					if (this.crystals != null)
					{
						this.respawnState.process(this.world, this, this.crystals, this.respawnStateTicks++, this.exitPortalLocation);
					}
				}
				else 
				{
					this.tryRespawnDragon();
				}

				if (!this.dragonKilled && this.enderDragon != null && !this.enderDragon.removed)
				{
					if (++this.ticksSinceDragonSeen >= 1200)
					{
						this.findOrCreateDragon();
						this.ticksSinceDragonSeen = 0;
					}
					else if(this.enderDragon != null)
					{
						dragonUpdate(this.enderDragon);
					}

					if (++this.ticksSinceCrystalsScanned >= 100 && !this.noCrystalAlive)
					{
						this.findAliveCrystals();
						this.ticksSinceCrystalsScanned = 0;
					}
					
					if(this.enderDragon.getHealth() <= 0)
					{
						this.enderDragon.method_6824(250);
					}
				}
				else
				{
					if(this.enderDragon != null && this.enderDragon.removed) 
					{
						processDragonDeath(this.enderDragon);
						saveWBDragonData(this.world);
					}
				}
			}
		}
		else
		{
			this.world.getChunkManager().removeTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
		}

	}


	private void generatePortalAndDragon()
	{
		boolean flag = this.worldContainsEndPortal();
		if (flag)
		{
			this.previouslyKilled = true;
		}
		else
		{
			this.previouslyKilled = false;
			if (this.findExitPortal() == null)
			{
				this.generatePortal(false);
			}
		}

		findOrCreateDragon();
		this.dragonKilled = false;

		if (!this.previouslyKilled && this.dragonKilled)
		{
			this.dragonKilled = false;
		}
	}


	private void findOrCreateDragon()
	{
		List<EnderDragonEntity> list = this.world.getAliveEnderDragons();
		if (list.isEmpty())
		{
			//LOGGER.debug("Haven't seen the dragon, respawning it");
			this.createNewDragon();
		}
		else
		{
			//LOGGER.debug("Haven't seen our dragon, but found another one to use.");
			this.dragonUniqueId = list.get(0).getUuid();
			this.enderDragon = list.get(0);
		}

	}


	protected void setRespawnState(WBDragonSpawnState preparingToSummonPillars)
	{
		if (this.respawnState == null)
		{
			throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
		}
		else
		{
			this.respawnStateTicks = 0;
			if (preparingToSummonPillars == WBDragonSpawnState.END)
			{
				this.respawnState = null;
				this.dragonKilled = false;
				EnderDragonEntity enderdragonentity = this.createNewDragon();

				for (ServerPlayerEntity serverplayerentity : this.bossInfo.getPlayers())
				{
					Criterions.SUMMONED_ENTITY.trigger(serverplayerentity, enderdragonentity);
				}
			}
			else if(doesRespawnCrystalExist() != null)
			{
				this.respawnState = preparingToSummonPillars;
			}

		}
	}


	private boolean worldContainsEndPortal()
	{
		for (int x = -1; x <= 1; ++x)
		{
			for (int z = -1; z <= 1; ++z)
			{
				WorldChunk chunk = this.world.getChunk(x, z);

				for (BlockEntity tileentity : chunk.getBlockEntities().values())
				{
					if (tileentity instanceof EndPortalBlockEntity)
					{
						return true;
					}
				}
			}
		}

		return false;
	}


	@Nullable
	private BlockPattern.Result findExitPortal()
	{
		for (int x = -1; x <= 1; ++x)
		{
			for (int z = -1; z <= 1; ++z)
			{
				WorldChunk chunk = this.world.getChunk(x, z);

				for (BlockEntity tileentity : chunk.getBlockEntities().values())
				{
					if (tileentity instanceof EndPortalBlockEntity)
					{
						BlockPattern.Result blockpattern$patternhelper = this.portalPattern.searchAround(this.world, tileentity.getPos());
						if (blockpattern$patternhelper != null)
						{
							BlockPos blockpos = blockpattern$patternhelper.translate(3, 3, 3).getBlockPos();
							if (this.exitPortalLocation == null && blockpos.getX() == 0 && blockpos.getZ() == 0)
							{
								this.exitPortalLocation = blockpos;
							}

							return blockpattern$patternhelper;
						}
					}
				}
			}
		}

		int maxHeight = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN).getY() + 1;

		for (int currentHeight = maxHeight; currentHeight >= 0; --currentHeight)
		{
			BlockPattern.Result blockpattern$patternhelper1 = this.portalPattern.searchAround(this.world, new BlockPos(EndPortalFeature.ORIGIN.getX(), currentHeight, EndPortalFeature.ORIGIN.getZ()));
			if (blockpattern$patternhelper1 != null)
			{
				if (this.exitPortalLocation == null)
				{
					this.exitPortalLocation = blockpattern$patternhelper1.translate(3, 3, 3).getBlockPos();
				}

				return blockpattern$patternhelper1;
			}
		}

		return null;
	}


	private boolean isWorldOriginTicking()
	{
		for (int x = -4; x <= 4; ++x)
		{
			for (int z = -4; z <= 4; ++z)
			{
				Chunk ichunk = this.world.getChunk(x, z, ChunkStatus.FULL, false);
				if (!(ichunk instanceof WorldChunk))
				{
					return false;
				}

				ChunkHolder.LevelType chunkholder$locationtype = ((WorldChunk) ichunk).getLevelType();
				if (!chunkholder$locationtype.isAfter(ChunkHolder.LevelType.TICKING))
				{
					return false;
				}
			}
		}

		return true;
	}



	private void updatePlayers()
	{
		Set<ServerPlayerEntity> set = Sets.newHashSet();

		for (ServerPlayerEntity serverplayerentity : this.world.getPlayers(VALID_PLAYER))
		{
			this.bossInfo.addPlayer(serverplayerentity);
			set.add(serverplayerentity);
		}

		Set<ServerPlayerEntity> set1 = Sets.newHashSet(this.bossInfo.getPlayers());
		set1.removeAll(set);

		for (ServerPlayerEntity serverplayerentity1 : set1)
		{
			this.bossInfo.removePlayer(serverplayerentity1);
		}

	}


	private void findAliveCrystals()
	{
		this.ticksSinceCrystalsScanned = 0;
		this.aliveCrystals = 0;

		for (EndSpikeFeature.Spike endspikefeature$endspike : EndSpikeFeature.getSpikes(this.world))
		{
			this.aliveCrystals += this.world.getNonSpectatingEntities(EnderCrystalEntity.class, endspikefeature$endspike.getBoundingBox()).size();
		}

		if(this.aliveCrystals == 0) this.noCrystalAlive = true;
		
		//LOGGER.debug("Found {} end crystals still alive", this.aliveCrystals);
	}


	public void processDragonDeath(EnderDragonEntity dragonEntity)
	{
		if (dragonEntity == null || dragonEntity.getUuid().equals(this.dragonUniqueId))
		{
			this.bossInfo.setPercent(0.0F);
			this.bossInfo.setVisible(false);
			this.generatePortal(true);
			
			ServerWorld endWorld = this.world.getServer().getWorld(DimensionType.THE_END);
			BlockPos.Mutable blockPos = new BlockPos.Mutable(0,255,0);
			
			//looks to see if the end podium was created yet
			while(blockPos.getY() > 0 && endWorld.getBlockState(blockPos) != Blocks.BEDROCK.getDefaultState())
			{
				blockPos.setOffset(Direction.DOWN);
			}
			
			//We had found that the portal was not made. Create out pillar to hold the podium so the obsidian platform wont break it
			if(blockPos.getY() == 0)
			{
				for(int x = -3; x <= 3; x++)
				{
					for(int z = -3; z <= 3; z++)
					{
						for(int height = 0; height <= 35; height++)
						{
							if(x*x+z*z < 15)
								endWorld.setBlockState(blockPos.add(x, 0, z), Blocks.OBSIDIAN.getDefaultState(), 3);
							
							blockPos.setOffset(Direction.UP);
						}
						
						blockPos.set(0, 40, 0);
					}
				}
			}
			
			if (!this.previouslyKilled)
			{
				this.world.setBlockState(this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN), Blocks.DRAGON_EGG.getDefaultState());
			}

			this.previouslyKilled = true;
			this.dragonKilled = true;
			this.enderDragon = null;
		}

	}

	private void generatePortal(boolean portalActive)
	{
		//will not generate end podium if it is directly blacklisted
		if(ConfigBlacklisting.isResourceLocationBlacklisted(BlacklistType.FEATURE, new Identifier("minecraft:end_podium")))
			return;
		
		EndPortalFeature endpodiumfeature = new EndPortalFeature(portalActive);
		if (this.exitPortalLocation == null)
		{
			for (this.exitPortalLocation = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN); 
					this.world.getBlockState(this.exitPortalLocation).getBlock() == Blocks.BEDROCK && this.exitPortalLocation.getY() > this.world.getSeaLevel(); 
					this.exitPortalLocation = this.exitPortalLocation.down())
			{
				;
			}
		}
		endpodiumfeature.configure(FeatureConfig.DEFAULT).generate(this.world, this.world.getChunkManager().getChunkGenerator(), new Random(), this.exitPortalLocation);
	}


	private EnderDragonEntity createNewDragon()
	{
		this.world.getWorldChunk(new BlockPos(0, 128, 0));
		EnderDragonEntity enderdragonentity = EntityType.ENDER_DRAGON.create(this.world);
		enderdragonentity.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
		enderdragonentity.refreshPositionAndAngles(0.0D, 128.0D, 0.0D, this.world.random.nextFloat() * 360.0F, 0.0F);
		this.world.spawnEntity(enderdragonentity);
		this.dragonUniqueId = enderdragonentity.getUuid();
		this.enderDragon = enderdragonentity;
		return enderdragonentity;
	}


	public void dragonUpdate(EnderDragonEntity dragon)
	{
		if (dragon.getUuid().equals(this.dragonUniqueId))
		{
			this.bossInfo.setPercent(dragon.getHealth() / dragon.getMaximumHealth());
			this.ticksSinceDragonSeen = 0;
			if (dragon.hasCustomName())
			{
				this.bossInfo.setName(dragon.getDisplayName());
			}
		}

	}

	public void tryRespawnDragon()
	{
		if (this.dragonKilled && this.respawnState == null)
		{
			BlockPos blockpos = this.exitPortalLocation;
			if (blockpos == null)
			{
				BlockPattern.Result blockpattern$patternhelper = this.findExitPortal();
				if (blockpattern$patternhelper == null)
				{
					this.generatePortal(true);
				}

				blockpos = this.exitPortalLocation;
			}

			List<EnderCrystalEntity> list1 = doesRespawnCrystalExist();
			if(list1 == null) return;
			
			this.respawnDragon(list1);
		}

	}
	
	private List<EnderCrystalEntity> doesRespawnCrystalExist() {
		BlockPos blockpos1 = this.exitPortalLocation.up(1);
		List<EnderCrystalEntity> list1 = Lists.newArrayList();

		for (Direction direction : Direction.Type.HORIZONTAL)
		{
			List<EnderCrystalEntity> list = this.world.getNonSpectatingEntities(EnderCrystalEntity.class, new Box(blockpos1.offset(direction, 2)));
			if (list.isEmpty())
			{
				return null;
			}

			list1.addAll(list);
		}
		return list1;
	}


	private void respawnDragon(List<EnderCrystalEntity> p_186093_1_)
	{
		if (this.dragonKilled && this.respawnState == null)
		{
			for (BlockPattern.Result blockpattern$patternhelper = this.findExitPortal(); blockpattern$patternhelper != null; blockpattern$patternhelper = this.findExitPortal())
			{
				for (int i = 0; i < this.portalPattern.getWidth(); ++i)
				{
					for (int j = 0; j < this.portalPattern.getHeight(); ++j)
					{
						for (int k = 0; k < this.portalPattern.getDepth(); ++k)
						{
							CachedBlockPosition cachedblockinfo = blockpattern$patternhelper.translate(i, j, k);
							if (cachedblockinfo.getBlockState().getBlock() == Blocks.BEDROCK || cachedblockinfo.getBlockState().getBlock() == Blocks.END_PORTAL)
							{
								this.world.setBlockState(cachedblockinfo.getBlockPos(), Blocks.END_STONE.getDefaultState());
							}
						}
					}
				}
			}

			this.respawnState = WBDragonSpawnState.START;
			this.respawnStateTicks = 0;
			this.generatePortal(false);
			this.crystals = p_186093_1_;
		}

	}


	public void resetSpikeCrystals()
	{
		for (EndSpikeFeature.Spike endspikefeature$endspike : EndSpikeFeature.getSpikes(this.world))
		{
			for (EnderCrystalEntity endercrystalentity : this.world.getNonSpectatingEntities(EnderCrystalEntity.class, endspikefeature$endspike.getBoundingBox()))
			{
				endercrystalentity.setInvulnerable(false);
				endercrystalentity.setBeamTarget((BlockPos) null);
			}
		}
	}

}
