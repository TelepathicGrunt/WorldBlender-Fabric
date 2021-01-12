package com.telepathicgrunt.world_blender.mixin;

import com.telepathicgrunt.world_blender.dimension.WBBiomeProvider;
import net.minecraft.util.math.ChunkPos;
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
            method = "shouldStartAt(Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/source/BiomeSource;JLnet/minecraft/world/gen/ChunkRandom;IILnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void modifyBiomeRegistry(ChunkGenerator chunkGenerator, BiomeSource biomeSource,
                                     long seed, ChunkRandom random,
                                     int chunkX, int chunkZ, Biome biome,
                                     ChunkPos chunkPos, DefaultFeatureConfig config,
                                     CallbackInfoReturnable<Boolean> cir) 
    {
        if(biomeSource instanceof WBBiomeProvider){
            for(Biome neighboringBiome : biomeSource.getBiomesInArea(chunkX * 16 + 9, chunkGenerator.getSeaLevel(), chunkZ * 16 + 9, 16)) {
                if (!neighboringBiome.getGenerationSettings().hasStructureFeature((OceanMonumentFeature)(Object)this)) {
                    cir.setReturnValue(false);
                }
            }
            cir.setReturnValue(true);
        }
    }
}
