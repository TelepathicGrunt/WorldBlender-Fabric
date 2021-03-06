package com.telepathicgrunt.worldblender.features;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class WBFeatures
{
    public static Feature<DefaultFeatureConfig> WB_PORTAL_ALTAR = new WBPortalAltar();
    public static Feature<DefaultFeatureConfig> ANTI_FLOATING_BLOCKS_AND_SEPARATE_LIQUIDS = new AntiFloatingBlocksAndSeparateLiquids();

    public static void registerFeatures()
    {
        Registry.register(Registry.FEATURE, new Identifier(WorldBlender.MODID, "portal_altar"), WB_PORTAL_ALTAR);
        Registry.register(Registry.FEATURE, new Identifier(WorldBlender.MODID, "anti_floating_blocks_and_separate_liquids"), ANTI_FLOATING_BLOCKS_AND_SEPARATE_LIQUIDS);
    }
}
