package com.telepathicgrunt.world_blender;

import com.telepathicgrunt.world_blender.blocks.WBBlocks;
import com.telepathicgrunt.world_blender.blocks.WBPortalSpawning;
import com.telepathicgrunt.world_blender.configs.WBConfig;
import com.telepathicgrunt.world_blender.features.WBConfiguredFeatures;
import com.telepathicgrunt.world_blender.features.WBFeatures;
import com.telepathicgrunt.world_blender.generation.WBBiomeProvider;
import com.telepathicgrunt.world_blender.surfacebuilder.WBSurfaceBuilders;
import com.telepathicgrunt.world_blender.the_blender.ConfigBlacklisting;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.DefaultBiomeCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldBlender implements ModInitializer {
	public static String MODID = "world_blender";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static WBConfig WB_CONFIG;

	// TODO: Fixed serializing comparison breaking vanilla trees
	// TODO: Blacklist basalt delta's one feature replacing all netherrack
	// TODO: Add right click behavior to make portal
	// TODO: Finalized config folders and add translations for config
	@Override
	public void onInitialize() {
		//Set up config
		AutoConfig.register(WBConfig.class, JanksonConfigSerializer::new);
		WB_CONFIG = AutoConfig.getConfigHolder(WBConfig.class).getConfig();

		WBBlocks.register();
		WBPortalSpawning.generateRequiredBlockList(WB_CONFIG.WBPortalConfig.requiredBlocksInChests);

		WorldBlender.reserveBiomeIDs();
		WBFeatures.registerFeatures();
		WBConfiguredFeatures.registerConfiguredFeatures();
		WBSurfaceBuilders.registerSurfaceBuilders();
		WBBiomeProvider.registerBiomeProvider();
	}



	public static void reserveBiomeIDs() {
		//Reserve WorldBlender biome IDs for the json version to replace
		Registry.register(BuiltinRegistries.BIOME, WBIdentifiers.GENERAL_BLENDED_BIOME_ID, DefaultBiomeCreator.createNormalOcean(false));
		Registry.register(BuiltinRegistries.BIOME, WBIdentifiers.MOUNTAINOUS_BLENDED_BIOME_ID, DefaultBiomeCreator.createNormalOcean(false));
		Registry.register(BuiltinRegistries.BIOME, WBIdentifiers.COLD_HILLS_BLENDED_BIOME_ID, DefaultBiomeCreator.createNormalOcean(false));
		Registry.register(BuiltinRegistries.BIOME, WBIdentifiers.OCEAN_BLENDED_BIOME_ID, DefaultBiomeCreator.createNormalOcean(false));
		Registry.register(BuiltinRegistries.BIOME, WBIdentifiers.FROZEN_OCEAN_BLENDED_BIOME_ID, DefaultBiomeCreator.createNormalOcean(false));
	}
}
