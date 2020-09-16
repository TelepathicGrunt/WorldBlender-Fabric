package net.telepathicgrunt.worldblender.features;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.telepathicgrunt.worldblender.configs.WBConfig;


public class SeparateLavaAndWater extends Feature<DefaultFeatureConfig>
{

    public SeparateLavaAndWater(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
	super(configFactory);
    }


    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> changedBlock, Random rand, BlockPos position, DefaultFeatureConfig config) {
	// this feature is completely turned off.
	if (!WBConfig.preventLavaTouchingWater) return false;

	BlockPos.Mutable mutable = new BlockPos.Mutable();
	BlockState currentBlockstate = Blocks.STONE.getDefaultState();
	BlockState neighboringBlockstate;

	for (int x = 0; x < 16; x++) {
	    for (int z = 0; z < 16; z++) {
		mutable = new BlockPos.Mutable(position.getX() + x, 0, position.getZ() + z);
		mutable.setOffset(Direction.UP, Math.max(world.getTopY(Heightmap.Type.WORLD_SURFACE, mutable.getX(), mutable.getZ()), world.getSeaLevel()));

		// checks the column downward
		for (; mutable.getY() >= 0; mutable.setOffset(Direction.DOWN)) {
		    currentBlockstate = world.getBlockState(mutable);

		    // current block is a water-tagged fluid
		    if (currentBlockstate.getFluidState().matches(FluidTags.LAVA)) {

			for (Direction face : Direction.values()) {
			    neighboringBlockstate = world.getBlockState(mutable.offset(face));
			    if (neighboringBlockstate.getFluidState().matches(FluidTags.WATER)) {
				world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 2);
				break;
			    }
			}
		    }
		}
	    }
	}

	return true;

    }
}
