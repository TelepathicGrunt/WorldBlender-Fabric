package com.telepathicgrunt.worldblender.features;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class WBConfiguredFeatures
{
    public static final ConfiguredFeature<?,?> WB_PORTAL_ALTAR = WBFeatures.WB_PORTAL_ALTAR.configure(FeatureConfig.DEFAULT);
    public static final ConfiguredFeature<?,?> ANTI_FLOATING_BLOCKS_AND_SEPARATE_LIQUIDS = WBFeatures.ANTI_FLOATING_BLOCKS_AND_SEPARATE_LIQUIDS.configure(FeatureConfig.DEFAULT);
    public static final ConfiguredFeature<?,?> ITEM_CLEARING = WBFeatures.ITEM_CLEARING.configure(FeatureConfig.DEFAULT);

    public static void registerConfiguredFeatures()
    {
        MutableRegistry<ConfiguredFeature<?, ?>> registry = (MutableRegistry<ConfiguredFeature<?, ?>>) BuiltinRegistries.CONFIGURED_FEATURE;

        Registry.register(registry, new Identifier(WorldBlender.MODID, "portal_altar"), WB_PORTAL_ALTAR);
        Registry.register(registry, new Identifier(WorldBlender.MODID, "anti_floating_blocks_and_separate_liquids"), ANTI_FLOATING_BLOCKS_AND_SEPARATE_LIQUIDS);
        Registry.register(registry, new Identifier(WorldBlender.MODID, "item_clearing"), ITEM_CLEARING);
    }
}
