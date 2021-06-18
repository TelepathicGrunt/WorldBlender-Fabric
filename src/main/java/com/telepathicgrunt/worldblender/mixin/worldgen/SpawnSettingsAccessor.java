package com.telepathicgrunt.worldblender.mixin.worldgen;

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
    Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> worldblender_getSpawners();

    @Accessor("spawners")
    void worldblender_setSpawners(Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> features);


    @Accessor("spawnCosts")
    Map<EntityType<?>, SpawnSettings.SpawnDensity> worldblender_getSpawnCosts();

    @Accessor("spawnCosts")
    void worldblender_setSpawnCosts(Map<EntityType<?>, SpawnSettings.SpawnDensity> structureFeatures);

}