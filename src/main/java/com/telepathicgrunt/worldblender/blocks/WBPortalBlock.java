package com.telepathicgrunt.worldblender.blocks;

import com.telepathicgrunt.worldblender.WBIdentifiers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;


public class WBPortalBlock extends BlockWithEntity
{
	protected static final VoxelShape COLLISION_BOX = Block.createCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
	
	protected WBPortalBlock()
	{
		super(Block.Settings.of(Material.PORTAL, MapColor.BLACK)
				.noCollision()
				.luminance((blockState) -> 6)
				.strength(-1.0F, 3600000.0F)
				.dropsNothing()
				.allowsSpawning((state, world, pos, entityType) -> false)
				.solidBlock((state, world, pos) -> false)
				.suffocates((state, world, pos) -> false)
		);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new WBPortalBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, WBBlocks.WORLD_BLENDER_PORTAL_BE, WBPortalBlockEntity::tick);
	}

	@Override
	public boolean canBucketPlace(BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION_BOX;
	}

	@Override
	public ItemStack getPickStack(BlockView p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_)
	{
		return ItemStack.EMPTY;
	}

	@SuppressWarnings("resource")
	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos position, Entity entity)
	{
		BlockEntity blockEntityOriginal = world.getBlockEntity(position);
		if (blockEntityOriginal instanceof WBPortalBlockEntity)
		{
			WBPortalBlockEntity wbBlockEntity = (WBPortalBlockEntity) blockEntityOriginal;

			if (!world.isClient &&
					!wbBlockEntity.isCoolingDown() &&
					!entity.hasVehicle() &&
					!entity.hasPassengers() &&
					entity.canUsePortals() &&
					VoxelShapes.matchesAnywhere(
							VoxelShapes.cuboid(entity.getBoundingBox().offset(
									(-position.getX()),
									(-position.getY()),
									(-position.getZ()))),
							COLLISION_BOX,
							BooleanBiFunction.AND))
			{
				//gets the world in the destination dimension
				MinecraftServer minecraftServer = entity.getServer(); // the server itself

				assert minecraftServer != null;
				ServerWorld destinationWorld = minecraftServer.getWorld(world.getRegistryKey().equals(WBIdentifiers.WB_WORLD_KEY) ? World.OVERWORLD : WBIdentifiers.WB_WORLD_KEY);
				ServerWorld originalWorld = minecraftServer.getWorld(entity.world.getRegistryKey());

				if(destinationWorld == null) return;
				BlockPos destPos = null;

				//looks for portal blocks in other dimension
				//within a 9x256x9 area
				boolean portalOrChestFound = false;
				for (BlockPos blockpos : BlockPos.iterate(position.add(-4, -position.getY(), -4), position.add(4, 255 - position.getY(), 4)))
				{
					Block blockNearTeleport = destinationWorld.getBlockState(blockpos).getBlock();

					if (blockNearTeleport == WBBlocks.WORLD_BLENDER_PORTAL)
					{
						//gets portal block closest to players original xz coordinate
						if (destPos == null || (Math.abs(blockpos.getX() - position.getX()) < Math.abs(destPos.getX() - position.getX()) && Math.abs(blockpos.getZ() - position.getZ()) < Math.abs(destPos.getZ() - position.getZ())))
							destPos = blockpos.toImmutable();

						portalOrChestFound = true;

						//make portals have a cooldown after being teleported to
						BlockEntity blockEntity = destinationWorld.getBlockEntity(blockpos);
						if(blockEntity instanceof WBPortalBlockEntity){
							((WBPortalBlockEntity)blockEntity).triggerCooldown();
						}

						continue;
					}


					// We check if the block entity class itself has 'chest in the name.
					// Cache the result and only count the block entity if it is a chest.
					BlockEntity blockEntity = destinationWorld.getBlockEntity(blockpos);
					if(blockEntity == null || blockNearTeleport instanceof Inventory) continue;

					if (WBPortalSpawning.VALID_CHEST_BLOCKS_ENTITY_TYPES.getOrDefault(blockEntity.getType(), false))
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
					BlockPos motionBlockPosition = destinationWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, position);
					BlockPos worldSurfacePosition = destinationWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE, position);
					destPos = motionBlockPosition.getY() > worldSurfacePosition.getY() ? motionBlockPosition : worldSurfacePosition;

					//places a portal block in World Blender so player can escape if
					//there is no portal block and then makes it be in cooldown
					if (destinationWorld.getRegistryKey().equals(WBIdentifiers.WB_WORLD_KEY))
					{
						// prevents portal over void killing player
						if(destPos.getY() == destinationWorld.getBottomY()){
							destinationWorld.setBlockState(destPos, Blocks.STONE.getDefaultState(), 3);
							destPos = destPos.up();
						}

						destinationWorld.setBlockState(destPos, Blocks.AIR.getDefaultState());
						destinationWorld.setBlockState(destPos.up(), Blocks.AIR.getDefaultState());
						
						destinationWorld.setBlockState(destPos, WBBlocks.WORLD_BLENDER_PORTAL.getDefaultState());
						BlockEntity blockEntity = destinationWorld.getBlockEntity(destPos);
						if(blockEntity instanceof WBPortalBlockEntity){
							((WBPortalBlockEntity)blockEntity).triggerCooldown();
						}
					}
				}

				wbBlockEntity.teleportEntity(entity, destPos, destinationWorld, originalWorld);
			}
		}
	}


	/**
	 * Turns this portal blocks to air when right clicked while crouching
	 */
	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult rayTrace)
	{
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if(playerEntity.isInSneakingPose() &&
				blockEntity instanceof WBPortalBlockEntity &&
				((WBPortalBlockEntity)blockEntity).isRemoveable())
		{
			if (world.isClient) {
				//show lots of particles when portal is removed on client
				createLotsOfParticles(blockState, world, blockPos, world.random);
			}
			else {
				//remove this portal on server side
				world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
			}
			return ActionResult.SUCCESS;
		}
		
		return ActionResult.FAIL;
	}


	/**
	 * Shows particles around this block
	 */
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos position, Random random)
	{
		BlockEntity tileentity = world.getBlockEntity(position);
		if (tileentity instanceof WBPortalBlockEntity)
		{
			if (random.nextFloat() < 0.09f)
			{
				spawnParticle(world, position, random);
			}

		}
	}

	
	public void createLotsOfParticles(BlockState blockState, World world, BlockPos position, Random random)
	{
		BlockEntity tileentity = world.getBlockEntity(position);
		if (tileentity instanceof WBPortalBlockEntity)
		{
			for(int i = 0; i < 50; i++) 
			{
				spawnParticle(world, position, random);
			}
		}
	}

	private void spawnParticle(World world, BlockPos position, Random random) {
		double xPos = (double) position.getX() + (double) random.nextFloat();
		double yPos = (double) position.getY() + (double) random.nextFloat();
		double zPos = (double) position.getZ() + (double) random.nextFloat();
		double xVelocity = (random.nextFloat() - 0.5D) * 0.08D;
		double yVelocity = (random.nextFloat() - 0.5D) * 0.13D;
		double zVelocity = (random.nextFloat() - 0.5D) * 0.08D;

		world.addParticle(ParticleTypes.END_ROD, xPos, yPos, zPos, xVelocity, yVelocity, zVelocity);
	}

}
