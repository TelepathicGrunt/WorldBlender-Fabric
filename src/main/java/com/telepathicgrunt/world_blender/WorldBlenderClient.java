package com.telepathicgrunt.world_blender;

import com.telepathicgrunt.world_blender.blocks.WBBlocks;
import com.telepathicgrunt.world_blender.blocks.WBPortalBlockEntity;
import com.telepathicgrunt.world_blender.blocks.WBPortalBlockEntityRenderer;
import com.telepathicgrunt.world_blender.dimension.WBSkyProperty;
import com.telepathicgrunt.world_blender.mixin.dimensions.SkyPropertiesAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class WorldBlenderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SkyPropertiesAccessor.wb_getBY_IDENTIFIER().put(new Identifier(WorldBlender.MODID, "sky_property"), new WBSkyProperty());
		BlockEntityRendererRegistry.INSTANCE.register(WBBlocks.WORLD_BLENDER_PORTAL_BE, WBPortalBlockEntityRenderer::new);

		// Set cooldown for portal after server says it was triggered
		ClientSidePacketRegistry.INSTANCE.register(WBIdentifiers.PORTAL_COOLDOWN_PACKET_ID,
				(packetContext, attachedData) -> {
					BlockPos blockPos = attachedData.readBlockPos();
					float cooldown = attachedData.readFloat();

					packetContext.getTaskQueue().execute(() -> {
						WBPortalBlockEntity wbPortalBlockEntity = null;

						if (MinecraftClient.getInstance().world != null)
							wbPortalBlockEntity = (WBPortalBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(blockPos);

						if (wbPortalBlockEntity != null)
							wbPortalBlockEntity.setCoolDown(cooldown);
					});
				});
	}
}
