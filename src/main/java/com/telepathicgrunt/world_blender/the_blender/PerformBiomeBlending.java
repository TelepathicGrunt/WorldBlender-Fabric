package com.telepathicgrunt.world_blender.the_blender;

import com.google.common.collect.ImmutableList;
import com.telepathicgrunt.world_blender.biome.WBBiomes;
import com.telepathicgrunt.world_blender.biome.biomes.surfacebuilder.BlendedSurfaceBuilder;
import com.telepathicgrunt.world_blender.configs.WBConfig;
import com.telepathicgrunt.world_blender.features.WBFeatures;
import com.telepathicgrunt.world_blender.the_blender.ConfigBlacklisting.BlacklistType;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnEntry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;


public class PerformBiomeBlending {
    public static void setupBiomes() {
        FeatureGrouping.setupFeatureMaps();
        BlendedSurfaceBuilder.resetSurfaceList();

        // add end spike directly to all biomes if not directly blacklisted. Turning off vanilla features will not prevent end spikes from spawning due to them marking the world origin nicely
        if (!ConfigBlacklisting.isResourceLocationBlacklisted(BlacklistType.FEATURE, new Identifier("minecraft:end_spike")))
            WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Feature.END_SPIKE.configure(new EndSpikeFeatureConfig(false, ImmutableList.of(), null)).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))));


        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            // ignore our own biomes to speed things up and prevent possible duplications
            if (WBBiomes.biomes.contains(biome))
                continue;

                // if the biome is a vanilla biome but config says no vanilla biome, skip this biome
            else if (ForgeRegistries.BIOMES.getKey(biome).getNamespace().equals("minecraft") && !WBConfig.allowVanillaBiomeImport)
                continue;

                // if the biome is a modded biome but config says no modded biome, skip this biome
            else if (!WBConfig.allowModdedBiomeImport)
                continue;

                // blacklisted by blanket list
            else if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.BLANKET, biome.getRegistryName()))
                continue;


            /////////// FEATURES//////////////////
            addBiomeFeatures(biome);

            ////////////////////// STRUCTURES////////////////////////
            addBiomeStructures(biome);

            //////////////////////// CARVERS/////////////////////////
            addBiomeCarvers(biome);

            //////////////////////// SPAWNER/////////////////////////
            addBiomeNaturalMobs(biome);

            //////////////////////// SURFACE/////////////////////////
            addBiomeSurfaceConfig(biome);
        }


        ////////// Misc Features///////////////
        // Add these only after we have finally gone through all biomes

        // add grass, flower, and other small plants now so they are generated second to last
        for (GenerationStep.Feature stage : GenerationStep.Feature.values()) {
            for (ConfiguredFeature<?, ?> grassyFlowerFeature : FeatureGrouping.SMALL_PLANT_MAP.get(stage)) {
                if (WBBiomes.BLENDED_BIOME.getFeaturesForStep(stage).stream().noneMatch(addedConfigFeature -> FeatureGrouping.serializeAndCompareFeature(addedConfigFeature, grassyFlowerFeature))) {
                    WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(stage, grassyFlowerFeature));
                }
            }
        }


        if (!WBConfig.disallowLaggyFeatures && FeatureGrouping.bambooFound) {
            // add bamboo so it is dead last
            WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.BAMBOO.configure(new ProbabilityConfig(0.2F)).createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0D, 0.3D, Heightmap.Type.WORLD_SURFACE_WG)))));
        }


        // Makes carvers be able to carve any underground blocks including netherrack, end stone, and modded blocks
        if (WBConfig.carversCanCarveMoreBlocks) {
            Set<Block> allBlocksToCarve = BlendedSurfaceBuilder.blocksToCarve();
            for (Carver carverStage : GenerationStep.Carver.values()) {
                for (ConfiguredCarver<?> carver : WBBiomes.BLENDED_BIOME.getCarversForStep(carverStage)) {
                    allBlocksToCarve.addAll(carver.carver.alwaysCarvableBlocks);
                    carver.carver.alwaysCarvableBlocks = allBlocksToCarve;
                }
            }
        }

        // add this last so that this can contain other local modification feature's liquids/falling blocks better
        if (!ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, new Identifier("world_blender:no_floating_liquids_or_falling_blocks"))) {
            WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(net.minecraft.world.gen.GenerationStep.Feature.LOCAL_MODIFICATIONS, WBFeatures.NO_FLOATING_LIQUIDS_OR_FALLING_BLOCKS.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))));
        }
        if (!ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, new Identifier("world_blender:separate_lava_and_water"))) {
            WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(net.minecraft.world.gen.GenerationStep.Feature.LOCAL_MODIFICATIONS, WBFeatures.SEPARATE_LAVA_AND_WATER.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))));
        }


        // free up memory when we are done.
        FeatureGrouping.clearFeatureMaps();
        WBBiomes.VANILLA_TEMP_BIOME = null;
    }


    private static void addBiomeFeatures(Biome biome) {
        for (net.minecraft.world.gen.GenerationStep.Feature stage : GenerationStep.Feature.values()) {
            for (ConfiguredFeature<?, ?> configuredFeature : biome.getFeaturesForStep(stage)) {
                if (WBBiomes.BLENDED_BIOME.getFeaturesForStep(stage).stream().noneMatch(addedConfigFeature -> FeatureGrouping.serializeAndCompareFeature(addedConfigFeature, configuredFeature))) {
                    ///////// Check feature blacklist from config
                    if (configuredFeature.config instanceof DecoratedFeatureConfig) {
                        ConfiguredFeature<?, ?> insideFeature = ((DecoratedFeatureConfig) configuredFeature.config).feature;

                        if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, insideFeature.feature.getRegistryName())) {
                            continue;
                        }


                        // A bunch of edge cases that have to handled because features can hold other features.
                        // If a mod makes a custom feature to hold features, welp, we are screwed. Nothing we can do about it.
                        if (insideFeature.feature == Feature.RANDOM_RANDOM_SELECTOR) {
                            if (((RandomRandomFeatureConfig) insideFeature.config).features.stream().anyMatch(buriedFeature -> ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, buriedFeature.feature.getRegistryName()))) {
                                continue;
                            }
                        }

                        if (insideFeature.feature == Feature.RANDOM_SELECTOR) {
                            if (((RandomFeatureConfig) insideFeature.config).features.stream().anyMatch(buriedFeature -> ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, buriedFeature.feature.feature.getRegistryName()))) {
                                continue;
                            }
                        }

                        if (insideFeature.feature == Feature.SIMPLE_RANDOM_SELECTOR) {
                            if (((SimpleRandomFeatureConfig) insideFeature.config).features.stream().anyMatch(buriedFeature -> ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, buriedFeature.feature.getRegistryName()))) {
                                continue;
                            }
                        }

                        if (insideFeature.feature == Feature.RANDOM_BOOLEAN_SELECTOR) {
                            if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, ((RandomBooleanFeatureConfig) insideFeature.config).featureTrue.feature.getRegistryName()) || ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, ((RandomBooleanFeatureConfig) insideFeature.config).featureFalse.feature.getRegistryName())) {
                                continue;
                            }
                        }
                    }


                    //// begin adding features//////

                    // check if feature is already added
                    if (WBBiomes.VANILLA_TEMP_BIOME.getFeaturesForStep(stage).stream().anyMatch(vanillaConfigFeature -> FeatureGrouping.serializeAndCompareFeature(vanillaConfigFeature, configuredFeature))) {

                        if (WBConfig.SERVER.allowVanillaFeatures.get()) {
                            // add the vanilla grass and flowers to a map so we can add them
                            // later to the feature list so trees have a chance to spawn
                            if (FeatureGrouping.checksAndAddSmallPlantFeatures(stage, configuredFeature)) {
                                continue;
                            }

                            // if we have no laggy feature config on, then the feature must not be fire, lava, bamboo, etc in order to be added
                            if ((!FeatureGrouping.isLaggyFeature(stage, configuredFeature) || !WBConfig.disallowLaggyFeatures) && !(configuredFeature.config instanceof DecoratedFeatureConfig && ((DecoratedFeatureConfig) configuredFeature.config).feature.feature == Feature.BAMBOO)) {
                                WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(stage, configuredFeature));
                            }
                        }
                    } else if (WBConfig.SERVER.allowModdedFeatures.get()) {
                        // checksAndAddSmallPlantFeatures add the vanilla grass and flowers to a map
                        // so we can add them later to the feature list so trees have a chance to spawn
                        //
                        // checksAndAddLargePlantFeatures adds modded features that might be trees to front
                        // of feature list so they have priority over all vanilla features in same generation stage.
                        if (!FeatureGrouping.checksAndAddSmallPlantFeatures(stage, configuredFeature) && FeatureGrouping.checksAndAddLargePlantFeatures(stage, configuredFeature)) {
                            WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.features.get(stage).add(0, configuredFeature));
                        }
                        else {
                            // cannot be a bamboo feature as we will place them dead last in the feature
                            // list so they don't overwhelm other features or cause as many bamboo breaking
                            // because it got cut off
                            // if we have no laggy feature config on, then the feature must not be fire, lava, bamboo, etc in order to be added
                            if ((!FeatureGrouping.isLaggyFeature(stage, configuredFeature) || !WBConfig.disallowLaggyFeatures) && !(configuredFeature.config instanceof DecoratedFeatureConfig && ((DecoratedFeatureConfig) configuredFeature.config).feature.feature == Feature.BAMBOO)) {
                                WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(stage, configuredFeature));
                            }
                        }
                    }
                }
            }
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addBiomeStructures(Biome biome) {
        for (StructureFeature<?> structure : biome.structureFeatures.keySet()) {
            if (WBBiomes.BLENDED_BIOME.structureFeatures.keySet().stream().noneMatch(struct -> struct == structure)) {
                // blacklisted by structure list
                if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.STRUCTURE, structure.getRegistryName())) {
                    continue;
                }

                if (WBBiomes.VANILLA_TEMP_BIOME.structureFeatures.keySet().stream().anyMatch(vanillaStructure -> vanillaStructure.getClass().equals(structure.getClass()))) {
                    if (WBConfig.SERVER.allowVanillaStructures.get()) {
                        // add the structure version of the structure
                        WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addStructureFeature(new ConfiguredFeature(structure, biome.structureFeatures.get(structure))));
                        boolean finishedFeaturePortion = false;

                        // find the feature version of the structure in this biome and add it so it can spawn
                        for (net.minecraft.world.gen.GenerationStep.Feature stage : GenerationStep.Feature.values()) {
                            for (ConfiguredFeature<?, ?> configuredFeature : biome.getFeaturesForStep(stage)) {
                                if (configuredFeature.config instanceof DecoratedFeatureConfig && ((DecoratedFeatureConfig) configuredFeature.config).feature.feature.getClass().equals(structure.getClass())) {
                                    if (WBBiomes.BLENDED_BIOME.features.get(stage).stream().noneMatch(addedFeature -> FeatureGrouping.serializeAndCompareFeature(addedFeature, configuredFeature))) {
                                        WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(stage, configuredFeature));
                                    }

                                    finishedFeaturePortion = true;
                                    break;
                                }
                            }

                            if (finishedFeaturePortion) break;
                        }
                    }

                } else if (WBConfig.SERVER.allowModdedStructures.get()) {
                    // add the structure version of the structure
                    WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addStructureFeature(new ConfiguredFeature(structure, biome.structureFeatures.get(structure))));
                    boolean finishedFeaturePortion = false;

                    // find the feature version of the structure in this biome and add it so it can spawn
                    for (net.minecraft.world.gen.GenerationStep.Feature stage : GenerationStep.Feature.values()) {
                        for (ConfiguredFeature<?, ?> configuredFeature : biome.getFeaturesForStep(stage)) {
                            if (configuredFeature.feature.getClass().equals(biome.structureFeatures.get(structure).getClass())) {

                                if (WBBiomes.BLENDED_BIOME.features.get(stage).stream().noneMatch(addedFeature -> FeatureGrouping.serializeAndCompareFeature(addedFeature, configuredFeature))) {
                                    WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addFeature(stage, configuredFeature));
                                }

                                finishedFeaturePortion = true;
                                break;
                            }
                        }

                        if (finishedFeaturePortion) break;
                    }
                }
            }
        }
    }


    private static void addBiomeCarvers(Biome biome) {
        for (Carver carverStage : GenerationStep.Carver.values()) {
            for (ConfiguredCarver<?> carver : biome.getCarversForStep(carverStage)) {
                // blacklisted by carver list
                if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.CARVER, carver.carver.getRegistryName())) {
                    continue;
                }

                if (WBBiomes.BLENDED_BIOME.getCarversForStep(carverStage).stream().noneMatch(config -> config.carver == carver.carver)) {
                    if (carver.carver.getRegistryName() != null && carver.carver.getRegistryName().getNamespace().equals("minecraft")) {
                        if (WBConfig.SERVER.allowVanillaCarvers.get())
                            WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addCarver(carverStage, carver));
                    } else if (WBConfig.SERVER.allowModdedCarvers.get()) {
                        WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addCarver(carverStage, carver));
                    }
                }
            }
        }
    }


    private static void addBiomeNaturalMobs(Biome biome) {
        for (EntityCategory entityClass : EntityCategory.values()) {
            for (SpawnEntry spawnEntry : biome.getEntitySpawnList(entityClass)) {
                // blacklisted by natural spawn list
                if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.SPAWN, Registry.ENTITY_TYPE.getId(spawnEntry.type))) {
                    continue;
                }

                if (WBBiomes.BLENDED_BIOME.getEntitySpawnList(entityClass).stream().noneMatch(spawn -> spawn.type == spawnEntry.type)) {
                    if (Registry.ENTITY_TYPE.getId(spawnEntry.type) != null && Registry.ENTITY_TYPE.getId(spawnEntry.type).getNamespace().equals("minecraft")) {
                        if (WBConfig.SERVER.allowVanillaSpawns.get())
                            WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addSpawn(entityClass, spawnEntry));
                    } else if (WBConfig.SERVER.allowModdedSpawns.get()) {
                        WBBiomes.biomes.forEach(blendedBiome -> blendedBiome.addSpawn(entityClass, spawnEntry));
                    }
                }
            }
        }
    }


    private static void addBiomeSurfaceConfig(Biome biome) {
        // return early if biome's is turned off by the vanilla surface configs.
        if (biome.getRegistryName() != null && biome.getRegistryName().getNamespace().equals("minecraft")) {
            if (!WBConfig.SERVER.allowVanillaSurfaces.get()) return;
        } else if (!WBConfig.SERVER.allowModdedSurfaces.get()) {
            return;
        }


        //A check to make sure we can safely cast
        if (biome.getSurfaceConfig() instanceof TernarySurfaceConfig) {
            TernarySurfaceConfig surfaceConfig = (TernarySurfaceConfig) biome.getSurfaceConfig();

            // blacklisted by surface list. Checks top block
            if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.SURFACE_BLOCK, surfaceConfig.getTopMaterial().getBlock().getRegistryName())) {
                return;
            }

            if (!((BlendedSurfaceBuilder) WBBiomes.BLENDED_SURFACE_BUILDER).containsConfig(surfaceConfig)) {
                ((BlendedSurfaceBuilder) WBBiomes.BLENDED_SURFACE_BUILDER).addConfig(surfaceConfig);
            }
        } else {
            //backup way to get the surface config (should always be safe as getSurfaceBuilderConfig always returns a ISurfaceBuilderConfig)
            //Downside is we cannot get the underwater block now.
            SurfaceConfig surfaceConfig = biome.getSurfaceConfig();

            // blacklisted by surface list. Checks top block
            if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.SURFACE_BLOCK, surfaceConfig.getTopMaterial().getBlock().getRegistryName())) {
                return;
            }

            if (!((BlendedSurfaceBuilder) WBBiomes.BLENDED_SURFACE_BUILDER).containsConfig(surfaceConfig)) {
                ((BlendedSurfaceBuilder) WBBiomes.BLENDED_SURFACE_BUILDER).addConfig(surfaceConfig);
            }
        }
    }
}
