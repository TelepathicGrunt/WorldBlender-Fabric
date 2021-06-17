package com.telepathicgrunt.worldblender.theblender;

import com.google.common.collect.ImmutableMap;
import com.telepathicgrunt.worldblender.WBIdentifiers;
import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.configs.WBBlendingConfigs;
import com.telepathicgrunt.worldblender.features.WBConfiguredFeatures;
import com.telepathicgrunt.worldblender.mixin.worldgen.*;
import com.telepathicgrunt.worldblender.surfacebuilder.SurfaceBlender;
import com.telepathicgrunt.worldblender.theblender.ConfigBlacklisting.BlacklistType;
import net.fabricmc.fabric.impl.structure.FabricStructureImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TheBlender {
	// Prevent modded mobs from drowning out vanilla or other mod's mobs.
	private static final Map<SpawnGroup, Integer> MAX_WEIGHT_PER_GROUP = ImmutableMap.of(
		SpawnGroup.CREATURE, 15,
		SpawnGroup.MONSTER, 120,
		SpawnGroup.WATER_AMBIENT, 30,
		SpawnGroup.WATER_CREATURE, 12,
		SpawnGroup.AMBIENT, 15
	);
	
	/**
	 Kickstarts the blender. Should always be run in MinecraftServer's init which is before the world is loaded.
	 This way it is after Forge and Fabric's biome modification hooks and after any possible edge case mod not using them.
	 */
	public static void blendTheWorld(DynamicRegistryManager.Impl registryManager) {
		Optional<MutableRegistry<Biome>> biomeRegistry = registryManager.getOptionalMutable(Registry.BIOME_KEY);
		if (!biomeRegistry.isPresent()) return;
		Registry<Biome> biomes = biomeRegistry.get();
		
		TheBlender blender = new TheBlender(registryManager);
		blender.blendTheWorld(biomes);
		
		biomes.getEntries().stream()
			.filter(entry -> entry.getKey().getValue().getNamespace().equals(WorldBlender.MODID))
			.map(Map.Entry::getValue)
			.forEach(blender::apply);
	}
	
	// store all the data we're blending
	private final List<List<Supplier<ConfiguredFeature<?, ?>>>> blendedFeaturesByStage = new ArrayList<>();
	private final Collection<Supplier<ConfiguredStructureFeature<?, ?>>> blendedStructures = new ArrayList<>();
	private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> blendedCarversByStage = new HashMap<>();
	private final SpawnSettings.Builder blendedSpawnInfo = new SpawnSettings.Builder();
	private final SurfaceBlender blendedSurface;
	
	// some registries we access
	private final Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry;
	private final Registry<ConfiguredStructureFeature<?, ?>> configuredStructureRegistry;
	private final Registry<ConfiguredCarver<?>> configuredCarverRegistry;
	private final Registry<Block> blockRegistry;
	private final Registry<EntityType<?>> entityTypeRegistry;
	
	// MUST KEEP THESE SETS.
	// They massively speed up World Blender at mod startup by preventing excessive running of .anyMatch and other streams/checks
	// Stores ConfiguredFeatures, ConfiguredStructures, and ConfiguredCarvers.
	private final Set<Object> checkedWorldgenObjects = new HashSet<>();
	private final Set<SpawnSettings.SpawnEntry> checkedMobs = new HashSet<>();
	
	// recognizes and tracks features we need to handle specially
	private final FeatureGrouping featureGrouping = new FeatureGrouping();
	
	private TheBlender(DynamicRegistryManager.Impl registryManager) {
		// Reset the data collections in case user exits a world and re-enters another without quitting Minecraft.
		blendedFeaturesByStage.clear();
		blendedStructures.clear();
		blendedCarversByStage.clear();

		// set up collections of nested lists
		Arrays.stream(GenerationStep.Feature.values()).forEach(stage -> blendedFeaturesByStage.add(new ArrayList<>()));
		Arrays.stream(GenerationStep.Carver.values()).forEach(stage -> blendedCarversByStage.put(stage, new ArrayList<>()));
		
		ConfigBlacklisting.setupBlackLists();
		blendedSurface = new SurfaceBlender(); // this initializer depends on the blacklists being set up
		
		configuredFeatureRegistry = registryManager.get(Registry.CONFIGURED_FEATURE_KEY);
		configuredStructureRegistry = registryManager.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
		configuredCarverRegistry = registryManager.get(Registry.CONFIGURED_CARVER_KEY);
		blockRegistry = Registry.BLOCK;
		entityTypeRegistry = Registry.ENTITY_TYPE;
	}
	
	private List<Supplier<ConfiguredFeature<?, ?>>> blendedStageFeatures(GenerationStep.Feature stage) {
		return blendedFeaturesByStage.get(stage.ordinal());
	}
	
	private void blendTheWorld(Registry<Biome> biomeRegistry) {
		final long startNanos = System.nanoTime();

		for (Map.Entry<RegistryKey<Biome>, Biome> biomeEntry : biomeRegistry.getEntries()) {
			if (!biomeEntry.getKey().getValue().getNamespace().equals(WorldBlender.MODID)) {
				// begin blending into our biomes
				blend(
					biomeEntry.getValue(), // Biome
					biomeEntry.getKey().getValue() // Identifier
				);
			}
		}
		
		// wrap up the last bits that still needs to be blended but after the biome loop
		completeBlending();
		blendedSurface.save();

		final long blendTimeMS = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
		WorldBlender.LOGGER.debug("Blend time: {}ms", blendTimeMS);
		WorldBlender.LOGGER.debug("Feature cache: {}", featureGrouping.getCacheStats());

		// Dispose of cached json in-case future implementations keep this object around for longer.
		featureGrouping.clearCache();
	}
	
	private void apply(Biome blendedBiome) {
		makeBiomeMutable(blendedBiome);
		
		// TODO: it's possible that there will be issues with just passing these nested lists and stuff by reference.
		// it would be easy enough to just deep clone the lists.
		// we should test if this is necessary, ideally in a large modpack.

		// Update: seems to be working alright with a ton of other worldgen mods. Will need to test more.
		
		blendedBiome.getGenerationSettings().getFeatures().clear();
		blendedBiome.getGenerationSettings().getFeatures().addAll(blendedFeaturesByStage);
		
		blendedBiome.getGenerationSettings().getStructureFeatures().clear();
		blendedBiome.getGenerationSettings().getStructureFeatures().addAll(blendedStructures);
		
		Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers = ((GenerationSettingsAccessor) blendedBiome.getGenerationSettings()).wb_getCarvers();
		carvers.clear();
		carvers.putAll(blendedCarversByStage);
		
		SpawnSettings spawnInfo = blendedBiome.getSpawnSettings();
		SpawnSettingsAccessor spawnInfoAccessor = (SpawnSettingsAccessor) spawnInfo;
		SpawnSettingsAccessor blendedAccessor = (SpawnSettingsAccessor) blendedSpawnInfo.build();
		spawnInfoAccessor.wb_setSpawnCosts(blendedAccessor.wb_getSpawnCosts());
		spawnInfoAccessor.wb_setSpawners(blendedAccessor.wb_getSpawners());
	}

	/**
	 Helper method to make WB biomes mutable to add stuff to it later
	 */
	private static void makeBiomeMutable(Biome biome) {
		// Make the structure and features list mutable for modification late
		GenerationSettingsAccessor settingsAccessor = (GenerationSettingsAccessor) biome.getGenerationSettings();
		
		// Fill in generation stages so there are at least 10 or else Minecraft crashes.
		// (we need all stages for adding features/structures to the right stage too)
		List<List<Supplier<ConfiguredFeature<?, ?>>>> generationStages = new ArrayList<>(settingsAccessor.wb_getGSFeatures());
		int minSize = GenerationStep.Feature.values().length;
		for (int i = generationStages.size(); i < minSize; i++) {
			generationStages.add(new ArrayList<>());
		}
		
		// Make the Structure and GenerationStages (features) list mutable for modification later
		settingsAccessor.wb_setGSFeatures(generationStages);
		settingsAccessor.wb_setGSStructureFeatures(new ArrayList<>(settingsAccessor.wb_getGSStructureFeatures()));
		settingsAccessor.wb_setGSStructureFeatures(new ArrayList<>(settingsAccessor.wb_getGSStructureFeatures()));
		
		settingsAccessor.wb_setCarvers(new HashMap<>(settingsAccessor.wb_getCarvers()));
		for (GenerationStep.Carver carverGroup : GenerationStep.Carver.values()) {
			settingsAccessor.wb_getCarvers().put(carverGroup, new ArrayList<>(biome.getGenerationSettings().getCarversForStep(carverGroup)));
		}
		
		((SpawnSettingsAccessor) biome.getSpawnSettings()).wb_setSpawners(new HashMap<>(((SpawnSettingsAccessor) biome.getSpawnSettings()).wb_getSpawners()));
		for (SpawnGroup spawnGroup : SpawnGroup.values()) {
			((SpawnSettingsAccessor) biome.getSpawnSettings()).wb_getSpawners().put(spawnGroup, new ArrayList<>(biome.getSpawnSettings().getSpawnEntries(spawnGroup).getEntries()));
		}
		
		((SpawnSettingsAccessor) biome.getSpawnSettings()).wb_setSpawnCosts(new HashMap<>(((SpawnSettingsAccessor) biome.getSpawnSettings()).wb_getSpawnCosts()));
	}
	
	/**
	 blends the given biome into WB biomes
	 */
	private void blend(Biome biome, Identifier biomeID) {
		// ignore our own biomes to speed things up and prevent possible duplications
		if (biomeID.getNamespace().equals(WorldBlender.MODID)) return;
		
		if (shouldSkip(
			biomeID,
			c -> c.allowVanillaBiomeImport,
			c -> c.allowModdedBiomeImport,
			BlacklistType.BLANKET
		)) return;

		GenerationSettings settings = biome.getGenerationSettings();
		GenerationSettingsAccessor settingsAccessor = (GenerationSettingsAccessor) settings;
		
		addBiomeFeatures(settings.getFeatures());
		addBiomeStructures(settings.getStructureFeatures());
		addBiomeCarvers(settingsAccessor.wb_getCarvers());
		addBiomeNaturalMobs(biome.getSpawnSettings());
		addBiomeSurfaceConfig(settings.getSurfaceConfig(), biomeID);
	}
	
	/**
	 Adds the last bit of stuff that needs to be added to WB biomes after everything else is added.
	 Like bamboo and flowers should be dead last so they don't crowd out tree spawning
	 */
	private void completeBlending() {
		// add end spike directly to all biomes if not directly blacklisted.
		// Turning off vanilla features config will not prevent end spikes from spawning due to them marking the world origin nicely
		Identifier endSpikeID = new Identifier("minecraft", "end_spike");
		if (!ConfigBlacklisting.isIdentifierBlacklisted(BlacklistType.FEATURE, endSpikeID)) {
			blendedStageFeatures(GenerationStep.Feature.SURFACE_STRUCTURES)
				.add(() -> configuredFeatureRegistry.get(endSpikeID));
		}

		// add grass, flower, and other small plants now so they are generated second to last
		for (GenerationStep.Feature stage : GenerationStep.Feature.values()) {
			featureGrouping.smallPlants.get(stage).forEach(grassyFlowerFeature -> {
				List<Supplier<ConfiguredFeature<?, ?>>> stageFeatures = blendedStageFeatures(stage);

				boolean alreadyPresent = stageFeatures.stream().anyMatch(existing ->
					featureGrouping.serializeAndCompareFeature(
						existing.get(),
						grassyFlowerFeature,
						true
					)
				);
				if (alreadyPresent) return;
				
				stageFeatures.add(() -> grassyFlowerFeature);
			});
		}

		if (featureGrouping.bambooFound) {
			// add 1 configured bamboo so it is dead last
			blendedStageFeatures(GenerationStep.Feature.VEGETAL_DECORATION)
				.add(() -> configuredFeatureRegistry.get(new Identifier("minecraft", "bamboo_light")));
		}
		
		
		// make carvers be able to carve any underground blocks including netherrack, end stone, and modded blocks
		if (WorldBlender.WB_CONFIG.WBDimensionConfig.carversCanCarveMoreBlocks) {
			List<CarverAccessor> carvers = blendedCarversByStage.values().stream()
				.flatMap(Collection::stream)
				.map(carver -> (ConfiguredCarverAccessor) carver.get())
				.map(carver -> (CarverAccessor) carver.wb_getcarver())
				.collect(Collectors.toList());
			
			Set<Block> allCarvableBlocks = carvers.stream()
				.flatMap(carver -> carver.wb_getalwaysCarvableBlocks().stream())
				.collect(Collectors.toSet());

			// Apply the surface blocks that needs to be carved to the carvers
			allCarvableBlocks.addAll(blendedSurface.blocksToCarve());
			
			carvers.forEach(carver -> carver.wb_setalwaysCarvableBlocks(allCarvableBlocks));
		}
		
		// add these last so that this can contain other feature's liquids/falling blocks in local modification stage much better
		boolean isModificationBlacklisted = ConfigBlacklisting.isIdentifierBlacklisted(
			BlacklistType.FEATURE,
			new Identifier(WorldBlender.MODID, "anti_floating_blocks_and_separate_liquids")
		);
		if (!isModificationBlacklisted) {
			blendedStageFeatures(GenerationStep.Feature.LOCAL_MODIFICATIONS)
				.add(() -> WBConfiguredFeatures.ANTI_FLOATING_BLOCKS_AND_SEPARATE_LIQUIDS);
		}

		isModificationBlacklisted = ConfigBlacklisting.isIdentifierBlacklisted(
			BlacklistType.FEATURE,
			new Identifier(WorldBlender.MODID, "item_clearing")
		);
		if (!isModificationBlacklisted) {
			blendedStageFeatures(GenerationStep.Feature.LOCAL_MODIFICATIONS)
				.add(() -> WBConfiguredFeatures.ITEM_CLEARING);
		}
	}
	
	private void addBiomeFeatures(List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeaturesByStage) {
		for (GenerationStep.Feature stage : GenerationStep.Feature.values()) {
			if (stage.ordinal() >= biomeFeaturesByStage.size()) break; // In case biomes changes number of stages
			
			List<Supplier<ConfiguredFeature<?, ?>>> stageFeatures = blendedStageFeatures(stage);
			
			for (Supplier<ConfiguredFeature<?, ?>> featureSupplier : biomeFeaturesByStage.get(stage.ordinal())) {
				ConfiguredFeature<?, ?> feature = featureSupplier.get();
				if (checkedWorldgenObjects.contains(feature)) continue;
				checkedWorldgenObjects.add(feature);

				
				// Do deep check to see if this configuredfeature instance is actually the same as another configuredfeature
				boolean alreadyPresent = stageFeatures.stream().anyMatch(existing ->
					featureGrouping.serializeAndCompareFeature(
						existing.get(),
						feature,
						true
					)
				);
				if (alreadyPresent) continue;

				// make sure it is registered
				Identifier featureID = configuredFeatureRegistry.getId(feature);
				if (featureID == null) {
					featureID = BuiltinRegistries.CONFIGURED_FEATURE.getId(feature);
				}
				
				if (shouldSkip(
					featureID,
					c -> c.allowVanillaFeatures,
					c -> c.allowModdedFeatures,
					BlacklistType.FEATURE
				)) continue;
				
				//// begin adding features //////
				
				// add the vanilla grass and flowers to a map so we can add them
				// later to the feature list so trees have a chance to spawn
				if (featureGrouping.checkAndAddSmallPlantFeatures(stage, feature)) continue;
				
				// add modded features that might be trees to front of feature list so
				// they have priority over all vanilla features in same generation stage.
				boolean isVanilla = featureID.getNamespace().equals("minecraft");
				if (!isVanilla && featureGrouping.checkAndAddLargePlantFeatures(stage, feature)) {
					stageFeatures.add(0, featureSupplier);
					continue;
				}

				// if bamboo, dont import as we will import our own bamboo at a better stage.
				// if we have no laggy feature config on, then the feature must not be fire, lava, basalt, etc in order to be added
				if (featureGrouping.isBamboo(feature) || (disallowFireLavaBasaltFeatures() && featureGrouping.isFireLavaBasalt(feature)))
					continue;
				
				stageFeatures.add(featureSupplier);
			}
		}
	}
	
	private void addBiomeStructures(Collection<Supplier<ConfiguredStructureFeature<?, ?>>> biomeStructures) {
		for (Supplier<ConfiguredStructureFeature<?, ?>> structureSupplier : biomeStructures) {
			ConfiguredStructureFeature<?, ?> configuredStructure = structureSupplier.get();
			if (checkedWorldgenObjects.contains(configuredStructure)) continue;
			checkedWorldgenObjects.add(configuredStructure);

			boolean alreadyPresent = blendedStructures.contains(structureSupplier);
			if (alreadyPresent) continue;

			// make sure it is registered
			Identifier configuredStructureID = configuredStructureRegistry.getId(configuredStructure);
			if (configuredStructureID == null) {
				configuredStructureID = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructure);
			}
			
			if (shouldSkip(
				configuredStructureID,
				c -> c.allowVanillaStructures,
				c -> c.allowModdedStructures,
				BlacklistType.STRUCTURE
			)) continue;
			
			blendedStructures.add(structureSupplier);
		}
	}
	
	private void addBiomeCarvers(Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> biomeCarversByStage) {
		for (GenerationStep.Carver carverStage : GenerationStep.Carver.values()) {
			List<Supplier<ConfiguredCarver<?>>> blendedCarvers = blendedCarversByStage.get(carverStage);
			List<Supplier<ConfiguredCarver<?>>> biomeCarvers = biomeCarversByStage.get(carverStage);
			if (biomeCarvers == null) continue;

			for (Supplier<ConfiguredCarver<?>> carverSupplier : biomeCarvers) {
				ConfiguredCarver<?> configuredCarver = carverSupplier.get();
				if (checkedWorldgenObjects.contains(configuredCarver)) continue;
				checkedWorldgenObjects.add(configuredCarver);

				boolean alreadyPresent = blendedCarvers.contains(carverSupplier);
				if (alreadyPresent) continue;

				// make sure it is registered
				Identifier configuredCarverID = configuredCarverRegistry.getId(configuredCarver);
				if (configuredCarverID == null) {
					configuredCarverID = BuiltinRegistries.CONFIGURED_CARVER.getId(configuredCarver);
				}
				
				if (shouldSkip(
					configuredCarverID,
					c -> c.allowVanillaCarvers,
					c -> c.allowModdedCarvers,
					BlacklistType.CARVER
				)) continue;
				
				blendedCarvers.add(carverSupplier);
			}
		}
	}
	
	private void addBiomeNaturalMobs(SpawnSettings biomeSpawnInfo) {
		for (SpawnGroup spawnGroup : SpawnGroup.values()) {
			Integer maxWeight = MAX_WEIGHT_PER_GROUP.getOrDefault(spawnGroup, Integer.MAX_VALUE);
			List<SpawnSettings.SpawnEntry> blendedSpawns = ((BuilderAccessor)blendedSpawnInfo).wb_getSpawners().get(spawnGroup);
			for (SpawnSettings.SpawnEntry spawnEntry : biomeSpawnInfo.getSpawnEntries(spawnGroup).getEntries()) {
				if (checkedMobs.contains(spawnEntry)) continue;
				checkedMobs.add(spawnEntry);
				
				boolean alreadyPresent = blendedSpawns.stream().anyMatch(existing -> existing.type == spawnEntry.type);
				if (alreadyPresent) continue;
				
				// no check needed for if entitytype is null because it is impossible for it to be null without Minecraft blowing up
				Identifier entityTypeID = entityTypeRegistry.getId(spawnEntry.type);
				
				if (shouldSkip(
					entityTypeID,
					c -> c.allowVanillaSpawns,
					c -> c.allowModdedSpawns,
					BlacklistType.SPAWN
				)) continue;
				
				SpawnSettings.SpawnEntry newEntry = new SpawnSettings.SpawnEntry(
					spawnEntry.type,
					// Cap the weight and make sure it isn't too low
					Math.max(1, Math.min(maxWeight, ((EntryAccessor)spawnEntry).getWeight())),
					spawnEntry.minGroupSize,
					spawnEntry.maxGroupSize
				);

				blendedSpawns.add(newEntry);
			}
		}
	}
	
	private void addBiomeSurfaceConfig(SurfaceConfig biomeSurface, Identifier biomeID) {
		if (shouldSkip(
			biomeID,
			c -> c.allowVanillaSurfaces,
			c -> c.allowModdedSurfaces,
			null
		)) return;
		
		// Blacklisted by surface list. Checks top block
		BlockState topMaterial = biomeSurface.getTopMaterial();
		
		// Also do null check as BYG actually managed to set the surfaceConfig's block to be null lol
		if (topMaterial == null) return;
		
		Identifier topBlockID = blockRegistry.getId(topMaterial.getBlock());
		if (ConfigBlacklisting.isIdentifierBlacklisted(BlacklistType.SURFACE_BLOCK, topBlockID)) return;
		
		blendedSurface.addIfMissing(biomeSurface);
	}
	
	/**
	 An attempt to make sure we always have the spacing config for all structures
	 */
	public static void addDimensionalSpacing(Map<RegistryKey<World>, ServerWorld> worlds) {
		Map<StructureFeature<?>, StructureConfig> tempMap = new HashMap<>();
		ServerWorld WBServerWorld = worlds.get(WBIdentifiers.WB_WORLD_KEY);

		if(WBServerWorld != null){
			// Add the default spacings
			tempMap.putAll(StructuresConfig.DEFAULT_STRUCTURES);
			tempMap.putAll(FabricStructureImpl.STRUCTURE_TO_CONFIG_MAP);

			for(Map.Entry<RegistryKey<World>, ServerWorld> serverWorldEntry : worlds.entrySet()){
				// These maps may be immutable for some chunk generators. Our own won't be unless
				// someone messes with it. I take no chances so defensive programming incoming!
				tempMap.putAll(serverWorldEntry.getValue().getChunkManager().getChunkGenerator().getStructuresConfig().getStructures());
			}

			// Set the structure spacing config in wb dimension.
			((StructuresConfigAccessor)WBServerWorld.getChunkManager().getChunkGenerator().getStructuresConfig()).wb_setStructureConfigMap(tempMap);
		}
	}
	
	/**
	 Checks if the given resource should be skipped based on config (checking for null in the process).
	 */
	private static boolean shouldSkip(
		Identifier id,
		Function<WBBlendingConfigs, Boolean> allowVanilla,
		Function<WBBlendingConfigs, Boolean> allowModded,
		ConfigBlacklisting.BlacklistType blacklist
	) {
		if (id == null) return true;
		
		boolean isVanilla = id.getNamespace().equals("minecraft");
		if (isVanilla && !allowVanilla.apply(WorldBlender.WB_CONFIG.WBBlendingConfig)) return true;
		if (!isVanilla && !allowModded.apply(WorldBlender.WB_CONFIG.WBBlendingConfig)) return true;
		
		return blacklist != null && ConfigBlacklisting.isIdentifierBlacklisted(blacklist, id);
	}
	
	private static boolean disallowFireLavaBasaltFeatures() {
		return WorldBlender.WB_CONFIG.WBBlendingConfig.disallowFireLavaBasaltFeatures;
	}
}
