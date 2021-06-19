package com.telepathicgrunt.worldblender.mixin.worldgen;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.dimension.WBBiomeProvider;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;


@Mixin(Structure.class)
public class StructureMixin {

    /**
     * @author TelepathicGrunt
     * @reason Prevent template structures from being placed at world bottom if allowed in World Blender's config
     */
    @Inject(
            method = "place(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/structure/StructurePlacementData;Ljava/util/Random;I)Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void worldblender_removeWorldBottomStructures(ServerWorldAccess world, BlockPos pos, BlockPos pivot,
                                                  StructurePlacementData placementData, Random random, int i,
                                                  CallbackInfoReturnable<Boolean> cir)
    {
        if(WorldBlender.WB_CONFIG.WBDimensionConfig.removeWorldBottomStructures &&
            world.toServerWorld().getChunkManager().getChunkGenerator().getBiomeSource() instanceof WBBiomeProvider &&
            pos.getY() <= world.getBottomY())
        {
            cir.setReturnValue(true);
        }
    }
}
