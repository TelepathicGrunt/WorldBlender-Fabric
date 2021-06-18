package com.telepathicgrunt.worldblender.blocks;

import com.google.common.primitives.Floats;
import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.mixin.blocks.MultiPhaseInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.JsonEffectGlShader;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import java.io.IOException;
import java.util.Random;


public class WBPortalBlockEntityRenderer implements BlockEntityRenderer<WBPortalBlockEntity>
{
	public WBPortalBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
	}

	private static final Random RANDOM = new Random(31100L);
	public static final Identifier MAIN_TEXTURE = new Identifier("textures/misc/enchanted_item_glint.png");
	public static final Identifier ADDITIVE_TEXTURE = new Identifier("textures/misc/forcefield.png");

	RenderLayer.MultiPhase WORLD_BLENDER_PORTAL =
			MultiPhaseInvoker.worldblender_createMultiPhase(
					"world_blender_portal",
					VertexFormats.POSITION_COLOR,
					VertexFormat.DrawMode.QUADS,
					256,
					false,
					false,
					RenderLayer.MultiPhaseParameters.builder()
							.shader(new RenderPhase.Shader(() -> {
								try {
									return new Shader(MinecraftClient.getInstance().getResourceManager(), "rendertype_world_blender_portal", VertexFormats.POSITION_COLOR);
								} catch (IOException e) {
									e.printStackTrace();
								}
								return null;
							}))
							.texture(RenderPhase.Textures.create().add(MAIN_TEXTURE, false, false)
									.add(ADDITIVE_TEXTURE, false, false)
									.build())
							.build(false)
			);

	public void render(WBPortalBlockEntity endPortalBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		RANDOM.setSeed(31100L);
		Matrix4f matrix4f = matrixStack.peek().getModel();
		this.renderSides(endPortalBlockEntity, matrix4f, vertexConsumerProvider.getBuffer(this.getLayer()));
	}

	private void renderSides(WBPortalBlockEntity entity, Matrix4f matrix4f, VertexConsumer vertexConsumer) {

		// turns dark red when cooling down but lightens over time. And when finished cooling down, it pops to full brightness
		float coolDownEffect = entity.isCoolingDown() ? 0.6f - entity.getCoolDown()/1200F : 0.85f;
		float coolDownDiff = entity.getCoolDown()/280F;

		float red = Floats.constrainToRange(coolDownEffect + coolDownDiff, 0.3f, 0.7f);
		float green = Floats.constrainToRange(coolDownEffect - coolDownDiff, 0.3f, 0.65f);
		float blue = Floats.constrainToRange(coolDownEffect - coolDownDiff, 0.3f, 0.65f);

		this.renderSide(entity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, red, green, blue, Direction.SOUTH);
		this.renderSide(entity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, red, green, blue, Direction.NORTH);
		this.renderSide(entity, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, red, green, blue, Direction.EAST);
		this.renderSide(entity, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, red, green, blue, Direction.WEST);
		this.renderSide(entity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, red, green, blue, Direction.DOWN);
		this.renderSide(entity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, red, green, blue, Direction.UP);
	}

	private void renderSide(WBPortalBlockEntity entity, Matrix4f model, VertexConsumer vertices, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, float red, float green, float blue, Direction direction) {
		if (entity.shouldDrawSide(direction)) {
			vertices.vertex(model, x1, y1, z1).color(red, green, blue, 1).next();
			vertices.vertex(model, x2, y1, z2).color(red, green, blue, 1).next();
			vertices.vertex(model, x2, y2, z3).color(red, green, blue, 1).next();
			vertices.vertex(model, x1, y2, z4).color(red, green, blue, 1).next();
		}
	}
	protected RenderLayer getLayer() {
		return WORLD_BLENDER_PORTAL;
	}
}