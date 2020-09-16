package net.telepathicgrunt.worldblender.the_blender.dedicated_mod_support;

import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.telepathicgrunt.worldblender.biome.WBBiomes;
import net.telepathicgrunt.worldblender.features.WBFeatures;
import net.telepathicgrunt.worldblender.the_blender.ConfigBlacklisting;

public class DimDungeonsCompatibility
{
    private static Identifier DD_BASIC_DUNGEON_RL = new Identifier("dimdungeons:feature_basic_dungeon");
    private static Identifier DD_ADVANCED_DUNGEON_RL = new Identifier("dimdungeons:feature_advanced_dungeon");
    public static boolean allowedBasic = true;
    public static boolean allowedAdvanced = true;

    public static void addDDDungeons() {

	if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, DD_BASIC_DUNGEON_RL) ||
		ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.BLANKET, DD_BASIC_DUNGEON_RL)) {
	    allowedBasic = false;
	}
	if (ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.FEATURE, DD_ADVANCED_DUNGEON_RL) ||
		ConfigBlacklisting.isResourceLocationBlacklisted(ConfigBlacklisting.BlacklistType.BLANKET, DD_ADVANCED_DUNGEON_RL)) {
	    allowedAdvanced = false;
	}


	// loop through WB biomes only and check if DD's dungeons was imported
	// add our feature to handle their dungeons
	for (Biome blendedBiome : WBBiomes.biomes) {

	    // add our dungeon if allowed
	    if(allowedBasic || allowedAdvanced)
		blendedBiome.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, WBFeatures.DD_DUNGEON_FEATURE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT)));

	    // Remove DD's dungeon from the biome to prevent log spam
	    List<ConfiguredFeature<?, ?>> cflist = blendedBiome.getFeaturesForStep(GenerationStep.Feature.SURFACE_STRUCTURES);
	    for (int i = cflist.size() - 1; i >= 0; i--) {
		if (cflist.get(i).config instanceof DecoratedFeatureConfig) {

		    // only add our DD feature if the dungeon dimension biome contains the actual mod's feature
		    Identifier rl = ((DecoratedFeatureConfig) cflist.get(i).config).feature.feature.getRegistryName();
		    if (rl != null && (rl.equals(DD_BASIC_DUNGEON_RL) || rl.equals(DD_ADVANCED_DUNGEON_RL))) {
			
			// remove DD's dungeon since it wont spawn normally in our biome
			blendedBiome.features.get(GenerationStep.Feature.SURFACE_STRUCTURES).remove(cflist.get(i));
		    }
		}
	    }
	}
    }
}
