package com.telepathicgrunt.world_blender.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.world_blender.WBIdentifiers;
import com.telepathicgrunt.world_blender.mixin.BiomeLayerSamplerAccessor;
import com.telepathicgrunt.world_blender.utils.WorldSeedHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.List;
import java.util.function.LongFunction;


public class WBBiomeProvider extends BiomeSource
{
	public static void registerBiomeProvider() {
		Registry.register(Registry.BIOME_SOURCE, WBIdentifiers.WB_BIOME_PROVIDER_ID, WBBiomeProvider.CODEC);
	}

	public static final Codec<WBBiomeProvider> CODEC =
			RecordCodecBuilder.create((instance) -> instance.group(
					Codec.LONG.fieldOf("seed").orElseGet(WorldSeedHolder::getSeed).forGetter((biomeSource) -> biomeSource.seed),
					RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter((biomeSource) -> biomeSource.biomeRegistry),
					Codec.intRange(1, 20).fieldOf("biome_size").orElse(2).forGetter((biomeSource) -> biomeSource.biomeSize)
			).apply(instance, instance.stable(WBBiomeProvider::new)));

	private final long seed;
	private final int biomeSize;
	private final BiomeLayerSampler biomeSampler;
	private final Registry<Biome> biomeRegistry;
	private static final List<RegistryKey<Biome>> BIOMES = ImmutableList.of(
			RegistryKey.of(Registry.BIOME_KEY, WBIdentifiers.GENERAL_BLENDED_BIOME_ID),
			RegistryKey.of(Registry.BIOME_KEY, WBIdentifiers.MOUNTAINOUS_BLENDED_BIOME_ID),
			RegistryKey.of(Registry.BIOME_KEY, WBIdentifiers.COLD_HILLS_BLENDED_BIOME_ID),
			RegistryKey.of(Registry.BIOME_KEY, WBIdentifiers.OCEAN_BLENDED_BIOME_ID),
			RegistryKey.of(Registry.BIOME_KEY, WBIdentifiers.FROZEN_OCEAN_BLENDED_BIOME_ID));


	public WBBiomeProvider(long seed, Registry<Biome> biomeRegistry, int biomeSize) {
		super(BIOMES.stream().map((registryKey) -> () -> (Biome)biomeRegistry.get(registryKey)));

		this.biomeRegistry = biomeRegistry;
		this.biomeSize = biomeSize;
		this.seed = seed;
		this.biomeSampler = buildWorldProcedure(seed, biomeSize, biomeRegistry);
	}

	public static BiomeLayerSampler buildWorldProcedure(long seed, int biomeSize, Registry<Biome> biomeRegistry) {
		LayerFactory<CachingLayerSampler> layerFactory = build((salt) ->
				new CachingLayerContext(25, seed, salt),
				biomeSize,
				seed,
				biomeRegistry);
		return new BiomeLayerSampler(layerFactory);
	}


	public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(LongFunction<C> contextFactory, int biomeSize, long seed, Registry<Biome> biomeRegistry) {
		LayerFactory<T> layerFactory = (new MainBiomeLayer(seed, biomeRegistry)).create(contextFactory.apply(200L));
		for(int currentExtraZoom = 0; currentExtraZoom < biomeSize; currentExtraZoom++){
			if((currentExtraZoom + 2) % 3 != 0){
				layerFactory = ScaleLayer.NORMAL.create(contextFactory.apply(2001L + currentExtraZoom), layerFactory);
			}
			else{
				layerFactory = ScaleLayer.FUZZY.create(contextFactory.apply(2000L + (currentExtraZoom * 31)), layerFactory);
			}
		}
		return layerFactory;
	}

	public Biome getBiomeForNoiseGen(int x, int y, int z) {
		int k = ((BiomeLayerSamplerAccessor)this.biomeSampler).wb_getSampler().sample(x, z);
		Biome biome = this.biomeRegistry.get(k);
		if (biome == null) {
			//fallback to builtin registry if dynamic registry doesnt have biome
			if (SharedConstants.isDevelopment) {
				throw Util.throwOrPause(new IllegalStateException("Unknown biome id: " + k));
			}
			else {
				return this.biomeRegistry.get(BuiltinBiomes.fromRawId(0));
			}
		}
		else {
			return biome;
		}
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public BiomeSource withSeed(long seed) {
		return new WBBiomeProvider(seed, this.biomeRegistry, this.biomeSize);
	}
}
