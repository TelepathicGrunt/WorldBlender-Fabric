package com.telepathicgrunt.world_blender;

import com.telepathicgrunt.world_blender.blocks.WBPortalSpawning;
import net.fabricmc.api.ModInitializer;

public class WorldBlender implements ModInitializer {
	@Override
	public void onInitialize() {
		WBPortalSpawning.generateRequiredBlockList(WBConfig.requiredBlocksInChests);
	}
}
