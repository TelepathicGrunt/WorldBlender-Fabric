package com.telepathicgrunt.world_blender.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(PistonBlockEntityRenderer.class)
public class PistonBlockEntityRendererMixin {

    @Overwrite
    /**
     * this method is broken with this mod for some reason
     */
    private void method_3575(BlockPos blockPos, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, World world, boolean bl, int i) {
        RenderLayer renderLayer = RenderLayers.getMovingBlockLayer(blockState);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(world, MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState), blockState, blockPos, matrixStack, vertexConsumer, bl, new Random(), blockState.getRenderingSeed(blockPos), i);
    }

}
