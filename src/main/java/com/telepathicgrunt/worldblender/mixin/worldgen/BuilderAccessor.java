package com.telepathicgrunt.worldblender.mixin.worldgen;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(SpawnSettings.Builder.class)
public interface BuilderAccessor {
    @Accessor("spawners")
    Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> wb_getSpawners();
}
