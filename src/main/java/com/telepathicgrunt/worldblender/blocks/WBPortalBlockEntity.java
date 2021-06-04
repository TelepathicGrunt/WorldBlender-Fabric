package com.telepathicgrunt.worldblender.blocks;

import com.telepathicgrunt.worldblender.WBIdentifiers;
import com.telepathicgrunt.worldblender.mixin.blocks.BlockAccessor;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class WBPortalBlockEntity extends BlockEntity implements Tickable {
    private float teleportCooldown = 300;
    private boolean removeable = true;

    // Culling optimization by Comp500
    // https://github.com/comp500/PolyDungeons/blob/master/src/main/java/polydungeons/block/entity/DecorativeEndBlockEntity.java
    private final Direction[] FACINGS = Direction.values();
    private int cachedCullFaces = 0;
    private boolean hasCachedFaces = false;


    public WBPortalBlockEntity() {
        super(WBBlocks.WORLD_BLENDER_PORTAL_BE);
    }


    @Override
    public void tick() {
        boolean isCoolingDown = this.isCoolingDown();
        if (isCoolingDown) {
            --this.teleportCooldown;
        }

        if (isCoolingDown != this.isCoolingDown()) {
            this.markDirty();
        }
    }


    public void teleportEntity(Entity entity, BlockPos destPos, ServerWorld destinationWorld, ServerWorld originalWorld) {
        this.triggerCooldown();

        if (entity instanceof PlayerEntity) {
            ((ServerPlayerEntity) entity).teleport(
                    destinationWorld,
                    destPos.getX() + 0.5D,
                    destPos.getY() + 1D,
                    destPos.getZ() + 0.5D,
                    entity.yaw,
                    entity.pitch);
        }
        else {
            Entity entity2 = entity.getType().create(destinationWorld);
            if (entity2 != null) {
                entity2.copyFrom(entity);
                entity2.refreshPositionAndAngles(destPos, entity.yaw, entity.pitch);
                entity2.setVelocity(entity.getVelocity());
                destinationWorld.onDimensionChanged(entity2);
            }
            entity.remove();
            assert this.world != null;
            this.world.getProfiler().pop();
            originalWorld.resetIdleTimeout();
            destinationWorld.resetIdleTimeout();
            this.world.getProfiler().pop();
        }
    }

    public boolean isCoolingDown() {
        return this.teleportCooldown > 0;
    }

    public float getCoolDown() {
        return this.teleportCooldown;
    }

    public void setCoolDown(float cooldown) {
        this.teleportCooldown = cooldown;
    }

    public void triggerCooldown() {
        if (this.world == null || this.world.isClient()) return;

        this.teleportCooldown = 300;
        this.markDirty();
        this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);

        // Send cooldown to client to display visually
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(this.pos);
        passedData.writeFloat(this.getCoolDown());

        PlayerLookup.world((ServerWorld)world).forEach(player ->
                ServerPlayNetworking.send(player, WBIdentifiers.PORTAL_COOLDOWN_PACKET_ID, passedData));
    }

    public boolean isRemoveable() {
        return this.removeable;
    }

    public void makeNotRemoveable() {
        this.removeable = false;
        this.markDirty();
    }

    @Override
    public CompoundTag toTag(CompoundTag data) {
        super.toTag(data);
        data.putFloat("Cooldown", this.teleportCooldown);
        data.putBoolean("Removeable", this.removeable);
        return data;
    }


    @Override
    public void fromTag(BlockState blockState, CompoundTag data) {
        super.fromTag(blockState, data);
        if (data.contains("Cooldown")) {
            this.teleportCooldown = data.getFloat("Cooldown");
        }
        else {
            this.teleportCooldown = 300; //if this is missing cooldown entry, have it start with a cooldown
        }

        this.removeable = data.getBoolean("Removeable");
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For modded
     * TE's, this packet comes back to you clientside
     */
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 0, this.toInitialChunkDataTag());
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when many
     * blocks change at once. This compound comes back to you clientside
     */
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }


    @Environment(EnvType.CLIENT)
    public boolean shouldRenderFace(Direction direction) {
        return shouldDrawSide(direction);
    }

    @Deprecated
    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 65536.0D;
    }


    @Environment(EnvType.CLIENT)
    public void updateCullFaces() {
        assert world != null;
        hasCachedFaces = true;
        int mask;
        for (Direction dir : FACINGS) {
            mask = 1 << dir.getId();
            if (shouldDrawSideSpecialized(getCachedState(), world, getPos(), dir)) {
                cachedCullFaces |= mask;
            }
            else {
                cachedCullFaces &= ~mask;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean shouldDrawSide(Direction direction) {
        // Cull faces that are not visible
        if (!hasCachedFaces) {
            updateCullFaces();
        }
        return (cachedCullFaces & (1 << direction.getId())) != 0;
    }

    @Environment(EnvType.CLIENT)
    public static void updateCullCache(BlockPos pos, World world) {
        updateCullCacheNeighbor(pos.up(), world);
        updateCullCacheNeighbor(pos.down(), world);
        updateCullCacheNeighbor(pos.north(), world);
        updateCullCacheNeighbor(pos.east(), world);
        updateCullCacheNeighbor(pos.south(), world);
        updateCullCacheNeighbor(pos.west(), world);
    }

    @Environment(EnvType.CLIENT)
    public static void updateCullCacheNeighbor(BlockPos pos, World world) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof WBPortalBlockEntity) {
            ((WBPortalBlockEntity) be).updateCullFaces();
        }
    }

    /**
     * Need out own implementation because the original method uses getOutlineShape to extrude and cause
     * our block's sides to be culled for snow and slabs. We need getOutlineShape so we can right click the block.
     */
    @Environment(EnvType.CLIENT)
    public static boolean shouldDrawSideSpecialized(BlockState state, BlockView world, BlockPos pos, Direction facing) {
        BlockPos blockPos = pos.offset(facing);
        BlockState blockState = world.getBlockState(blockPos);
        // Do not draw side for our block if bordering our own block.
        if (state.isSideInvisible(blockState, facing) || blockState.getBlock() == WBBlocks.WORLD_BLENDER_PORTAL) {
            return false;
        }
        else if (blockState.isOpaque()) {
            Block.NeighborGroup neighborGroup = new Block.NeighborGroup(state, blockState, facing);
            Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = BlockAccessor.wb_getFACE_CULL_MAP().get();
            byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
            if (b != 127) {
                return b != 0;
            }
            else {
                VoxelShape voxelShape = VoxelShapes.fullCube(); // No extrusions for our block.
                VoxelShape voxelShape2 = blockState.getCullingFace(world, blockPos, facing.getOpposite());
                boolean bl = VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
                if (object2ByteLinkedOpenHashMap.size() == 2048) {
                    object2ByteLinkedOpenHashMap.removeLastByte();
                }

                object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte) (bl ? 1 : 0));
                return bl;
            }
        }
        else {
            return true;
        }
    }
}
