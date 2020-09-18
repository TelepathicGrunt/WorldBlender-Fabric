package com.telepathicgrunt.world_blender.the_blender;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.*;
import java.util.regex.Pattern;


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
	private static final List<String> LAGGY_STATE_KEYWORDS = Arrays.asList("lava", "fire", "bamboo", "sugar_cane");
	private static final List<String> LAGGY_FEATURE_KEYWORDS = Arrays.asList("basalt_pillar","netherrack_replace_blobs");
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

			if(LAGGY_FEATURE_KEYWORDS.stream().anyMatch(string -> configuredFeatureJSON.toString().contains(string))){
				int t =5;
			}

			if(regexContainsBannedFeatureName(configuredFeatureJSON, BAMBOO_FEATURE_KEYWORDS))
				bambooFound = true;

			if(regexContainsBannedFeatureName(configuredFeatureJSON, LAGGY_FEATURE_KEYWORDS))
				return true;

			if(regexContainsBannedState(configuredFeatureJSON, LAGGY_STATE_KEYWORDS))
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

			if (regexContainsBannedFeatureName(configuredFeatureJSON, SMALL_PLANT_KEYWORDS) ||
					regexContainsBannedState(configuredFeatureJSON, SMALL_PLANT_KEYWORDS)) {

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

			if (regexContainsBannedFeatureName(configuredFeatureJSON, LARGE_PLANT_KEYWORDS) ||
					regexContainsBannedState(configuredFeatureJSON, LARGE_PLANT_KEYWORDS)) {

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
	 *
	 * If you get crossed-eye, that normal.
	 * Checks if the state's name block contains a banned word.
	 */
	private static boolean regexContainsBannedState(JsonElement jsonElement, List<String> keywordList)
	{
		JsonObject jsonStartObject = jsonElement.getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : jsonStartObject.entrySet()){
			if(entry.getKey().equals("state")){
				JsonObject jsonStateObject = entry.getValue().getAsJsonObject();
				if(jsonStateObject.has("Name")){
					String blockPath = jsonStateObject.get("Name").getAsString().split(":")[1];for(String keyword : keywordList) {
						if(blockPath.contains(keyword)) return true;
					}
				}
			}
			else if(entry.getValue().isJsonObject()){
				regexContainsBannedState(entry.getValue().getAsJsonObject(), keywordList);
			}
		}

		return false;
	}


	/**
	 * Look to see if any of the banned words are in the json feature object
	 *
	 * If you get crossed-eye, that normal. I blame mojang's json format being so cursed and random.
	 * This is gonna check if the bottommost type or default contains a banned word
	 */
	private static boolean regexContainsBannedFeatureName(JsonElement jsonElement, List<String> keywordList)
	{
		JsonObject jsonStartObject = jsonElement.getAsJsonObject();

		if(jsonStartObject.has("config") && jsonStartObject.get("config").getAsJsonObject().has("feature")){

			JsonObject jsonConfigObject = jsonStartObject.get("config").getAsJsonObject();
			JsonElement jsonFeatureElement = jsonConfigObject.get("feature");

			if(jsonFeatureElement.isJsonObject()){
				return regexContainsBannedFeatureName(jsonFeatureElement, keywordList);
			}
			else if(jsonFeatureElement.isJsonArray()){

				if(jsonConfigObject.has("default")){
					String stringToCheck = jsonConfigObject.get("default").getAsString().split(":")[1];
					for(String keyword : keywordList) {
						if(stringToCheck.contains(keyword)) return true;
					}
					return false;
				}
				else if(jsonConfigObject.has("type")){
					String stringToCheck = jsonConfigObject.get("type").getAsString().split(":")[1];
					for(String keyword : keywordList) {
						if(stringToCheck.contains(keyword)) return true;
					}
					return false;
				}
			}
		}
		else if(jsonStartObject.has("type")){
			String stringToCheck = jsonStartObject.get("type").getAsString().split(":")[1];
			for(String keyword : keywordList) {
				if(stringToCheck.contains(keyword)) return true;
			}
			return false;
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

		return false;

		// Check deeper to see if they are the same.
//		return (configuredFeature1.config instanceof DecoratedFeatureConfig &&
//				configuredFeature2.config instanceof DecoratedFeatureConfig) &&
//						((DecoratedFeatureConfig) configuredFeature1.config).feature.get().feature ==
//						((DecoratedFeatureConfig) configuredFeature2.config).feature.get().feature;
	}
}
