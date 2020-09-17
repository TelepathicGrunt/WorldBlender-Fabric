package com.telepathicgrunt.world_blender.the_blender;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;

import java.util.*;


public class FeatureGrouping
{
	public static void setupFeatureMaps() 
	{
		for(GenerationStep.Feature stage : GenerationStep.Feature.values())
		{
			SMALL_PLANT_MAP.put(stage, new ArrayList<>());
			LARGE_PLANT_MAP.put(stage, new ArrayList<>());
			bambooFound = false;
		}
	}
	
	public static void clearFeatureMaps() 
	{
		SMALL_PLANT_MAP.clear();
		LARGE_PLANT_MAP.clear();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	private static final List<String> BAMBOO_FEATURE_KEYWORDS = Arrays.asList("bamboo");
	private static final List<String> LAGGY_FEATURE_KEYWORDS = Arrays.asList("lava", "fire", "bamboo", "sugar_cane");
	public static boolean bambooFound = false;
	
	/**
	 * tries to find if the feature is bamboo, sugar cane, lava, or 
	 * fire and return true if it is due to them being laggy
	 */
	public static boolean isLaggyFeature(GenerationStep.Feature stage, ConfiguredFeature<?, ?> configuredFeature) 
	{
		Optional<JsonElement> optionalConfiguredFeatureJSON = ConfiguredFeature.CODEC.encode(() -> configuredFeature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();

		if(optionalConfiguredFeatureJSON.isPresent()){
			JsonElement configuredFeatureJSON = optionalConfiguredFeatureJSON.get();
			JsonElement test = configuredFeatureJSON.getAsJsonObject().get("state");

			if(regexFindWord(configuredFeatureJSON.toString(), BAMBOO_FEATURE_KEYWORDS))
				bambooFound = true;

			if(regexFindState(configuredFeatureJSON.toString(), LAGGY_FEATURE_KEYWORDS))
				return true;

		}

		return false;
	}
	

	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Map<GenerationStep.Feature, List<ConfiguredFeature<?, ?>>> SMALL_PLANT_MAP = Maps.newHashMap();
	private static final List<String> SMALL_PLANT_KEYWORDS = Arrays.asList("grass","flower","rose","plant","bush","fern");

	/**
	 * Will check if incoming configuredfeature is a small plant and add it to the small plant map if it is so 
	 * we can have a list of them for specific feature manipulation later
	 */
	public static boolean checksAndAddSmallPlantFeatures(GenerationStep.Feature stage, ConfiguredFeature<?, ?> configuredFeature) 
	{
		//if small plant is already added, skip it
		if(SMALL_PLANT_MAP.get(stage).stream().anyMatch(vanillaConfigFeature -> serializeAndCompareFeature(vanillaConfigFeature, configuredFeature)))
		{
			return false;
		}


		Optional<JsonElement> optionalConfiguredFeatureJSON = ConfiguredFeature.CODEC.encode(() -> configuredFeature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();

		if(optionalConfiguredFeatureJSON.isPresent()) {
			JsonElement configuredFeatureJSON = optionalConfiguredFeatureJSON.get();

			if (regexFindWord(configuredFeatureJSON.toString(), SMALL_PLANT_KEYWORDS) ||
					regexFindState(configuredFeatureJSON.toString(), SMALL_PLANT_KEYWORDS)) {

				SMALL_PLANT_MAP.get(stage).add(configuredFeature);
				return true;
			}
		}

		
		return false;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//while we are storing large plants into this map, we don't use it at the moment as we just
	//need to identify what's a large plant and move it to the front of the feature list.
	public static final Map<GenerationStep.Feature, List<ConfiguredFeature<?, ?>>> LARGE_PLANT_MAP = Maps.newHashMap();
	private static final List<String> LARGE_PLANT_KEYWORDS = Arrays.asList("tree","huge_mushroom","big_mushroom","poplar","twiglet","mangrove","bramble");
	
	/**
	 * Will check if incoming configuredfeature is a large plant and add it to the Large plant map if it is so 
	 * we can have a list of them for specific feature manipulation later
	 */
	public static boolean checksAndAddLargePlantFeatures(GenerationStep.Feature stage, ConfiguredFeature<?, ?> configuredFeature) 
	{
		//if large plant is already added, skip it
		if(LARGE_PLANT_MAP.get(stage).stream().anyMatch(vanillaConfigFeature -> serializeAndCompareFeature(vanillaConfigFeature, configuredFeature)))
		{
			return false;
		}


		Optional<JsonElement> optionalConfiguredFeatureJSON = ConfiguredFeature.CODEC.encode(() -> configuredFeature, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();

		if(optionalConfiguredFeatureJSON.isPresent()) {
			JsonElement configuredFeatureJSON = optionalConfiguredFeatureJSON.get();

			if (regexFindWord(configuredFeatureJSON.toString(), LARGE_PLANT_KEYWORDS) ||
					regexFindState(configuredFeatureJSON.toString(), LARGE_PLANT_KEYWORDS)) {

				LARGE_PLANT_MAP.get(stage).add(configuredFeature);
				return true;
			}
		}

		return false;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//UTILS
	

	/**
	 * Look to see if any of the banned words are in the json state object
	 */
	private static boolean regexFindState(String jsonstring, List<String> keywordList)
	{
		for(String keyword : keywordList)
		{
			if(jsonstring.contains(keyword))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Look to see if any of the banned words are in the json feature object
	 */
	private static boolean regexFindWord(String jsonstring, List<String> keywordList)
	{
		for(String keyword : keywordList)
		{
			if(jsonstring.contains(keyword))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Will serialize (if possible) both features and check if they are the same feature.
	 * If cannot serialize, compare the feature itself to see if it is the same.
	 */
	public static boolean serializeAndCompareFeature(ConfiguredFeature<?, ?> configuredFeature1, ConfiguredFeature<?, ?> configuredFeature2) {

		Optional<JsonElement> configuredFeatureJSON1 = ConfiguredFeature.CODEC.encode(() -> configuredFeature1, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();
		Optional<JsonElement> configuredFeatureJSON2 = ConfiguredFeature.CODEC.encode(() -> configuredFeature2, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().left();

		// Compare the JSON to see if it's the exact same ConfiguredFeature.
		if(configuredFeatureJSON1.isPresent() &&
				configuredFeatureJSON2.isPresent() &&
				configuredFeatureJSON1.equals(configuredFeatureJSON2))
		{
			return true;
		}

		// Check deeper to see if they are the same.
		return (configuredFeature1.config instanceof DecoratedFeatureConfig &&
				configuredFeature2.config instanceof DecoratedFeatureConfig) &&
						((DecoratedFeatureConfig) configuredFeature1.config).feature.get().feature ==
						((DecoratedFeatureConfig) configuredFeature2.config).feature.get().feature;
	}
}
