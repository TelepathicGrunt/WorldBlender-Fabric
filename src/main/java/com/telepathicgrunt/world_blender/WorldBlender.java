package com.telepathicgrunt.world_blender;

import com.telepathicgrunt.world_blender.blocks.WBPortalSpawning;
import com.telepathicgrunt.world_blender.configs.WBConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldBlender implements ModInitializer {
	public static String MODID = "world_blender";
	public static final Identifier PORTAL_COOLDOWN_PACKET_ID = new Identifier(MODID, "portal_cooldown");
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static WBConfig WB_CONFIG;

	@Override
	public void onInitialize() {
		//Set up config
		AutoConfig.register(WBConfig.class, JanksonConfigSerializer::new);
		WB_CONFIG = AutoConfig.getConfigHolder(WBConfig.class).getConfig();


		WBPortalSpawning.generateRequiredBlockList(WB_CONFIG.WBPortalConfig.requiredBlocksInChests);
	}
}
