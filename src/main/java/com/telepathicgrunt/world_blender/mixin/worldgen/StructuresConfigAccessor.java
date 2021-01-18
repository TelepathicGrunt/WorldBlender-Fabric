package com.telepathicgrunt.world_blender.mixin.worldgen;

import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StructuresConfig.class)
public interface StructuresConfigAccessor {

    @Accessor("structures")
    void wb_setStructureConfigMap(Map<StructureFeature<?>, StructureConfig> structures);
}