package com.telepathicgrunt.world_blender.features;

import com.mojang.datafixers.Dynamic;
import com.telepathicgrunt.world_blender.WorldBlender;
import com.telepathicgrunt.world_blender.blocks.WBBlocks;
import com.telepathicgrunt.world_blender.blocks.WBPortalTileEntity;
import com.telepathicgrunt.world_blender.dimension.WBDimensionRegistration;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;
import java.util.function.Function;


public class WBPortalAltar extends Feature<DefaultFeatureConfig>
{
	private static StructurePlacementData placementSettings = (new StructurePlacementData()).setMirrored(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(false).setChunkPosition((ChunkPos) null);
	
	public WBPortalAltar(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory)
	{
		super(configFactory);
	}
	

	@Override
	public boolean place(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> changedBlock, Random rand, BlockPos position, DefaultFeatureConfig config)
	{
		//only world origin chunk allows generation
		if (world.getDimension().getType() != WBDimensionRegistration.worldblender() || position.getX() >> 4 != 0 || position.getZ() >> 4 != 0)
		{
			return false;
		}
		
		StructureManager templatemanager = ((ServerWorld) world.getWorld()).getSaveHandler().getStructureManager();
		Structure template = templatemanager.getStructure(new Identifier(WorldBlender.MODID + ":world_blender_portal_altar"));

		if (template == null)
		{
			WorldBlender.LOGGER.warn("world blender portal altar NTB does not exist!");
			return false;
		}
		
		BlockPos.Mutable finalPosition = new BlockPos.Mutable(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, position));
		
		//go past trees to world surface
		BlockState blockState = world.getBlockState(finalPosition);
		while(finalPosition.getY() > 12 && (!blockState.isOpaque() || blockState.getMaterial() == Material.WOOD)) {
			finalPosition.setOffset(Direction.DOWN);
			blockState = world.getBlockState(finalPosition);
		}
		
		world.setBlockState(finalPosition.down(), Blocks.AIR.getDefaultState(), 3);
		template.method_15178(world, finalPosition.add(-5, -2, -5), placementSettings);
		finalPosition.setOffset(Direction.DOWN);
		world.setBlockState(finalPosition, WBBlocks.WORLD_BLENDER_PORTAL.get().getDefaultState(), 3); //extra check to make sure portal is placed

		//make portal block unremoveable in altar
		if(world.getBlockEntity(finalPosition) != null && world.getBlockEntity(finalPosition) instanceof WBPortalTileEntity)
			((WBPortalTileEntity)world.getBlockEntity(finalPosition)).makeNotRemoveable();
		
		
		return true;

	}

}
