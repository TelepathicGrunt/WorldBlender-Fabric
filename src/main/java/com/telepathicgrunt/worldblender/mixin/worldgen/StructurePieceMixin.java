package com.telepathicgrunt.worldblender.mixin.worldgen;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.dimension.WBBiomeProvider;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(StructurePiece.class)
public class StructurePieceMixin {

    @Shadow
    protected int applyXTransform(int x, int z) {
        return 0;
    }

    @Shadow
    protected int applyZTransform(int x, int z) {
        return 0;
    }

    /**
     * @author TelepathicGrunt
     * @reason Prevent some structures from placing pillars if disallowed in World Blender's config
     */
    @Inject(
            method = "fillDownwards(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void worldblender_disablePillars(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box, CallbackInfo ci)
    {
        if(WorldBlender.WB_CONFIG.WBDimensionConfig.removeStructurePillars &&
            world.toServerWorld().getChunkManager().getChunkGenerator().getBiomeSource() instanceof WBBiomeProvider)
        {
            int heightmapY = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, applyXTransform(x, z), applyZTransform(x, z));
            if(heightmapY <= world.getBottomY() + 2){
                ci.cancel();
            }
        }
    }
}
