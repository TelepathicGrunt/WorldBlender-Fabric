package com.telepathicgrunt.worldblender.mixin.worldgen;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.dimension.WBBiomeProvider;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceWithDimensions;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(StructurePieceWithDimensions.class)
public abstract class StructurePieceWithDimensionsMixin extends StructurePiece {

    protected StructurePieceWithDimensionsMixin(StructurePieceType type, int length, BlockBox boundingBox) {
        super(type, length, boundingBox);
    }

    /**
     * @author TelepathicGrunt
     * @reason Prevent structures from being placed at world bottom if disallowed in World Blender's config
     */
    @Inject(
            method = "method_14839(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockBox;I)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;getTopPosition(Lnet/minecraft/world/Heightmap$Type;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void worldblender_disableHeightmapSnap(WorldAccess world, BlockBox boundingBox, int height, CallbackInfoReturnable<Boolean> cir,
                                                   int j, int k, BlockPos.Mutable mutable)
    {
        if(WorldBlender.WB_CONFIG.WBDimensionConfig.removeWorldBottomStructures &&
                world instanceof ChunkRegion &&
                ((ChunkRegion)world).toServerWorld().getChunkManager().getChunkGenerator().getBiomeSource() instanceof WBBiomeProvider &&
                world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY() <= world.getBottomY())
        {
            cir.setReturnValue(false);
        }
    }
}
