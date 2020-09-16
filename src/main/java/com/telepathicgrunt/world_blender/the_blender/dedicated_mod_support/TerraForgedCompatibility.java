package net.telepathicgrunt.worldblender.the_blender.dedicated_mod_support;

import com.terraforged.feature.template.decorator.DecoratedFeature;
import com.terraforged.feature.template.feature.MultiTemplateFeature;
import com.terraforged.feature.template.feature.TemplateFeature;
import com.terraforged.feature.template.feature.TemplateFeatureConfig;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.registries.ForgeRegistries;
import net.telepathicgrunt.worldblender.biome.WBBiomes;
import net.telepathicgrunt.worldblender.configs.WBConfig;
import net.telepathicgrunt.worldblender.the_blender.ConfigBlacklisting;
import net.telepathicgrunt.worldblender.the_blender.ConfigBlacklisting.BlacklistType;

public class TerraForgedCompatibility
{
    public static void addTerraForgedtrees() {
	if (!WBConfig.allowModdedFeatures || ConfigBlacklisting.isResourceLocationBlacklisted(BlacklistType.BLANKET, new Identifier("terraforged:_"))) {
	    return;
	}

	for (Feature<?> feature : ForgeRegistries.FEATURES) {
	    if (feature.getRegistryName() == null) continue;

	    // find terraforge trees/features
	    if (feature.getRegistryName().getNamespace().equals("terraforged") && 
		    !ConfigBlacklisting.isResourceLocationBlacklisted(BlacklistType.FEATURE, feature.getRegistryName())) {
		
		// add it to our biomes with vanilla tree placement
		for (Biome blendedBiome : WBBiomes.biomes) {
		    if (feature instanceof TemplateFeature) {
			blendedBiome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ((TemplateFeature) feature).configure(new TemplateFeatureConfig(false, false, 0)).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(5, 0.1F, 1))));
		    }
		    else if (feature instanceof MultiTemplateFeature) {
			blendedBiome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ((MultiTemplateFeature) feature).configure(new TemplateFeatureConfig(false, false, 0)).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(5, 0.1F, 1))));
		    }
		    else if (feature instanceof DecoratedFeature) {
			blendedBiome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ((DecoratedFeature<?, ?>) feature).configure(new TemplateFeatureConfig(false, false, 0)).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(5, 0.1F, 1))));
		    }
		    else {
			blendedBiome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ((TemplateFeature) feature).configure(new TemplateFeatureConfig(false, false, 0)).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(5, 0.1F, 1))));
		    }
		}
	    }
	}
    }
}
