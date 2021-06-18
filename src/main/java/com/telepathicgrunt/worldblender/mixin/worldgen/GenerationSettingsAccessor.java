package com.telepathicgrunt.worldblender.mixin.worldgen;

import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(GenerationSettings.class)
public interface GenerationSettingsAccessor {

    @Accessor("features")
    List<List<Supplier<ConfiguredFeature<?, ?>>>> worldblender_getGSFeatures();

    @Accessor("features")
    void worldblender_setGSFeatures(List<List<Supplier<ConfiguredFeature<?, ?>>>> features);


    @Accessor("structureFeatures")
    List<Supplier<ConfiguredStructureFeature<?, ?>>> worldblender_getGSStructureFeatures();

    @Accessor("structureFeatures")
    void worldblender_setGSStructureFeatures(List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures);


    @Accessor("carvers")
    Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> worldblender_getCarvers();

    @Accessor("carvers")
    void worldblender_setCarvers(Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> features);
}