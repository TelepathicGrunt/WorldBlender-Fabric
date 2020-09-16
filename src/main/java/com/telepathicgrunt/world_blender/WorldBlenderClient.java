package com.telepathicgrunt.world_blender;

import com.telepathicgrunt.world_blender.blocks.WBPortalBlock;
import com.telepathicgrunt.world_blender.blocks.WBPortalBlockEntity;
import com.telepathicgrunt.world_blender.blocks.WBPortalSpawning;
import com.telepathicgrunt.world_blender.configs.WBConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldBlenderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// We associate PLAY_PARTICLE_PACKET_ID with this callback, so the server can then use that id to execute the callback.
		ClientSidePacketRegistry.INSTANCE.register(WorldBlender.PORTAL_COOLDOWN_PACKET_ID,
				(packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
					WBPortalBlockEntity wbPortalBlockEntity = null;

					if(MinecraftClient.getInstance().world != null)
						wbPortalBlockEntity = (WBPortalBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(attachedData.readBlockPos());

					if(wbPortalBlockEntity != null)
						wbPortalBlockEntity.setCoolDown(attachedData.readInt());
				}));
	}
}
