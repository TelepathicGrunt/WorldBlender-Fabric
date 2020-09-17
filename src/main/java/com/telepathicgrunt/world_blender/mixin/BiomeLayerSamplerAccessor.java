package com.telepathicgrunt.world_blender.mixin;

import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeLayerSampler.class)
public interface BiomeLayerSamplerAccessor {

    @Accessor("sampler")
    CachingLayerSampler getSampler();
}
