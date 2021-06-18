package com.telepathicgrunt.worldblender.features;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.entities.ItemClearingEntity;
import com.telepathicgrunt.worldblender.entities.WBEntities;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;


public class ItemClearingFeature extends Feature<DefaultFeatureConfig>
{

	public ItemClearingFeature()
	{
		super(DefaultFeatureConfig.CODEC);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context)
	{
		ItemClearingEntity itemClearingEntity = WBEntities.ITEM_CLEARING_ENTITY.create(context.getWorld().toServerWorld());
		if(itemClearingEntity == null){
			WorldBlender.LOGGER.warn("Error with spawning clearing item entity at: ({}, {}, {})", context.getOrigin().getX(), context.getOrigin().getY(), context.getOrigin().getZ());
			return false;
		}
		itemClearingEntity.refreshPositionAndAngles((double)context.getOrigin().getX() + 0.5D, context.getGenerator().getWorldHeight(), (double)context.getOrigin().getZ() + 0.5D, 0.0F, 0.0F);
		context.getWorld().spawnEntity(itemClearingEntity);
		return true;
	}
}
