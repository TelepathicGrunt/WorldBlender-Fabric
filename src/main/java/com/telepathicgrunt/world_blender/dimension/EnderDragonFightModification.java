package com.telepathicgrunt.world_blender.dimension;

import com.telepathicgrunt.world_blender.WBIdentifiers;
import com.telepathicgrunt.world_blender.blocks.WBPortalBlockEntity;
import com.telepathicgrunt.world_blender.mixin.EnderDragonFightAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

public class EnderDragonFightModification {

    /*
     * Needed so that the portal check does not recognize a pattern in the
     * Bedrock floor of World Blender's dimension as if it is an End Podium.
     *
     * This was the cause of End Podium and Altar not spawning in WB dimension randomly.
     */
    public static BlockPattern.Result findEndPortal(EnderDragonFight enderDragonFight, BlockPattern.Result blockPattern) {
        if(((EnderDragonFightAccessor)enderDragonFight).wb_getworld().getRegistryKey().equals(WBIdentifiers.WB_WORLD_KEY)){
            WorldChunk worldChunk = ((EnderDragonFightAccessor)enderDragonFight).wb_getworld().getChunk(0, 0);

            for(BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
                if (blockEntity instanceof WBPortalBlockEntity) {
                    if(!((WBPortalBlockEntity) blockEntity).isRemoveable()){
                        BlockPattern.Result blockpattern = ((EnderDragonFightAccessor)enderDragonFight).wb_getendPortalPattern().searchAround(((EnderDragonFightAccessor)enderDragonFight).wb_getworld(), blockEntity.getPos());
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

            return null; // Skip checking the bedrock layer
        }

        return blockPattern;
    }
}
