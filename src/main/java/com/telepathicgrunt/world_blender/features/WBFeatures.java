package net.telepathicgrunt.worldblender.features;

import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.telepathicgrunt.worldblender.WorldBlender;

public class WBFeatures
{
    public static Feature<DefaultFeatureConfig> WB_PORTAL_ALTAR = new WBPortalAltar(DefaultFeatureConfig::deserialize);
    public static Feature<DefaultFeatureConfig> DD_DUNGEON_FEATURE = new DDDungeonFeature(DefaultFeatureConfig::deserialize);
    public static Feature<DefaultFeatureConfig> NO_FLOATING_LIQUIDS_OR_FALLING_BLOCKS = new NoFloatingLiquidsOrFallingBlocks(DefaultFeatureConfig::deserialize);
    public static Feature<DefaultFeatureConfig> SEPARATE_LAVA_AND_WATER = new SeparateLavaAndWater(DefaultFeatureConfig::deserialize);
    
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event)
    {
    	IForgeRegistry<Feature<?>> registry = event.getRegistry();
        WorldBlender.register(registry, WB_PORTAL_ALTAR, "world_blender_portal_altar");
        WorldBlender.register(registry, DD_DUNGEON_FEATURE, "dd_dungeon_feature");
        WorldBlender.register(registry, NO_FLOATING_LIQUIDS_OR_FALLING_BLOCKS, "no_floating_liquids_or_falling_blocks");
        WorldBlender.register(registry, SEPARATE_LAVA_AND_WATER, "separate_lava_and_water");
    }
}
