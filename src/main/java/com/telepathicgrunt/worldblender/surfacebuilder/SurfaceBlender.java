package com.telepathicgrunt.worldblender.surfacebuilder;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.theblender.ConfigBlacklisting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SurfaceBlender {
	final List<SurfaceConfig> surfaces = new ArrayList<>();
	final Map<Integer, Boolean> undergroundBlocks = new HashMap<>();
	final double baseScale;
	
	public SurfaceBlender() {

		int index = 0;

		for(String rawStringEntry : WorldBlender.omegaConfig.biomeSurfacesLayerOrder){
			List<String> parsedStrings = Arrays.stream(rawStringEntry.replace(" ", "").split(",")).collect(Collectors.toList());

			if(parsedStrings.size() == 1){
				BlockState state = parseStringToState(parsedStrings.get(0), index);
				surfaces.add(new TernarySurfaceConfig(state, state, state));
				undergroundBlocks.put(index, true);
				index++;
			}
			else if(parsedStrings.size() == 2){
				BlockState state = parseStringToState(parsedStrings.get(0), index);
				BlockState state2 = parseStringToState(parsedStrings.get(1), index);
				surfaces.add(new TernarySurfaceConfig(state, state2, state2));
				undergroundBlocks.put(index, false);
				index++;
			}
			else if(parsedStrings.size() > 2){
				BlockState state = parseStringToState(parsedStrings.get(0), index);
				BlockState state2 = parseStringToState(parsedStrings.get(1), index);
				BlockState state3 = parseStringToState(parsedStrings.get(2), index);
				surfaces.add(new TernarySurfaceConfig(state, state2, state3));
				undergroundBlocks.put(index, false);
				index++;
			}
		}
		
		baseScale = 0.6D / surfaces.size();
	}
	
	public void save() {
		BlendedSurfaceBuilder.blender = this;
	}


	private BlockState parseStringToState(String blockId, int surfaceIndex){
		Optional<Block> state = Registry.BLOCK.getOrEmpty(new Identifier(blockId));

		if(state.isEmpty()){
			WorldBlender.LOGGER.warn("Surface Block Creator: Unable to parse {} into a block from the block registry. " +
					"The layer it would've been is layer {}. " +
					"Check biomeSurfacesLayerOrder config option to make sure all blocks are not mispelled.",
					blockId,
					surfaceIndex);

			return Blocks.AIR.getDefaultState();
		}
		return state.get().getDefaultState();
	}
	
	/**
	 Adds the surface to allSurfaceList for surface gen later
	 */
	public void addIfMissing(SurfaceConfig config) {
		boolean alreadyPresent = surfaces.stream().anyMatch(existing -> areEquivalent(existing, config));
		if (alreadyPresent) return;

		if(!Registry.BLOCK.getId(config.getUnderMaterial().getBlock()).getNamespace().equals("minecraft")){
			undergroundBlocks.put(surfaces.size(), true);
		}else{
			undergroundBlocks.put(surfaces.size(), false);
		}
		surfaces.add(config);
	}
	
	// Returns what vanilla carvers should carve through so they don't get cut off by unique blocks added to surfacebuilder config
	public Set<Block> blocksToCarve() {
		Set<Block> carvableBlocks = new HashSet<>();
		carvableBlocks.add(Blocks.NETHERRACK);
		carvableBlocks.add(Blocks.END_STONE);
		
		// adds underground modded blocks to carve through
		for (SurfaceConfig surface : surfaces) {
			BlockState underwaterMaterial = getUnderwaterMaterial(surface);
			if (underwaterMaterial == null) continue;
			Block underwaterBlock = underwaterMaterial.getBlock();
			boolean isVanilla = Registry.BLOCK.getId(underwaterBlock).getNamespace().equals("minecraft");
			if (isVanilla) continue;
			carvableBlocks.add(underwaterBlock);
		}
		
		return carvableBlocks;
	}

	private static BlockState getUnderwaterMaterial(SurfaceConfig surface) {
		if (!(surface instanceof TernarySurfaceConfig)) return null;
		return surface.getUnderwaterMaterial();
	}
	
	private static boolean areEquivalent(SurfaceConfig config1, SurfaceConfig config2) {
		if (config1.getTopMaterial() != config2.getTopMaterial()) return false;
		if (config1.getUnderMaterial() != config2.getUnderMaterial()) return false;
		return getUnderwaterMaterial(config1) == getUnderwaterMaterial(config2);
	}
}
