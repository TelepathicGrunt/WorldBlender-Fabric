package com.telepathicgrunt.worldblender.features;

import com.telepathicgrunt.worldblender.WBIdentifiers;
import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.blocks.WBBlocks;
import com.telepathicgrunt.worldblender.blocks.WBPortalBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;


public class WBPortalAltar extends Feature<DefaultFeatureConfig>
{
	public static Structure ALTAR_TEMPLATE;
	private static final StructurePlacementData placementSettings = (new StructurePlacementData())
																			.setMirror(BlockMirror.NONE)
																			.setRotation(BlockRotation.NONE)
																			.setIgnoreEntities(false);
	
	public WBPortalAltar()
	{
		super(DefaultFeatureConfig.CODEC);
	}
	

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context)
	{
		//only world origin chunk allows generation
		if (context.getWorld().toServerWorld().getRegistryKey() != WBIdentifiers.WB_WORLD_KEY ||
				context.getOrigin().getX() >> 4 != 0 ||
				context.getOrigin().getZ() >> 4 != 0)
		{
			return false;
		}

		if (ALTAR_TEMPLATE == null) {
			ALTAR_TEMPLATE = context.getWorld().toServerWorld().getServer().getStructureManager().getStructureOrBlank(WBIdentifiers.ALTAR_ID);

			if (ALTAR_TEMPLATE == null) {
				WorldBlender.LOGGER.warn("world blender portal altar NTB does not exist!");
				return false;
			}
		}
		
		BlockPos.Mutable finalPosition = new BlockPos.Mutable().set(context.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, context.getOrigin()));
		
		//go past trees to world surface
		BlockState blockState = context.getWorld().getBlockState(finalPosition);
		while(finalPosition.getY() > 12 && (!blockState.isOpaque() || blockState.getMaterial() == Material.WOOD)) {
			finalPosition.move(Direction.DOWN);
			blockState = context.getWorld().getBlockState(finalPosition);
		}

		finalPosition.move(Direction.UP);
		context.getWorld().setBlockState(finalPosition.down(), Blocks.AIR.getDefaultState(), 3);
		ALTAR_TEMPLATE.place(context.getWorld(), finalPosition.add(-5, -2, -5), finalPosition.add(-5, -2, -5), placementSettings, context.getRandom(), 3);
		finalPosition.move(Direction.DOWN);
		context.getWorld().setBlockState(finalPosition, WBBlocks.WORLD_BLENDER_PORTAL.getDefaultState(), 3); //extra check to make sure portal is placed

		//make portal block unremoveable in altar
		BlockEntity blockEntity = context.getWorld().getBlockEntity(finalPosition);
		if(blockEntity instanceof WBPortalBlockEntity)
			((WBPortalBlockEntity)blockEntity).makeNotRemoveable();
		
		return true;

	}

}
