package net.telepathicgrunt.worldblender.the_blender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.DecoratedFlowerFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;


public class FeatureGrouping
{
	public static void setupFeatureMaps() 
	{
		for(GenerationStep.Feature stage : GenerationStep.Feature.values())
		{
			SMALL_PLANT_MAP.put(stage, new ArrayList<ConfiguredFeature<?,?>>());
			LARGE_PLANT_MAP.put(stage, new ArrayList<ConfiguredFeature<?,?>>());
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
	private static final List<String> LAGGY_FEATURE_KEYWORDS = Arrays.asList("lava","fire","bamboo","sugar_cane");
	private static final Identifier GNS_NETHER_SPREAD = new Identifier("goodnightsleep:nether_splash");
	public static boolean bambooFound = false;
	
	/**
	 * tries to find if the feature is bamboo, sugar cane, lava, or 
	 * fire and return true if it is due to them being laggy
	 */
	public static boolean isLaggyFeature(GenerationStep.Feature stage, ConfiguredFeature<?, ?> configuredFeature) 
	{
		if(configuredFeature.config instanceof DecoratedFeatureConfig)
		{
			DecoratedFeatureConfig decoratedConfig = (DecoratedFeatureConfig)configuredFeature.config;
			Identifier rl = null;
			
			//A bunch of edge cases that have to handled because features can hold other features.
			//If a mod makes a custom feature to hold features, welp, we are screwed. Nothing we can do about it. 
			if(decoratedConfig.feature.feature == Feature.RANDOM_RANDOM_SELECTOR)
			{
				for(ConfiguredFeature<?, ?> nestedConfiguredFeature : ((RandomRandomFeatureConfig)decoratedConfig.feature.config).features)
				{
					rl = nestedConfiguredFeature.feature.getRegistryName();
					if(keywordFoundInPath(rl, BAMBOO_FEATURE_KEYWORDS))
						bambooFound = true;
					
					if(keywordFoundInPath(rl, LAGGY_FEATURE_KEYWORDS))
						return true;
				}
			}
			else if(decoratedConfig.feature.feature == Feature.RANDOM_SELECTOR)
			{
				for(RandomFeatureEntry<?> nestedConfiguredFeature : ((RandomFeatureConfig)decoratedConfig.feature.config).features)
				{
					rl = nestedConfiguredFeature.feature.feature.getRegistryName();
					if(keywordFoundInPath(rl, BAMBOO_FEATURE_KEYWORDS))
						bambooFound = true;
					
					if(keywordFoundInPath(rl, LAGGY_FEATURE_KEYWORDS))
						return true;
				}
			}
			else if(decoratedConfig.feature.feature == Feature.SIMPLE_RANDOM_SELECTOR)
			{
				for(ConfiguredFeature<?, ?> nestedConfiguredFeature : ((SimpleRandomFeatureConfig)decoratedConfig.feature.config).features)
				{
					rl = nestedConfiguredFeature.feature.getRegistryName();
					if(keywordFoundInPath(rl, BAMBOO_FEATURE_KEYWORDS))
						bambooFound = true;
					
					if(keywordFoundInPath(rl, LAGGY_FEATURE_KEYWORDS))
						return true;
				}
			}
			else if(decoratedConfig.feature.feature == Feature.RANDOM_BOOLEAN_SELECTOR)
			{
				rl = ((RandomBooleanFeatureConfig)decoratedConfig.feature.config).featureTrue.feature.getRegistryName();
				if(keywordFoundInPath(rl, BAMBOO_FEATURE_KEYWORDS))
					bambooFound = true;
				
				if(keywordFoundInPath(rl, LAGGY_FEATURE_KEYWORDS))
					return true;
				
				rl = ((RandomBooleanFeatureConfig)decoratedConfig.feature.config).featureFalse.feature.getRegistryName();
				if(keywordFoundInPath(rl, BAMBOO_FEATURE_KEYWORDS))
					bambooFound = true;
				
				if(keywordFoundInPath(rl, LAGGY_FEATURE_KEYWORDS))
					return true;
			}
			//end of edge cases with nested features
			else if(decoratedConfig.feature.feature == Feature.LAKE)
			{
				rl = ((SingleStateFeatureConfig)decoratedConfig.feature.config).state.getBlock().getRegistryName();
			}
			else if(decoratedConfig.feature.feature == Feature.SIMPLE_BLOCK)
			{
				rl = ((SimpleBlockFeatureConfig)decoratedConfig.feature.config).toPlace.getBlock().getRegistryName();
			}
			else if(decoratedConfig.feature.feature == Feature.RANDOM_PATCH)
			{
				rl = ((RandomPatchFeatureConfig)decoratedConfig.feature.config).stateProvider.getBlockState(new Random(0), BlockPos.ORIGIN).getBlock().getRegistryName();
			}
			else if(decoratedConfig.feature.feature == Feature.SPRING_FEATURE)
			{
				rl = ((SpringFeatureConfig)decoratedConfig.feature.config).state.getBlockState().getBlock().getBlock().getRegistryName();
			}
			else if(decoratedConfig.feature.feature == Feature.ORE)
			{
				rl = ((OreFeatureConfig)decoratedConfig.feature.config).state.getBlockState().getBlock().getBlock().getRegistryName();
			}
			else if(decoratedConfig.feature.feature == Feature.DISK)
			{
				rl = ((DiskFeatureConfig)decoratedConfig.feature.config).state.getBlockState().getBlock().getBlock().getRegistryName();
			}
			else
			{
				rl = decoratedConfig.feature.feature.getRegistryName();
			}

			//checks rl of non-nested feature's block or itself
			if(keywordFoundInPath(rl, BAMBOO_FEATURE_KEYWORDS))
				bambooFound = true;
			
			if(keywordFoundInPath(rl, LAGGY_FEATURE_KEYWORDS) || (rl != null && rl.equals(GNS_NETHER_SPREAD)))
				return true;
			
		}
		else
		{
			Identifier rl = configuredFeature.feature.getRegistryName();

			//checks rl of non-nested feature's block or itself
			if(keywordFoundInPath(rl, BAMBOO_FEATURE_KEYWORDS))
				bambooFound = true;
			
			if(keywordFoundInPath(rl, LAGGY_FEATURE_KEYWORDS))
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
		
		
		if(configuredFeature.feature instanceof DecoratedFlowerFeature)
		{
			//is flower already, add it to map
			SMALL_PLANT_MAP.get(stage).add(configuredFeature);
			return true;
		}
		else if(configuredFeature.config instanceof DecoratedFeatureConfig)
		{
			DecoratedFeatureConfig decoratedConfig = (DecoratedFeatureConfig)configuredFeature.config;
			Identifier rl;
			
			//A bunch of edge cases that have to handled because features can hold other features.
			//If a mod makes a custom feature to hold features, welp, we are screwed. Nothing we can do about it. 
			if(decoratedConfig.feature.feature == Feature.FLOWER)
			{
				//is flower already, add it to map
				SMALL_PLANT_MAP.get(stage).add(configuredFeature);
				return true;
			}
			else if(decoratedConfig.feature.feature == Feature.RANDOM_PATCH)
			{
				rl = ((RandomPatchFeatureConfig)decoratedConfig.feature.config).stateProvider.getBlockState(new Random(0), BlockPos.ORIGIN).getBlock().getRegistryName();
				if(keywordFoundInPath(rl, SMALL_PLANT_KEYWORDS))
				{
					SMALL_PLANT_MAP.get(stage).add(configuredFeature);
					return true;
				}
			}
			
		}
		else
		{
			Identifier rl = configuredFeature.feature.getRegistryName();
			if(keywordFoundInPath(rl, SMALL_PLANT_KEYWORDS))
			{
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
		
		if(configuredFeature.config instanceof DecoratedFeatureConfig)
		{
			DecoratedFeatureConfig decoratedConfig = (DecoratedFeatureConfig)configuredFeature.config;
			Identifier rl = null;
			
			//A bunch of edge cases that have to handled because features can hold other features.
			//If a mod makes a custom feature to hold features, welp, we are screwed. Nothing we can do about it. 
			if(decoratedConfig.feature.feature == Feature.RANDOM_RANDOM_SELECTOR)
			{
				for(ConfiguredFeature<?, ?> nestedConfiguredFeature : ((RandomRandomFeatureConfig)decoratedConfig.feature.config).features)
				{
					rl = nestedConfiguredFeature.feature.getRegistryName();
					if(addFeatureToLargePlantMap(rl, configuredFeature,stage))
						return true;
				}
			}
			else if(decoratedConfig.feature.feature == Feature.RANDOM_SELECTOR)
			{
				for(RandomFeatureEntry<?> nestedConfiguredFeature : ((RandomFeatureConfig)decoratedConfig.feature.config).features)
				{
					rl = nestedConfiguredFeature.feature.feature.getRegistryName();
					if(addFeatureToLargePlantMap(rl, configuredFeature,stage))
						return true;
				}
			}
			else if(decoratedConfig.feature.feature == Feature.SIMPLE_RANDOM_SELECTOR)
			{
				for(ConfiguredFeature<?, ?> nestedConfiguredFeature : ((SimpleRandomFeatureConfig)decoratedConfig.feature.config).features)
				{
					rl = nestedConfiguredFeature.feature.getRegistryName();
					if(addFeatureToLargePlantMap(rl, configuredFeature,stage))
						return true;
				}
			}
			else if(decoratedConfig.feature.feature == Feature.RANDOM_BOOLEAN_SELECTOR)
			{
				rl = ((RandomBooleanFeatureConfig)decoratedConfig.feature.config).featureTrue.feature.getRegistryName();
				if(addFeatureToLargePlantMap(rl, configuredFeature,stage))
					return true;
				
				rl = ((RandomBooleanFeatureConfig)decoratedConfig.feature.config).featureFalse.feature.getRegistryName();
				if(addFeatureToLargePlantMap(rl, configuredFeature,stage))
					return true;
			}
			else
			{
				rl = decoratedConfig.feature.feature.getRegistryName();
				if(addFeatureToLargePlantMap(rl, configuredFeature,stage))
					return true;
			}
		}
		else
		{
			Identifier rl = configuredFeature.feature.getRegistryName();
			if(addFeatureToLargePlantMap(rl, configuredFeature,stage))
				return true;
		}
		
		return false;
	}
	
	/*
	 * Adds large plant found to the large plant map if rl isn't null
	 */
	private static boolean addFeatureToLargePlantMap(Identifier rl, ConfiguredFeature<?,?> configuredFeature, GenerationStep.Feature stage) 
	{
		if(keywordFoundInPath(rl, LARGE_PLANT_KEYWORDS))
		{
			LARGE_PLANT_MAP.get(stage).add(configuredFeature);
			return true;
		}
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//UTILS
	
	
	/**
	 * Takes the feature's ResourceLocation and checks if the path contains a keyword from a list anywhere in it.
	 */
	private static boolean keywordFoundInPath(Identifier featureRL, List<String> keywordList) 
	{
		if(featureRL != null)
		{
			String path = featureRL.getPath();
			for(String keyword : keywordList)
			{
				if(path.contains(keyword))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Will serialize (if possible) both features and check if they are the same feature.
	 * If cannot serialize, compare the feature itself to see if it is the same
	 */
	public static boolean serializeAndCompareFeature(ConfiguredFeature<?, ?> feature1, ConfiguredFeature<?, ?> feature2)
	{
		try
		{
			Map<Dynamic<Tag>, Dynamic<Tag>> feature1Map = feature1.serialize(NbtOps.INSTANCE).getMapValues().get();
			Map<Dynamic<Tag>, Dynamic<Tag>> feature2Map = feature2.serialize(NbtOps.INSTANCE).getMapValues().get();

			if (feature1Map != null && feature2Map != null)
			{
				return feature1Map.equals(feature2Map);
			}
		}
		catch (Exception e)
		{
			//One of the features cannot be serialized which can only happen with custom modded features
			//Check if the features are the same feature even though the placement or config for the feature might be different. 
			//This is the best way we can remove duplicate modded features as best as we can. (I think)
			if ((feature1.config instanceof DecoratedFeatureConfig && feature2.config instanceof DecoratedFeatureConfig) && 
				((DecoratedFeatureConfig) feature1.config).feature.feature == ((DecoratedFeatureConfig) feature2.config).feature.feature)
			{
				return true;
			}
		}

		return false;
	}
}
