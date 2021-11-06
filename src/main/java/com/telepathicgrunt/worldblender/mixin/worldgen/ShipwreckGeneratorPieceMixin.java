package com.telepathicgrunt.worldblender.mixin.worldgen;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.dimension.WBBiomeProvider;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Random;


@Mixin(ShipwreckGenerator.Piece.class)
public class ShipwreckGeneratorPieceMixin {

    /**
     * @author TelepathicGrunt
     * @reason Prevent structures from being placed at world bottom if disallowed in World Blender's config
     */
    @Inject(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/StructureWorldAccess;getTopY(Lnet/minecraft/world/Heightmap$Type;II)I", ordinal = 0),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void worldblender_disableHeightmapSnap(StructureWorldAccess world, StructureAccessor structureAccessor,
                                                   ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox,
                                                   ChunkPos chunkPos, BlockPos pos, CallbackInfoReturnable<Boolean> cir,
                                                   int i, int j)
    {
        if(WorldBlender.WB_CONFIG.WBDimensionConfig.removeWorldBottomStructures &&
                world.toServerWorld().getChunkManager().getChunkGenerator().getBiomeSource() instanceof WBBiomeProvider &&
                j <= world.getBottomY())
        {
            cir.setReturnValue(false);
        }
    }

    /**
     * @author TelepathicGrunt
     * @reason Prevent structures from being placed at world bottom if disallowed in World Blender's config
     */
    @Inject(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/StructureWorldAccess;getTopY(Lnet/minecraft/world/Heightmap$Type;II)I", ordinal = 1),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void worldblender_disableHeightmapSnap2(StructureWorldAccess world, StructureAccessor structureAccessor,
                                                    ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox,
                                                    ChunkPos chunkPos, BlockPos pos, CallbackInfoReturnable<Boolean> cir,
                                                    int i, int j, Vec3i vec3i, Heightmap.Type type, int k, BlockPos blockPos,
                                                    Iterator<BlockPos> var14, BlockPos blockPos2, int l)
    {
        if(WorldBlender.WB_CONFIG.WBDimensionConfig.removeWorldBottomStructures &&
                world.toServerWorld().getChunkManager().getChunkGenerator().getBiomeSource() instanceof WBBiomeProvider &&
                l <= world.getBottomY())
        {
            cir.setReturnValue(false);
        }
    }
}
