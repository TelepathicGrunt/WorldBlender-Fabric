package com.telepathicgrunt.worldblender.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.telepathicgrunt.worldblender.WorldBlender;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class WBPortalClientOverlay {
    private static final Identifier TEXTURE_OVERLAY_1 = new Identifier(WorldBlender.MODID, "textures/portal_overlay_1.png");

    public static boolean portalOverlay(PlayerEntity player, MatrixStack matrixStack) {

        if (player.world.getBlockState(new BlockPos(player.getCameraPosVec(1))).getBlock() == WBBlocks.WORLD_BLENDER_PORTAL) {

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            RenderSystem.depthFunc(519);
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableTexture();
            RenderSystem.setShaderTexture(0, TEXTURE_OVERLAY_1);


            float minU = -0f;
            float maxU = 1f;
            float midU = (minU + maxU) / 2.0F;
            
            float minV = -0f;
            float maxV = 1f;
            float midV = (minV + maxV) / 2.0F;
            
            float lerpAmount = 0;
            float lerp1 = MathHelper.lerp(lerpAmount, minU, midU);
            float lerp2 = MathHelper.lerp(lerpAmount, maxU, midU);
            float lerp3 = MathHelper.lerp(lerpAmount, minV, midV);
            float lerp4 = MathHelper.lerp(lerpAmount, maxV, midV);

            float scale = 0.68f;
            float rSizeScale = 0.1f;
            float rSpinScale = 0.03f;
            float rSpinStartSpeed = 1f;
            float alpha = 0.37f;

            for(int r = 0; r < 6; ++r) {
                int altR = ((r % 2) * 2) - 1;
                float scaledSizeR = (r * rSizeScale);
                float scaledSpinR = altR * ((r + rSpinStartSpeed) * rSpinScale);

                matrixStack.push();
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(scaledSpinR * ((Util.getMeasuringTimeMs() * 10101) % 1000000000000000000L / 100000.0F)));
                Matrix4f matrix4f = matrixStack.peek().getModel();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
                bufferBuilder.vertex(matrix4f, -scale - scaledSizeR, -scale - scaledSizeR, -0.5F).color(1.0F, 1.0F, 1.0F, alpha).texture(lerp2, lerp4).next();
                bufferBuilder.vertex(matrix4f, scale + scaledSizeR, -scale - scaledSizeR, -0.5F).color(1.0F, 1.0F, 1.0F, alpha).texture(lerp1, lerp4).next();
                bufferBuilder.vertex(matrix4f, scale + scaledSizeR, scale + scaledSizeR, -0.5F).color(1.0F, 1.0F, 1.0F, alpha).texture(lerp1, lerp3).next();
                bufferBuilder.vertex(matrix4f, -scale - scaledSizeR, scale + scaledSizeR, -0.5F).color(1.0F, 1.0F, 1.0F, alpha).texture(lerp2, lerp3).next();
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
                matrixStack.pop();
            }

            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.depthFunc(515);

            return true;
        }

        return false;
    }
}
