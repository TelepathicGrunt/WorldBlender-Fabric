package com.telepathicgrunt.worldblender.dimension;

import com.telepathicgrunt.worldblender.WBIdentifiers;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.gen.ChunkRandom;

import java.util.stream.IntStream;


public class MainBiomeLayer implements InitLayer
{
	private final Registry<Biome> dynamicRegistry;
	private static OctaveSimplexNoiseSampler perlinGen;

	public MainBiomeLayer(long seed, Registry<Biome> dynamicRegistry){
		this.dynamicRegistry = dynamicRegistry;

		if (perlinGen == null)
		{
			ChunkRandom sharedseedrandom = new ChunkRandom(seed);
			perlinGen = new OctaveSimplexNoiseSampler(sharedseedrandom, IntStream.rangeClosed(0, 0));
		}
	}
	
//	private double max = -100000;
//	private double min = 100000;

	@Override
	public int sample(LayerRandomnessSource noise, int x, int z)
	{
		double perlinNoise = perlinGen.sample(x * 0.1D, z * 0.1D, false);
		double perlinNoise2 = perlinGen.sample(x * 0.08D + 1000, z * 0.08D + 1000, false);
		
//		max = Math.max(max, perlinNoise);
//		min = Math.min(min, perlinNoise);
//		AllTheFeatures.LOGGER.log(Level.DEBUG, "Max: " + max +", Min: "+min + ", perlin: "+perlinNoise);
		
		
		if(perlinNoise > 0.53) {	
			return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(WBIdentifiers.MOUNTAINOUS_BLENDED_BIOME_ID));
		}
		else if(perlinNoise > -0.58) {	
			if(perlinNoise2 < -0.75) {	
				return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(WBIdentifiers.COLD_HILLS_BLENDED_BIOME_ID));
			}
			else {
				return this.dynamicRegistry.getRawId(this.dynamicRegistry.get(WBIdentifiers.GENERAL_BLENDED_BIOME_ID));
			}
		}
		else {	
			return noise.nextInt(100)/800D + perlinNoise%0.4D > -0.2D ?
					this.dynamicRegistry.getRawId(this.dynamicRegistry.get(WBIdentifiers.OCEAN_BLENDED_BIOME_ID)) :
					this.dynamicRegistry.getRawId(this.dynamicRegistry.get(WBIdentifiers.FROZEN_OCEAN_BLENDED_BIOME_ID));
		}
	
	}
}