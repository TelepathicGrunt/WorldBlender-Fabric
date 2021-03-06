package com.telepathicgrunt.worldblender.surfacebuilder;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.theblender.ConfigBlacklisting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SurfaceBlender {
	final List<SurfaceConfig> surfaces = new ArrayList<>();
	final double baseScale;
	
	public SurfaceBlender() {
		// default order of surface builders I want to start with always
		surfaces.add(SurfaceBuilder.NETHER_CONFIG);
		surfaces.add(SurfaceBuilder.END_CONFIG);
		
		if (WorldBlender.WB_CONFIG.WBBlendingConfig.allowVanillaSurfaces &&
			WorldBlender.WB_CONFIG.WBBlendingConfig.allowVanillaBiomeImport) {
			surfaces.add(SurfaceBuilder.GRASS_CONFIG);
			surfaces.add(SurfaceBuilder.PODZOL_CONFIG);
			surfaces.add(SurfaceBuilder.BADLANDS_CONFIG);
			surfaces.add(BlendedSurfaceBuilder.SAND_SAND_UNDERWATER_CONFIG);
			surfaces.add(SurfaceBuilder.MYCELIUM_CONFIG);
			surfaces.add(new TernarySurfaceConfig(Blocks.SNOW_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState()));
			surfaces.add(SurfaceBuilder.CRIMSON_NYLIUM_CONFIG);
			surfaces.add(SurfaceBuilder.WARPED_NYLIUM_CONFIG);
			surfaces.add(SurfaceBuilder.BASALT_DELTA_CONFIG);
			surfaces.add(SurfaceBuilder.COARSE_DIRT_CONFIG);
			surfaces.add(SurfaceBuilder.GRAVEL_CONFIG);
		}
		
		// remove the surfaces that we disallow through blacklist but keep nether/end road
		for (int i = surfaces.size() - 1; i > 1; i--) {
			Block topBlock = surfaces.get(i).getTopMaterial().getBlock();
			boolean isBlacklisted = ConfigBlacklisting.isIdentifierBlacklisted(
				ConfigBlacklisting.BlacklistType.SURFACE_BLOCK,
				Registry.BLOCK.getId(topBlock)
			);
			if (isBlacklisted) {
				surfaces.remove(i);
			}
		}
		
		baseScale = 0.6D / surfaces.size();
	}
	
	public void save() {
		BlendedSurfaceBuilder.blender = this;
	}
	
	/**
	 Adds the surface to allSurfaceList for surface gen later
	 */
	public void addIfMissing(SurfaceConfig config) {
		boolean alreadyPresent = surfaces.stream().anyMatch(existing -> areEquivalent(existing, config));
		if (alreadyPresent) return;
		
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
		return ((TernarySurfaceConfig) surface).getUnderwaterMaterial();
	}
	
	private static boolean areEquivalent(SurfaceConfig config1, SurfaceConfig config2) {
		if (config1.getTopMaterial() != config2.getTopMaterial()) return false;
		if (config1.getUnderMaterial() != config2.getUnderMaterial()) return false;
		return getUnderwaterMaterial(config1) == getUnderwaterMaterial(config2);
	}
}
