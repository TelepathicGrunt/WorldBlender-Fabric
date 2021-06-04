package com.telepathicgrunt.worldblender.features;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.entities.ItemClearingEntity;
import com.telepathicgrunt.worldblender.entities.WBEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;


public class ItemClearingFeature extends Feature<DefaultFeatureConfig>
{

	public ItemClearingFeature()
	{
		super(DefaultFeatureConfig.CODEC);
	}

	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos position, DefaultFeatureConfig config)
	{
		ItemClearingEntity itemClearingEntity = WBEntities.ITEM_CLEARING_ENTITY.create(world.toServerWorld());
		if(itemClearingEntity == null){
			WorldBlender.LOGGER.warn("Error with spawning clearing item entity at: ({}, {}, {})", position.getX(), position.getY(), position.getZ());
			return false;
		}
		itemClearingEntity.refreshPositionAndAngles((double)position.getX() + 0.5D, 255, (double)position.getZ() + 0.5D, 0.0F, 0.0F);
		world.spawnEntity(itemClearingEntity);
		return true;
	}
}
