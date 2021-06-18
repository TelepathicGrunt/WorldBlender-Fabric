package com.telepathicgrunt.worldblender.dimension;

import com.telepathicgrunt.worldblender.mixin.worldgen.ChunkGeneratorAccessor;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class ChunkGeneratorBehavior {

    private static final Map<Biome, Map<StructureFeature<?>, List<ConfiguredStructureFeature<?, ?>>>> MULTIPLE_CONFIGURED_STRUCTURE_BIOMES = new Reference2ObjectOpenHashMap<>();

    public static boolean placeAllConfiguredStructures(ChunkGenerator chunkGenerator,
                                                    ConfiguredStructureFeature<?, ?> configuredStructureFeature,
                                                    DynamicRegistryManager dynamicRegistryManager,
                                                    StructureAccessor structureAccessor, Chunk chunk,
                                                    StructureManager structureManager, long worldSeed,
                                                    ChunkPos chunkPos, Biome biome)
    {
        // Need to create list of structures for this biome
        if(!MULTIPLE_CONFIGURED_STRUCTURE_BIOMES.containsKey(biome)){
            MULTIPLE_CONFIGURED_STRUCTURE_BIOMES.put(biome, new Reference2ObjectOpenHashMap<>());
            Map<StructureFeature<?>, List<ConfiguredStructureFeature<?, ?>>> structureMap = MULTIPLE_CONFIGURED_STRUCTURE_BIOMES.get(biome);

            // Stores all configuredforms of a structures in a list for random access later
            for(Supplier<ConfiguredStructureFeature<?, ?>> supplierCS : biome.getGenerationSettings().getStructureFeatures()){
                StructureFeature<?> structure = supplierCS.get().feature;
                if(!structureMap.containsKey(structure)){
                    structureMap.put(structure, new ArrayList<>());
                }
                structureMap.get(structure).add(supplierCS.get());
            }
        }


        if(MULTIPLE_CONFIGURED_STRUCTURE_BIOMES.get(biome).containsKey(configuredStructureFeature.feature)){
            StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), configuredStructureFeature.feature, chunk);
            int ref = structureStart != null ? structureStart.getReferences() : 0;
            StructureConfig structureConfig = chunkGenerator.getStructuresConfig().getForType(configuredStructureFeature.feature);
            if (structureConfig != null) {
                List<ConfiguredStructureFeature<?, ?>> randomStructureList = MULTIPLE_CONFIGURED_STRUCTURE_BIOMES.get(biome).get(configuredStructureFeature.feature);

                Random random = new Random();
                random.setSeed(worldSeed + chunkPos.toLong());

                StructureStart<?> structureStart2 = randomStructureList.get(random.nextInt(randomStructureList.size()))
                        .tryPlaceStart(
                            dynamicRegistryManager,
                            chunkGenerator,
                            ((ChunkGeneratorAccessor)chunkGenerator).worldblender_getPopulationSource(),
                            structureManager,
                            worldSeed,
                            chunkPos,
                            biome,
                            ref,
                            structureConfig,
                            chunk);

                structureAccessor.setStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), configuredStructureFeature.feature, structureStart2, chunk);
            }

            return true;
        }

        return false;
    }

}
