package com.telepathicgrunt.world_blender.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(SpawnSettings.class)
public interface SpawnSettingsAccessor {

    @Accessor("spawners")
    Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> wb_getSpawners();

    @Accessor("spawners")
    void wb_setSpawners(Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> features);


    @Accessor("spawnCosts")
    Map<EntityType<?>, SpawnSettings.SpawnDensity> wb_getSpawnCosts();

    @Accessor("spawnCosts")
    void wb_setSpawnCosts(Map<EntityType<?>, SpawnSettings.SpawnDensity> structureFeatures);

}