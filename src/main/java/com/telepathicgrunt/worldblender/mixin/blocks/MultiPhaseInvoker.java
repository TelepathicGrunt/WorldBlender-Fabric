package com.telepathicgrunt.worldblender.mixin.blocks;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderLayer.MultiPhase.class)
public interface MultiPhaseInvoker {
    @Invoker
    static RenderLayer.MultiPhase worldblender_createMultiPhase(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer.MultiPhaseParameters phases) {
        throw new UnsupportedOperationException();
    }
}
