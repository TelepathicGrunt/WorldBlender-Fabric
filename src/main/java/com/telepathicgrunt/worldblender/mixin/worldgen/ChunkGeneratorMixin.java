package com.telepathicgrunt.worldblender.mixin.worldgen;

import com.telepathicgrunt.worldblender.dimension.ChunkGeneratorBehavior;
import com.telepathicgrunt.worldblender.dimension.WBBiomeProvider;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    @Shadow
    public abstract BiomeSource getBiomeSource();

    /**
     * Picks a random ConfiguredStructure if WB biome has a Structure with multiple forms in it.
     * @author TelepathicGrunt
     * @reason Prevents multiple ConfiguredStructures with same Structure base in a biome spawning only 1 as a result
     */
    @Inject(
            method = "setStructureStart(Lnet/minecraft/world/gen/feature/ConfiguredStructureFeature;Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/structure/StructureManager;JLnet/minecraft/world/biome/Biome;)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void worldblender_generateAllConfiguredStructures(ConfiguredStructureFeature<?, ?> configuredStructureFeature,
                                                              DynamicRegistryManager dynamicRegistryManager,
                                                              StructureAccessor accessor, Chunk chunk,
                                                              StructureManager structureManager,
                                                              long worldSeed, Biome biome, CallbackInfo ci) {
        if (getBiomeSource() instanceof WBBiomeProvider) {
            if (ChunkGeneratorBehavior.placeAllConfiguredStructures(
            		((ChunkGenerator) (Object) this), configuredStructureFeature, dynamicRegistryManager,
                    accessor, chunk, structureManager, worldSeed, chunk.getPos(), biome))
            {
                ci.cancel();
            }
        }
    }
}
