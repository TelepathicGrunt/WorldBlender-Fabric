package com.telepathicgrunt.worldblender.mixin.worldgen;

import com.telepathicgrunt.worldblender.dimension.WBBiomeProvider;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(OceanMonumentFeature.class)
public class OceanMonumentStructureMixin {

    /**
     * @author TelepathicGrunt
     * @reason make Ocean Monuments skip their RIVER/OCEAN category checks if in World Blender's biome provider. Otherwise, Monuments don't spawn. Mojank lmao
     */
    @Inject(
            method = "shouldStartAt(Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/source/BiomeSource;JLnet/minecraft/world/gen/ChunkRandom;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;Lnet/minecraft/world/HeightLimitView;)Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void worldblender_modifyBiomeRegistry(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed,
                                                  ChunkRandom chunkRandom, ChunkPos chunkPos, Biome biome,
                                                  ChunkPos chunkPos2, DefaultFeatureConfig defaultFeatureConfig,
                                                  HeightLimitView heightLimitView, CallbackInfoReturnable<Boolean> cir)
    {
        if(biomeSource instanceof WBBiomeProvider){
            cir.setReturnValue(true);
        }
    }
}
