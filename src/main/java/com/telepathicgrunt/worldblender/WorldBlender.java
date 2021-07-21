package com.telepathicgrunt.worldblender;

import com.telepathicgrunt.worldblender.blocks.WBBlocks;
import com.telepathicgrunt.worldblender.blocks.WBPortalSpawning;
import com.telepathicgrunt.worldblender.configs.WBConfig;
import com.telepathicgrunt.worldblender.configs.WBDimensionOmegaConfigs;
import com.telepathicgrunt.worldblender.dimension.WBBiomeProvider;
import com.telepathicgrunt.worldblender.entities.WBEntities;
import com.telepathicgrunt.worldblender.features.WBConfiguredFeatures;
import com.telepathicgrunt.worldblender.features.WBFeatures;
import com.telepathicgrunt.worldblender.surfacebuilder.WBSurfaceBuilders;
import draylar.omegaconfig.OmegaConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.DefaultBiomeCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldBlender implements ModInitializer {
	public static String MODID = "world_blender";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static WBConfig WB_CONFIG;
	public static final WBDimensionOmegaConfigs omegaConfig = OmegaConfig.register(WBDimensionOmegaConfigs.class);

	@Override
	public void onInitialize() {
		//Set up config
		AutoConfig.register(WBConfig.class, JanksonConfigSerializer::new);
		WB_CONFIG = AutoConfig.getConfigHolder(WBConfig.class).getConfig();

		WBBlocks.register();

		WBFeatures.registerFeatures();
		WBConfiguredFeatures.registerConfiguredFeatures();
		WBSurfaceBuilders.registerSurfaceBuilders();
		WBBiomeProvider.registerBiomeProvider();
		WBEntities.registerEntities();

		UseBlockCallback.EVENT.register(WBPortalSpawning::blockRightClick);
	}
}
