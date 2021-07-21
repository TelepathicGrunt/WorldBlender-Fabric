package com.telepathicgrunt.worldblender.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.worldblender.WBIdentifiers;
import com.telepathicgrunt.worldblender.mixin.worldgen.BiomeLayerSamplerAccessor;
import com.telepathicgrunt.worldblender.utils.WorldSeedHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.LongFunction;
import java.util.stream.Collectors;


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
		super(BIOMES.stream().map(biomeRegistry::get).collect(Collectors.toList()));

		this.biomeRegistry = biomeRegistry;
		this.biomeSize = biomeSize;
		this.seed = seed;
		this.biomeSampler = buildWorldProcedure(seed, biomeSize, biomeRegistry);
		
		// for debugging purposes
//		Path filePath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), "world_blender-biome_source_debugging.txt");
//		try(PrintStream printStream = new PrintStream(filePath.toString())) {
//			printStream.println("Biome Registry " + biomeRegistry.getClass().getName() + "@" + Integer.toHexString(biomeRegistry.hashCode()));
//			for(Biome biome : biomeRegistry){
//				printStream.println("int id: " + biomeRegistry.getRawId(biome) + "       biome id: " + biomeRegistry.getId(biome) + "       biome instance: " + biome.getClass().getName() + "@" + Integer.toHexString(biome.hashCode()));
//			}
//			printStream.println();
//			printStream.println("----------------------------");
//			printStream.println();
//		}
//		catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
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
				layerFactory = ScaleLayer.FUZZY.create(contextFactory.apply(2000L + (currentExtraZoom * 31L)), layerFactory);
			}
		}
		return layerFactory;
	}

	//private final Set<Biome> printedBiomes = new HashSet<>();
	public Biome getBiomeForNoiseGen(int x, int y, int z) {
		int biomeRawID = ((BiomeLayerSamplerAccessor)this.biomeSampler).worldblender_getSampler().sample(x, z);
		Biome biome = this.biomeRegistry.get(biomeRawID);

		// for debugging purposes
//		if(!printedBiomes.contains(biome)){
//			printedBiomes.add(biome);
//			Path filePath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), "world_blender-biome_source_debugging.txt");
//			try(FileWriter fw = new FileWriter(filePath.toString(), true);
//				BufferedWriter bw = new BufferedWriter(fw);
//				PrintWriter printStream = new PrintWriter(bw))
//			{
//				if (biome == null) {
//					printStream.println("Null biome from int id to resolve: " + biomeRawID);
//					printStream.println("Biome Registry " + biomeRegistry.getClass().getName() + "@" + Integer.toHexString(biomeRegistry.hashCode()));
//					for(Biome biome2 : biomeRegistry){
//						printStream.println("int id: " + biomeRegistry.getRawId(biome2) + "       biome id: " + biomeRegistry.getId(biome2) + "       biome instance: " + biome2.getClass().getName() + "@" + Integer.toHexString(biome2.hashCode()));
//					}
//					printStream.println();
//					printStream.println("----------------------------");
//					printStream.println();
//				}
//				else{
//					printStream.println("int id to resolve: " + biomeRawID + "       biome resolved: " + this.biomeRegistry.getId(biome) + "       biome instance: " + biome.getClass().getName() + "@" + Integer.toHexString(biome.hashCode()) + "        Biome Registry " + biomeRegistry.getClass().getName() + "@" + Integer.toHexString(biomeRegistry.hashCode()));
//				}
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}

		if (biome == null) {
			//fallback to builtin registry if dynamic registry doesnt have biome
			if (SharedConstants.isDevelopment) {
				throw Util.throwOrPause(new IllegalStateException("Unknown biome id: " + biomeRawID));
			}
			else {
				return this.biomeRegistry.get(BuiltinBiomes.fromRawId(biomeRawID));
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
