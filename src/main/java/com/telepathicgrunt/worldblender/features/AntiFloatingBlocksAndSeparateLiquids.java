package com.telepathicgrunt.worldblender.features;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class AntiFloatingBlocksAndSeparateLiquids extends Feature<DefaultFeatureConfig> {

    public AntiFloatingBlocksAndSeparateLiquids() {
        super(DefaultFeatureConfig.CODEC);
    }

    private static final Map<MapColor, Block> COLOR_MAP;

    static {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(MapColor.CLEAR, Blocks.TERRACOTTA);
        COLOR_MAP.put(MapColor.PALE_GREEN, Blocks.LIME_TERRACOTTA);
        COLOR_MAP.put(MapColor.PALE_YELLOW, Blocks.YELLOW_TERRACOTTA);
        COLOR_MAP.put(MapColor.WHITE_GRAY, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MapColor.BRIGHT_RED, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MapColor.PALE_PURPLE, Blocks.MAGENTA_TERRACOTTA);
        COLOR_MAP.put(MapColor.IRON_GRAY, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MapColor.DARK_GREEN, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MapColor.WHITE, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MapColor.LIGHT_BLUE_GRAY, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.DIRT_BROWN, Blocks.BROWN_TERRACOTTA);
        COLOR_MAP.put(MapColor.STONE_GRAY, Blocks.CYAN_TERRACOTTA);
        COLOR_MAP.put(MapColor.WATER_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.OAK_TAN, Blocks.TERRACOTTA);
        COLOR_MAP.put(MapColor.OFF_WHITE, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MapColor.ORANGE, Blocks.ORANGE_TERRACOTTA);
        COLOR_MAP.put(MapColor.MAGENTA, Blocks.MAGENTA_TERRACOTTA);
        COLOR_MAP.put(MapColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.YELLOW, Blocks.YELLOW_TERRACOTTA);
        COLOR_MAP.put(MapColor.LIME, Blocks.LIME_TERRACOTTA);
        COLOR_MAP.put(MapColor.PINK, Blocks.PINK_TERRACOTTA);
        COLOR_MAP.put(MapColor.GRAY, Blocks.GRAY_TERRACOTTA);
        COLOR_MAP.put(MapColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_TERRACOTTA);
        COLOR_MAP.put(MapColor.CYAN, Blocks.CYAN_TERRACOTTA);
        COLOR_MAP.put(MapColor.PURPLE, Blocks.PURPLE_TERRACOTTA);
        COLOR_MAP.put(MapColor.BLUE, Blocks.BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.BROWN, Blocks.BROWN_TERRACOTTA);
        COLOR_MAP.put(MapColor.GREEN, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MapColor.RED, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MapColor.BLACK, Blocks.BLACK_TERRACOTTA);
        COLOR_MAP.put(MapColor.GOLD, Blocks.YELLOW_TERRACOTTA);
        COLOR_MAP.put(MapColor.DIAMOND_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.LAPIS_BLUE, Blocks.BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.EMERALD_GREEN, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MapColor.SPRUCE_BROWN, Blocks.TERRACOTTA);
        COLOR_MAP.put(MapColor.DULL_RED, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MapColor.DULL_PINK, Blocks.PINK_TERRACOTTA);
        COLOR_MAP.put(MapColor.DARK_RED, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MapColor.DARK_CRIMSON, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MapColor.TEAL, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MapColor.DARK_AQUA, Blocks.BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.DARK_DULL_PINK, Blocks.PINK_TERRACOTTA);
        COLOR_MAP.put(MapColor.BRIGHT_TEAL, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MapColor.DEEPSLATE_GRAY, Blocks.CYAN_TERRACOTTA);
        COLOR_MAP.put(MapColor.RAW_IRON_PINK, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MapColor.LICHEN_GREEN, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_WHITE, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_ORANGE, Blocks.ORANGE_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_BLACK, Blocks.BLACK_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_BLUE, Blocks.BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_BROWN, Blocks.BROWN_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_CYAN, Blocks.CYAN_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_LIGHT_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_GREEN, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_GRAY, Blocks.GRAY_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_LIGHT_GRAY, Blocks.LIGHT_GRAY_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_LIME, Blocks.LIME_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_MAGENTA, Blocks.MAGENTA_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_RED, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_YELLOW, Blocks.YELLOW_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_PINK, Blocks.PINK_TERRACOTTA);
        COLOR_MAP.put(MapColor.TERRACOTTA_PURPLE, Blocks.PURPLE_TERRACOTTA);
    }

    private static final Set<Material> REPLACEABLE_MATERIALS;

    static {
        REPLACEABLE_MATERIALS = new HashSet<>();
        REPLACEABLE_MATERIALS.add(Material.AIR);
        REPLACEABLE_MATERIALS.add(Material.STRUCTURE_VOID);
        REPLACEABLE_MATERIALS.add(Material.REPLACEABLE_PLANT);
        REPLACEABLE_MATERIALS.add(Material.CARPET);
        REPLACEABLE_MATERIALS.add(Material.CACTUS);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context){
        //this feature is completely turned off.
        if (!WorldBlender.WB_CONFIG.WBDimensionConfig.preventFallingBlocks &&
			!WorldBlender.WB_CONFIG.WBDimensionConfig.containFloatingLiquids &&
			!WorldBlender.WB_CONFIG.WBDimensionConfig.preventLavaTouchingWater)
        {
			return false;
		}

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState currentBlockstate;
		BlockState neighboringBlockstate;
        BlockState lastBlockstate = Blocks.STONE.getDefaultState();
		boolean setblock;
		int xChunkOrigin = ((context.getOrigin().getX() >> 4) << 4);
		int zChunkOrigin = ((context.getOrigin().getZ() >> 4) << 4);
		Chunk cachedChunk = context.getWorld().getChunk(xChunkOrigin >> 4, zChunkOrigin >> 4);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
				setblock = false;
                mutable.set(context.getOrigin().getX() + x, 0, context.getOrigin().getZ() + z);
                int maxHeight = Math.max(context.getWorld().getTopY(Heightmap.Type.WORLD_SURFACE, mutable.getX(), mutable.getZ()), context.getGenerator().getSeaLevel());
                maxHeight = Math.max(context.getWorld().getTopY(Heightmap.Type.WORLD_SURFACE, mutable.getX() + 1, mutable.getZ()), maxHeight);
                maxHeight = Math.max(context.getWorld().getTopY(Heightmap.Type.WORLD_SURFACE, mutable.getX(), mutable.getZ() + 1), maxHeight);
                maxHeight = Math.max(context.getWorld().getTopY(Heightmap.Type.WORLD_SURFACE, mutable.getX() - 1, mutable.getZ()), maxHeight);
                maxHeight = Math.max(context.getWorld().getTopY(Heightmap.Type.WORLD_SURFACE, mutable.getX(), mutable.getZ() - 1), maxHeight);

                mutable.move(Direction.UP, maxHeight);

                //checks the column downward
                for (; mutable.getY() >= context.getGenerator().getMinimumY(); mutable.move(Direction.DOWN)) {
                    currentBlockstate = context.getWorld().getBlockState(mutable);

					// current block is a lava-tagged fluid
					if (WorldBlender.WB_CONFIG.WBDimensionConfig.preventLavaTouchingWater &&
						currentBlockstate.getFluidState().isIn(FluidTags.LAVA))
					{
						for (Direction face : Direction.values()) {
							mutable.move(face);
							if(cachedChunk.getPos().x != mutable.getX() >> 4 || cachedChunk.getPos().z != mutable.getZ() >> 4){
								neighboringBlockstate = context.getWorld().getBlockState(mutable);
							}
							else{
								neighboringBlockstate = cachedChunk.getBlockState(mutable);
							}
							mutable.move(face.getOpposite());

							if (neighboringBlockstate.getFluidState().isIn(FluidTags.WATER)) {
                                context.getWorld().setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 2);
								setblock = true;
								break;
							}
						}
					}

					if(!setblock) {
						//current block is a block that liquids can break. time to check if we need to replace this block
						if (REPLACEABLE_MATERIALS.contains(currentBlockstate.getMaterial())) {
							//if above block was a fallible block, place a solid block below
							setblock = preventfalling(context.getWorld(), cachedChunk, mutable, lastBlockstate, currentBlockstate);
							if(!setblock){
								//if neighboring block is a liquid block, place a solid block next to it
								liquidContaining(context.getWorld(), cachedChunk, mutable, lastBlockstate, currentBlockstate);
							}
						}
						else if(!currentBlockstate.isOpaque() && !currentBlockstate.getFluidState().isEmpty()) {
							//if above block was a fallible block, place a solid block below
							preventfalling(context.getWorld(), cachedChunk, mutable, lastBlockstate, currentBlockstate);
						}
					}

                    //saves our current block to the last blockstate before we move down one.
                    lastBlockstate = currentBlockstate;
                }
            }
        }

        return true;

    }

    /**
     * Will place Terracotta block at mutable position if above block is a FallingBlock
     *
     * @param world          - world we are in
     * @param mutable        - current position
     * @param lastBlockstate - must be the above blockstate when passed in
     */
    private static boolean preventfalling(ServerWorldAccess world, Chunk cachedChunk, BlockPos.Mutable mutable, BlockState lastBlockstate, BlockState currentBlockstate)
	{
        if (!WorldBlender.WB_CONFIG.WBDimensionConfig.preventFallingBlocks) return false;

        if (lastBlockstate.getBlock() instanceof FallingBlock) {
			setReplacementBlock(world, cachedChunk, mutable, lastBlockstate, lastBlockstate, currentBlockstate);
			return true;
        }
        return false;
    }


    /**
     * Will place terracotta block at mutable position if above, north, west, east, or south is a liquid block
     *
     * @param world          - world we are in
     * @param mutable        - current position
     * @param lastBlockstate - must be the above blockstate when passed in
     */
    private static boolean liquidContaining(ServerWorldAccess world, Chunk cachedChunk, BlockPos.Mutable mutable, BlockState lastBlockstate, BlockState currentBlockstate)
	{
        if (!WorldBlender.WB_CONFIG.WBDimensionConfig.containFloatingLiquids) return false;

        boolean touchingLiquid = false;
        BlockState neighboringBlockstate = null;


        //if above is liquid, we need to contain it
        if (!lastBlockstate.getFluidState().isEmpty()) {
            touchingLiquid = true;
            neighboringBlockstate = lastBlockstate;
        }
        //if side is liquid, we need to contain it
        else {
            for (Direction face : Direction.Type.HORIZONTAL) {
				mutable.move(face);
				if(cachedChunk.getPos().x != mutable.getX() >> 4 || cachedChunk.getPos().z != mutable.getZ() >> 4){
					neighboringBlockstate = world.getBlockState(mutable);
				}
				else{
					neighboringBlockstate = cachedChunk.getBlockState(mutable);
				}
				mutable.move(face.getOpposite());

                if (!neighboringBlockstate.getFluidState().isEmpty()) {
                    touchingLiquid = true;
                    break;
                }
            }
        }


        if (touchingLiquid) {
			setReplacementBlock(world, cachedChunk, mutable, lastBlockstate, neighboringBlockstate, currentBlockstate);
			return true;
        }
        return false;
    }

	private static void setReplacementBlock(ServerWorldAccess world, Chunk cachedChunk, BlockPos.Mutable mutable, BlockState lastBlockstate, BlockState neighboringBlockstate, BlockState currentBlockstate) {
		MapColor targetMaterial = neighboringBlockstate.getMapColor(world, mutable);
		if(!COLOR_MAP.containsKey(targetMaterial)) {
			if(currentBlockstate.hasBlockEntity()){
				world.setBlockState(mutable, Blocks.CYAN_TERRACOTTA.getDefaultState(), 2);
			}
			else{
				cachedChunk.setBlockState(mutable, Blocks.CYAN_TERRACOTTA.getDefaultState(), false);
			}
		}
		else {
			if(currentBlockstate.hasBlockEntity()) {
				world.setBlockState(mutable, COLOR_MAP.get(targetMaterial).getDefaultState(), 2);
			}
			else{
				cachedChunk.setBlockState(mutable, COLOR_MAP.get(targetMaterial).getDefaultState(), false);
			}
		}
	}
}
