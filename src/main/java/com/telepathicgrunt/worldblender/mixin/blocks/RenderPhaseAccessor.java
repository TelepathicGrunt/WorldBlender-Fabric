package com.telepathicgrunt.worldblender.mixin.blocks;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
public interface RenderPhaseAccessor {

    @Accessor("END_GATEWAY_SHADER")
    static RenderPhase.Shader wb_getEND_GATEWAY_SHADER() {
        throw new UnsupportedOperationException();
    }
}