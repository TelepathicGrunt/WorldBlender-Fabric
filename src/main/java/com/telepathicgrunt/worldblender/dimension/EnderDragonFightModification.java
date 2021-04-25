package com.telepathicgrunt.worldblender.dimension;

import com.telepathicgrunt.worldblender.WBIdentifiers;
import com.telepathicgrunt.worldblender.blocks.WBPortalBlockEntity;
import com.telepathicgrunt.worldblender.mixin.dimensions.EnderDragonFightAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class EnderDragonFightModification {

    /*
     * Needed so that the portal check does not recognize a pattern in the
     * Bedrock floor of World Blender's dimension as if it is an End Podium.
     *
     * This was the cause of End Podium and Altar not spawning in WB dimension randomly.
     */
    public static BlockPattern.Result findEndPortal(EnderDragonFight enderDragonFight, BlockPattern.Result blockPattern) {
        ServerWorld world = ((EnderDragonFightAccessor)enderDragonFight).wb_getworld();
        WorldChunk worldChunk = world.getChunk(0, 0);

        // only check above world blender's portal block at world origin for dragon podium
        for(BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
            if (blockEntity instanceof WBPortalBlockEntity) {
                if(!((WBPortalBlockEntity) blockEntity).isRemoveable()){
                    BlockPattern.Result blockpattern = ((EnderDragonFightAccessor)enderDragonFight).wb_getendPortalPattern().searchAround(world, blockEntity.getPos().add(-3, 3, -3));
                    if (blockpattern != null) {
                        BlockPos blockpos = blockpattern.translate(3, 7, 3).getBlockPos();
                        if (((EnderDragonFightAccessor)enderDragonFight).wb_getexitPortalLocation() == null && blockpos.getX() == 0 && blockpos.getZ() == 0) {
                            ((EnderDragonFightAccessor)enderDragonFight).wb_setexitPortalLocation(blockpos);
                        }

                        return blockpattern;
                    }
                }
            }
        }

        int maxY = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN).getY();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        // skip checking bedrock layer
        for(int currentY = maxY; currentY >= 10; --currentY) {
            BlockPattern.Result result2 = ((EnderDragonFightAccessor)enderDragonFight).wb_getendPortalPattern().searchAround(world, mutable.set(EndPortalFeature.ORIGIN.getX(), currentY, EndPortalFeature.ORIGIN.getZ()));
            if (result2 != null) {
                if (((EnderDragonFightAccessor)enderDragonFight).wb_getexitPortalLocation() == null) {
                    ((EnderDragonFightAccessor)enderDragonFight).wb_setexitPortalLocation(result2.translate(3, 3, 3).getBlockPos());
                }

                return result2;
            }
        }

        return null;
    }
}
