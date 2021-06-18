package com.telepathicgrunt.worldblender.entities;

import com.telepathicgrunt.worldblender.mixin.blocks.AbstractRailBlockInvoker;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;

public class ItemClearingEntity extends Entity {
   private int ticksTillDetonation;
   private final int tickCountdownStart = 75;

   public ItemClearingEntity(World worldIn) {
      this(WBEntities.ITEM_CLEARING_ENTITY, worldIn);
   }

   public ItemClearingEntity(EntityType<? extends ItemClearingEntity> type, World worldIn) {
      super(type, worldIn);
      ticksTillDetonation = tickCountdownStart;
   }

   @Override
   @SuppressWarnings("unchecked")
   public EntityType<? extends ItemClearingEntity> getType() {
      return (EntityType<? extends ItemClearingEntity>) super.getType();
   }

   @Override
   protected void initDataTracker() {}

   @Override
   public Packet<?> createSpawnPacket() {
      return new EntitySpawnS2CPacket(this);
   }

   @Override
   public void writeCustomDataToNbt(NbtCompound compound) {
      compound.putInt("ticksTillDetonation", this.ticksTillDetonation);
   }

   @Override
   public void readCustomDataFromNbt(NbtCompound compound) {
      this.ticksTillDetonation = compound.getInt("ticksTillDetonation");
      if(this.ticksTillDetonation == 0) this.ticksTillDetonation = tickCountdownStart;
   }

   @Override
   public void tick() {
      if (this.world instanceof ServerWorld serverWorld) {
         if (ticksTillDetonation > 0) {
            // Force blocks to update themselves and tick so they break
            if (ticksTillDetonation == tickCountdownStart - 2) {
               BlockPos.Mutable mutable = new BlockPos.Mutable();
               ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
               Chunk chunk = serverWorld.getChunk(this.getChunkPos().getStartPos());
               BlockPos chunkBlockPos = chunk.getPos().getStartPos();

               // for compat with cubic chunks
               for (ChunkSection section : chunk.getSectionArray()) {
                  if(section == null) continue;
                  int bottomY = section.getYOffset();
                  if (bottomY > chunkGenerator.getWorldHeight() || bottomY < chunkGenerator.getMinimumY()) continue;

                  for (int x = 0; x < 16; x++) {
                     for (int z = 0; z < 16; z++) {
                        for (int y = bottomY; y < bottomY + 16; y++) {
                           BlockState currentState = chunk.getBlockState(mutable.set(chunkBlockPos, x, y, z));

                           // Special case as rails return themselves from getValidBlockForPosition when they shouldn't. Rails bad
                           if (currentState.getBlock() instanceof AbstractRailBlock railBlock) {
                              RailShape railShape = currentState.get(railBlock.getShapeProperty());
                              boolean invalidSpot = AbstractRailBlockInvoker.worldblender_callShouldDropRail(mutable, serverWorld, railShape);
                              if (invalidSpot) {
                                 serverWorld.removeBlock(mutable, false);
                              }
                              continue;
                           }

                           // Skip air, full solid cubes, and liquid blocks as those typically do not break themselves.
                           if (!currentState.isAir() &&
                                   !(currentState.getBlock() instanceof FluidBlock) &&
                                   !(currentState.getMaterial().blocksLight() && currentState.isOpaqueFullCube(serverWorld, mutable))) {
                              BlockState newState = Block.postProcessState(currentState, serverWorld, mutable);
                              if (currentState != newState) {
                                 // removes all invalid placed blocks like floating grass or rails
                                 serverWorld.setBlockState(mutable, newState, 3);
                              }
                              // Skip dripstone as it will fall if we tick it which is bad.
                              else if (!(currentState.getBlock() instanceof PointedDripstoneBlock)) {
                                 // forces blocks like leaves or twisting vine to self-destruct
                                 currentState.scheduledTick(serverWorld, mutable, this.random);
                                 currentState.randomTick(serverWorld, mutable, this.random);
                              }
                           }
                        }
                     }
                  }
               }
            }

            // count down
            ticksTillDetonation--;
         }

         // NUKE ALL THE ITEMS NOW
         else {
            ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
            Chunk chunk = serverWorld.getChunk(this.getChunkPos().getStartPos());
            // for compat with cubic chunks
            for (ChunkSection section : chunk.getSectionArray()) {
               if(section == null) continue;
               int bottomY = section.getYOffset();
               if (bottomY > chunkGenerator.getWorldHeight() || bottomY < chunkGenerator.getMinimumY()) continue;

               Box box = new Box(
                       this.getChunkPos().getStartX(),
                       bottomY,
                       this.getChunkPos().getStartZ(),
                       this.getChunkPos().getEndX(),
                       bottomY + 16,
                       this.getChunkPos().getEndZ()
               );
               List<Entity> entityList = serverWorld.getOtherEntities(this, box);

               // Clear the chunk of all ItemEntities
               for (Entity entity : entityList) {
                  if (entity.getType().equals(EntityType.ITEM)) {
                     entity.discard(); // Will be removed automatically on next world tick
                  }
               }
            }
            this.discard(); // remove self as task is done
         }
      }
   }
}