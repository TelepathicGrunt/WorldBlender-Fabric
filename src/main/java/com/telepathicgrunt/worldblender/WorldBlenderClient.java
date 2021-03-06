package com.telepathicgrunt.worldblender;

import com.telepathicgrunt.worldblender.blocks.WBBlocks;
import com.telepathicgrunt.worldblender.blocks.WBPortalBlockEntity;
import com.telepathicgrunt.worldblender.blocks.WBPortalBlockEntityRenderer;
import com.telepathicgrunt.worldblender.dimension.WBSkyProperty;
import com.telepathicgrunt.worldblender.mixin.dimensions.SkyPropertiesAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class WorldBlenderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SkyPropertiesAccessor.wb_getBY_IDENTIFIER().put(new Identifier(WorldBlender.MODID, "sky_property"), new WBSkyProperty());

		BlockEntityRendererRegistry.INSTANCE.register(WBBlocks.WORLD_BLENDER_PORTAL_BE, WBPortalBlockEntityRenderer::new);
		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, hitResult) -> {
			WBPortalBlockEntityRenderer.drawBuffers();
			return true;
		});

		// Set cooldown for portal after server says it was triggered
		ClientPlayNetworking.registerGlobalReceiver(WBIdentifiers.PORTAL_COOLDOWN_PACKET_ID,
				(client, handler, buf, responseSender) -> {
					BlockPos blockPos = buf.readBlockPos();
					float cooldown = buf.readFloat();

					client.execute(() -> {
						WBPortalBlockEntity wbPortalBlockEntity = null;

						if (MinecraftClient.getInstance().world != null)
							wbPortalBlockEntity = (WBPortalBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(blockPos);

						if (wbPortalBlockEntity != null)
							wbPortalBlockEntity.setCoolDown(cooldown);
					});
				});
	}
}
