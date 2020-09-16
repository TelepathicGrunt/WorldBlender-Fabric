package net.telepathicgrunt.worldblender.generation;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.LongFunction;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.LevelGeneratorType;
import net.telepathicgrunt.worldblender.biome.WBBiomes;
import net.telepathicgrunt.worldblender.generation.layer.MainBiomeLayer;


public class WBBiomeProvider extends BiomeSource
{

	private final BiomeLayerSampler genBiomes;


	public WBBiomeProvider(long seed, LevelGeneratorType worldType)
	{
		super(WBBiomes.biomes);

		//generates the world and biome layouts
		this.genBiomes =  buildOverworldProcedure(seed, worldType);
	}


	public WBBiomeProvider(World world)
	{
		this(world.getSeed(), world.getLevelProperties().getGeneratorType());
		MainBiomeLayer.setSeed(world.getSeed());
	}
	

	public static BiomeLayerSampler buildOverworldProcedure(long seed, LevelGeneratorType typeIn)
	{
	    LayerFactory<CachingLayerSampler> layerArea = buildOverworldProcedure(typeIn, (p_215737_2_) ->
		{
			return new CachingLayerContext(25, seed, p_215737_2_);
		});
		return new BiomeLayerSampler(layerArea);
	}


	public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> buildOverworldProcedure(LevelGeneratorType worldTypeIn, LongFunction<C> contextFactory)
	{
	    LayerFactory<T> layer = MainBiomeLayer.INSTANCE.create(contextFactory.apply(200L));
		layer = ScaleLayer.FUZZY.create(contextFactory.apply(2000L), layer);
		layer = ScaleLayer.NORMAL.create((LayerSampleContext<T>) contextFactory.apply(1001L), layer);
		layer = ScaleLayer.NORMAL.create((LayerSampleContext<T>) contextFactory.apply(1002L), layer);
		return layer;
	}


	@Override
	public Set<Biome> getBiomesInArea(int centerX, int centerY, int centerZ, int sideLength)
	{
		int i = centerX - sideLength >> 2;
		int j = centerY - sideLength >> 2;
		int k = centerZ - sideLength >> 2;
		int l = centerX + sideLength >> 2;
		int i1 = centerY + sideLength >> 2;
		int j1 = centerZ + sideLength >> 2;
		int k1 = l - i + 1;
		int l1 = i1 - j + 1;
		int i2 = j1 - k + 1;
		Set<Biome> set = Sets.newHashSet();

		for (int j2 = 0; j2 < i2; ++j2)
		{
			for (int k2 = 0; k2 < k1; ++k2)
			{
				for (int l2 = 0; l2 < l1; ++l2)
				{
					int xPos = i + k2;
					int yPos = j + l2;
					int zPos = k + j2;
					set.add(this.getBiomeForNoiseGen(xPos, yPos, zPos));
				}
			}
		}
		return set;
	}


	@Nullable
	@Override
	public BlockPos locateBiome(int x, int y, int z, int range, List<Biome> biomes, Random random)
	{
		int i = x - range >> 2;
		int j = z - range >> 2;
		int k = x + range >> 2;
		int l = z + range >> 2;
		int i1 = k - i + 1;
		int j1 = l - j + 1;
		BlockPos blockpos = null;
		int k1 = 0;

		for (int l1 = 0; l1 < i1 * j1; ++l1)
		{
			int i2 = i + l1 % i1 << 2;
			int j2 = j + l1 / i1 << 2;
			if (biomes.contains(this.getBiomeForNoiseGen(i2, k1, j2)))
			{
				if (blockpos == null || random.nextInt(k1 + 1) == 0)
				{
					blockpos = new BlockPos(i2, 0, j2);
				}

				++k1;
			}
		}

		return blockpos;
	}


	@Override
	public boolean hasStructureFeature(StructureFeature<?> structureIn)
	{
		return this.structureFeatures.computeIfAbsent(structureIn, (structure) ->
		{
			for (Biome biome : this.biomes)
			{
				if (biome.hasStructureFeature(structure))
				{
					return true;
				}
			}

			return false;
		});
	}


	@Override
	public Set<BlockState> getTopMaterials()
	{
		if (this.topMaterials.isEmpty())
		{
			for (Biome biome : this.biomes)
			{
				this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
			}
		}

		return this.topMaterials;
	}


	@Override
	public Biome getBiomeForNoiseGen(int x, int y, int z)
	{
		return this.genBiomes.sample(x, z);
	}

}
