package com.telepathicgrunt.worldblender.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

import java.util.stream.IntStream;

public class WBPortalClientOverlay {
    private static final Identifier TEXTURE_GLINT = new Identifier("textures/misc/enchanted_item_glint.png");
    private static final Identifier TEXTURE_FORCE_FIELD = new Identifier("textures/misc/forcefield.png");
    private static final OctaveSimplexNoiseSampler NOISE_SAMPLER = new OctaveSimplexNoiseSampler(new ChunkRandom(564566989L), IntStream.rangeClosed(-1, 0));

    public static boolean portalOverlay(PlayerEntity player, BlockPos pos, MatrixStack matrixStack) {

        if (player.world.getBlockState(new BlockPos(player.getCameraPosVec(1))).getBlock() == WBBlocks.WORLD_BLENDER_PORTAL) {
            MinecraftClient minecraftIn = MinecraftClient.getInstance();
            float brightnessAtEyes = player.getBrightnessAtEyes();
            float yaw = Math.abs(player.yaw) / 360;
            float pitch = Math.abs(player.pitch) / 360;
            float yPos = (float) player.getPos().y / 5F;

            minecraftIn.getTextureManager().bindTexture(TEXTURE_FORCE_FIELD);
            beginDrawingOverlay(matrixStack, brightnessAtEyes, yPos, yaw, pitch, 9945924F);

            minecraftIn.getTextureManager().bindTexture(TEXTURE_GLINT);
            beginDrawingOverlay(matrixStack, brightnessAtEyes, yPos, yaw, pitch, 23565F);
            return true;
        }

        return false;
    }

    private static void beginDrawingOverlay(MatrixStack matrixStack, float brightnessAtEyes, float yPos, float yaw, float pitch, float inheritOffser) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Matrix4f matrix4f = matrixStack.peek().getModel();
        bufferbuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        drawTexture(bufferbuilder, brightnessAtEyes, yPos, yaw, pitch, inheritOffser, matrix4f);
        // RenderSystem.translatef(0.0F, (Util.getMeasuringTimeMs() % 1000000000000000000L / 100000.0F), 0.0F);
        bufferbuilder.end();
        BufferRenderer.draw(bufferbuilder);
        RenderSystem.disableBlend();
    }

    private static void drawTexture(BufferBuilder bufferbuilder, float brightnessAtEyes, float yPos, float yaw, float pitch, float inheritOffser, Matrix4f matrix4f) {
        float timeOffset = (Util.getMeasuringTimeMs() % 1000000000000000000L / 5000.0F);
        float red = Math.min(((float) Math.abs(NOISE_SAMPLER.sample(timeOffset + inheritOffser, yaw, pitch, yPos)) * 4.95F) * brightnessAtEyes, 1F);
        float green = Math.min(((float) Math.abs(NOISE_SAMPLER.sample(yaw + inheritOffser, timeOffset, pitch + 10000F, yPos) * 3.95F)) * brightnessAtEyes, 1F);
        float blue = Math.min(((float) Math.abs(NOISE_SAMPLER.sample(pitch + 10540F + inheritOffser, yPos, yaw + 1012100F, timeOffset) * 4.0F)) * brightnessAtEyes, 1F);
        float alpha = Math.min(Math.max(((float) NOISE_SAMPLER.sample(pitch + 6500F + inheritOffser, yPos, timeOffset + 3540F, yaw + 13540F) * 3.0F), 0.7F), 0.85F);
        bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(red, green, blue, alpha).texture(4.0F + yaw, 4.0F + pitch).next();
        bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(red, green, blue, alpha).texture(0.0F + yaw, 4.0F + pitch).next();
        bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(red, green, blue, alpha).texture(0.0F + yaw, 0.0F + pitch).next();
        bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(red, green, blue, alpha).texture(4.0F + yaw, 0.0F + pitch).next();
    }
}
