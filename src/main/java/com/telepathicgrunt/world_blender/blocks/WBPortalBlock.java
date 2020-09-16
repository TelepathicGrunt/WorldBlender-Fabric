package net.telepathicgrunt.worldblender.blocks;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.telepathicgrunt.worldblender.dimension.WBDimensionRegistration;


public class WBPortalBlock extends BlockWithEntity
{
	
	protected static final VoxelShape COLLISION_BOX = Block.createCuboidShape(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	
	protected WBPortalBlock()
	{
		super(Block.Settings.of(Material.PORTAL, MaterialColor.BLACK).noCollision().lightLevel(6).strength(-1.0F, 3600000.0F).dropsNothing());
	}


	@Override
	public BlockEntity createBlockEntity(BlockView blockReader)
	{
		return new WBPortalTileEntity();
	}


	@SuppressWarnings("resource")
	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos position, Entity entity)
	{
		BlockEntity tileentity = world.getBlockEntity(position);
		if (tileentity instanceof WBPortalTileEntity)
		{
			WBPortalTileEntity wbtile = (WBPortalTileEntity) tileentity;

			if (!world.isClient && !wbtile.isCoolingDown() && !entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals() && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset((-position.getX()), (-position.getY()), (-position.getZ()))), COLLISION_BOX, BooleanBiFunction.AND))
			{
				//gets the world in the destination dimension
				MinecraftServer minecraftServer = entity.getServer(); // the server itself
				ServerWorld destinationWorld = minecraftServer.getWorld(world.dimension.getType() == WBDimensionRegistration.worldblender() ? DimensionType.OVERWORLD : WBDimensionRegistration.worldblender());
				ServerWorld originalWorld = minecraftServer.getWorld(entity.dimension);

				BlockPos destPos = null;

				//looks for portal blocks in other dimension
				//within a 9x256x9 area
				boolean portalOrChestFound = false;
				for (BlockPos blockpos : BlockPos.iterate(position.add(-4, -position.getY(), -4), position.add(4, 255 - position.getY(), 4)))
				{
					Block blockNearTeleport = destinationWorld.getBlockState(blockpos).getBlock();

					if (blockNearTeleport == WBBlocks.WORLD_BLENDER_PORTAL.get())
					{
						//gets portal block closest to players original xz coordinate
						if (destPos == null || (Math.abs(blockpos.getX() - position.getX()) < Math.abs(destPos.getX() - position.getX()) && Math.abs(blockpos.getZ() - position.getZ()) < Math.abs(destPos.getZ() - position.getZ())))
							destPos = blockpos.toImmutable();

						portalOrChestFound = true;

						//make portals have a cooldown after being teleported to
						WBPortalTileEntity wbtile2 = (WBPortalTileEntity) destinationWorld.getBlockEntity(blockpos);
						wbtile2.triggerCooldown();
					}
					else if (blockNearTeleport.getTags().contains(net.minecraftforge.common.Tags.Blocks.CHESTS.getId()))
					{
						//only set position to chest if no portal block is found
						if (destPos == null)
							destPos = blockpos.toImmutable();
						portalOrChestFound = true;
					}
				}

				//no portal or chest was found around destination. just teleport to top land
				if (!portalOrChestFound)
				{
					destPos = destinationWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, position);

					//places a portal block in World Blender so player can escape if
					//there is no portal block and then makes it be in cooldown
					if (destinationWorld.getWorld().dimension.getType() == WBDimensionRegistration.worldblender())
					{
						destinationWorld.setBlockState(destPos, Blocks.AIR.getDefaultState());
						destinationWorld.setBlockState(destPos.up(), Blocks.AIR.getDefaultState());
						
						destinationWorld.setBlockState(destPos, WBBlocks.WORLD_BLENDER_PORTAL.get().getDefaultState());
						WBPortalTileEntity wbtile2 = (WBPortalTileEntity) destinationWorld.getBlockEntity(destPos);
						wbtile2.triggerCooldown();
					}
				}

				wbtile.teleportEntity(entity, destPos, destinationWorld, originalWorld);
			}
		}
	}


	/**
	 * Turns this portal blocks to air when right clicked while crouching
	 */
	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult rayTrace)
	{
		if(playerEntity.isInSneakingPose() && ((WBPortalTileEntity)world.getBlockEntity(blockPos)).isRemoveable()) 
		{
			if (world.isClient)
			{
				//show lots of particles when portal is removed
				createLotsOfParticles(blockState, world, blockPos, world.random);
				return ActionResult.SUCCESS;
			}
			else
			{
				//remove this portal
				world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
				
				return ActionResult.SUCCESS;
			}
		}
		
		return ActionResult.FAIL;
	}


	/**
	 * Shows particles around this block
	 */
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState blockState, World world, BlockPos position, Random random)
	{
		BlockEntity tileentity = world.getBlockEntity(position);
		if (tileentity instanceof WBPortalTileEntity)
		{
			if (random.nextFloat() < 0.09f)
			{
				double xPos = (double) position.getX() + (double) random.nextFloat();
				double yPos = (double) position.getY() + (double) random.nextFloat();
				double zPos = (double) position.getZ() + (double) random.nextFloat();
				double xVelocity = (random.nextFloat() - 0.5D) * 0.08D;
				double yVelocity = (random.nextFloat() - 0.5D) * 0.13D;
				double zVelocity = (random.nextFloat() - 0.5D) * 0.08D;

				world.addParticle(ParticleTypes.END_ROD, xPos, yPos, zPos, xVelocity, yVelocity, zVelocity);
			}

		}
	}

	@Environment(EnvType.CLIENT)
	public void createLotsOfParticles(BlockState blockState, World world, BlockPos position, Random random)
	{
		BlockEntity tileentity = world.getBlockEntity(position);
		if (tileentity instanceof WBPortalTileEntity)
		{
			for(int i = 0; i < 50; i++) 
			{
				double xPos = (double) position.getX() + (double) random.nextFloat();
				double yPos = (double) position.getY() + (double) random.nextFloat();
				double zPos = (double) position.getZ() + (double) random.nextFloat();
				double xVelocity = (random.nextFloat() - 0.5D) * 0.08D;
				double yVelocity = (random.nextFloat() - 0.5D) * 0.13D;
				double zVelocity = (random.nextFloat() - 0.5D) * 0.08D;

				world.addParticle(ParticleTypes.END_ROD, xPos, yPos, zPos, xVelocity, yVelocity, zVelocity);
			}
		}
	}

	@Override
	public ItemStack getPickStack(BlockView p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_)
	{
		return ItemStack.EMPTY;
	}


	public boolean canBucketPlace(BlockState p_225541_1_, Fluid p_225541_2_)
	{
		return false;
	}
}
