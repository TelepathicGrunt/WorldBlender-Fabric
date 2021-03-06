package com.telepathicgrunt.worldblender.features;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.minecraft.block.*;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.*;


public class AntiFloatingBlocksAndSeparateLiquids extends Feature<DefaultFeatureConfig> {

    public AntiFloatingBlocksAndSeparateLiquids() {
        super(DefaultFeatureConfig.CODEC);
    }

    private static final Map<MaterialColor, Block> COLOR_MAP;

    static {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(MaterialColor.CLEAR, Blocks.TERRACOTTA);
        COLOR_MAP.put(MaterialColor.ORANGE, Blocks.ORANGE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.BLACK, Blocks.BLACK_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.BLUE, Blocks.BLUE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.BROWN, Blocks.BROWN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.CLAY, Blocks.CYAN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.CYAN, Blocks.CYAN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.DIAMOND, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.DIRT, Blocks.BROWN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.EMERALD, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.FOLIAGE, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.GOLD, Blocks.YELLOW_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.GRASS, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.GRAY, Blocks.GRAY_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.GREEN, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.ICE, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.IRON, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.LAPIS, Blocks.BLUE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.LIME, Blocks.LIME_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.MAGENTA, Blocks.MAGENTA_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.NETHER, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.SPRUCE, Blocks.BLACK_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.PINK, Blocks.PINK_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.PURPLE, Blocks.PURPLE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.QUARTZ, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.RED, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.SAND, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.WHITE, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.STONE, Blocks.CYAN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.LAVA, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.WATER, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.WOOD, Blocks.TERRACOTTA);
        COLOR_MAP.put(MaterialColor.WEB, Blocks.WHITE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.YELLOW, Blocks.YELLOW_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.ORANGE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.BLACK_TERRACOTTA, Blocks.BLACK_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.BLUE_TERRACOTTA, Blocks.BLUE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.BROWN_TERRACOTTA, Blocks.BROWN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.CYAN_TERRACOTTA, Blocks.CYAN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.LIGHT_BLUE_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.GREEN_TERRACOTTA, Blocks.GREEN_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.GRAY_TERRACOTTA, Blocks.GRAY_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.LIGHT_GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.LIME_TERRACOTTA, Blocks.LIME_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.MAGENTA_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.RED_TERRACOTTA, Blocks.RED_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.YELLOW_TERRACOTTA, Blocks.YELLOW_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.PINK_TERRACOTTA, Blocks.PINK_TERRACOTTA);
        COLOR_MAP.put(MaterialColor.PURPLE_TERRACOTTA, Blocks.PURPLE_TERRACOTTA);
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
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkgenerator, Random rand, BlockPos position, DefaultFeatureConfig config) {
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
		int xChunkOrigin = ((position.getX() >> 4) << 4);
		int zChunkOrigin = ((position.getZ() >> 4) << 4);
		Chunk cachedChunk = world.getChunk(xChunkOrigin >> 4, zChunkOrigin >> 4);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
				setblock = false;
                mutable.set(position.getX() + x, 0, position.getZ() + z);
                mutable.move(Direction.UP, Math.max(world.getTopY(Heightmap.Type.WORLD_SURFACE, mutable.getX(), mutable.getZ()), chunkgenerator.getSeaLevel()));

                //checks the column downward
                for (; mutable.getY() >= 0; mutable.move(Direction.DOWN)) {
                    currentBlockstate = world.getBlockState(mutable);

					// current block is a lava-tagged fluid
					if (WorldBlender.WB_CONFIG.WBDimensionConfig.preventLavaTouchingWater &&
						currentBlockstate.getFluidState().isIn(FluidTags.LAVA))
					{
						for (Direction face : Direction.values()) {
							mutable.move(face);
							if(cachedChunk.getPos().x != mutable.getX() >> 4 || cachedChunk.getPos().z != mutable.getZ() >> 4){
								neighboringBlockstate = world.getBlockState(mutable);
							}
							else{
								neighboringBlockstate = cachedChunk.getBlockState(mutable);
							}
							mutable.move(face.getOpposite());

							if (neighboringBlockstate.getFluidState().isIn(FluidTags.WATER)) {
								world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 2);
								setblock = true;
								break;
							}
						}
					}

					if(!setblock) {
						//current block is a block that liquids can break. time to check if we need to replace this block
						if (REPLACEABLE_MATERIALS.contains(currentBlockstate.getMaterial())) {
							//if above block was a fallible block, place a solid block below
							setblock = preventfalling(world, cachedChunk, mutable, lastBlockstate, currentBlockstate);
							if(!setblock){
								//if neighboring block is a liquid block, place a solid block next to it
								liquidContaining(world, cachedChunk, mutable, lastBlockstate, currentBlockstate);
							}
						}
						else if(!currentBlockstate.isOpaque() && !currentBlockstate.getFluidState().isEmpty()) {
							//if above block was a fallible block, place a solid block below
							preventfalling(world, cachedChunk, mutable, lastBlockstate, currentBlockstate);
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
		MaterialColor targetMaterial = neighboringBlockstate.getTopMaterialColor(world, mutable);
		if(!COLOR_MAP.containsKey(targetMaterial)) {
			if(currentBlockstate.getBlock().hasBlockEntity()){
				world.setBlockState(mutable, Blocks.CYAN_TERRACOTTA.getDefaultState(), 2);
			}
			else{
				cachedChunk.setBlockState(mutable, Blocks.CYAN_TERRACOTTA.getDefaultState(), false);
			}
		}
		else {
			if(currentBlockstate.getBlock().hasBlockEntity()) {
				world.setBlockState(mutable, COLOR_MAP.get(targetMaterial).getDefaultState(), 2);
			}
			else{
				cachedChunk.setBlockState(mutable, COLOR_MAP.get(targetMaterial).getDefaultState(), false);
			}
		}
	}
}
