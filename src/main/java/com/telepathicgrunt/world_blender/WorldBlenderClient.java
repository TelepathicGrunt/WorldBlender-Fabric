package com.telepathicgrunt.world_blender;

import com.telepathicgrunt.world_blender.blocks.WBPortalBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;

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
						wbPortalBlockEntity.setCoolDown(attachedData.readFloat());
				}));
	}
}
