package com.telepathicgrunt.world_blender.features;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.telepathicgrunt.worldblender.WorldBlender;
import net.telepathicgrunt.worldblender.blocks.WBBlocks;
import net.telepathicgrunt.worldblender.blocks.WBPortalTileEntity;
import net.telepathicgrunt.worldblender.dimension.WBDimensionRegistration;

import java.util.Random;
import java.util.function.Function;


public class WBPortalAltar extends Feature<NoFeatureConfig>
{
	private static PlacementSettings placementSettings = (new PlacementSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(false).setChunk((ChunkPos) null);
	
	public WBPortalAltar(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory)
	{
		super(configFactory);
	}
	

	@Override
	public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> changedBlock, Random rand, BlockPos position, NoFeatureConfig config)
	{
		//only world origin chunk allows generation
		if (world.getDimension().getType() != WBDimensionRegistration.worldblender() || position.getX() >> 4 != 0 || position.getZ() >> 4 != 0)
		{
			return false;
		}
		
		TemplateManager templatemanager = ((ServerWorld) world.getWorld()).getSaveHandler().getStructureTemplateManager();
		Template template = templatemanager.getTemplate(new ResourceLocation(WorldBlender.MODID + ":world_blender_portal_altar"));

		if (template == null)
		{
			WorldBlender.LOGGER.warn("world blender portal altar NTB does not exist!");
			return false;
		}
		
		BlockPos.Mutable finalPosition = new BlockPos.Mutable(world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, position));
		
		//go past trees to world surface
		BlockState blockState = world.getBlockState(finalPosition);
		while(finalPosition.getY() > 12 && (!blockState.isSolid() || blockState.getMaterial() == Material.WOOD)) {
			finalPosition.move(Direction.DOWN);
			blockState = world.getBlockState(finalPosition);
		}
		
		world.setBlockState(finalPosition.down(), Blocks.AIR.getDefaultState(), 3);
		template.addBlocksToWorld(world, finalPosition.add(-5, -2, -5), placementSettings);
		finalPosition.move(Direction.DOWN);
		world.setBlockState(finalPosition, WBBlocks.WORLD_BLENDER_PORTAL.get().getDefaultState(), 3); //extra check to make sure portal is placed

		//make portal block unremoveable in altar
		if(world.getTileEntity(finalPosition) != null && world.getTileEntity(finalPosition) instanceof WBPortalTileEntity)
			((WBPortalTileEntity)world.getTileEntity(finalPosition)).makeNotRemoveable();
		
		
		return true;

	}

}
