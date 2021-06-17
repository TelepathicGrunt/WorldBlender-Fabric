package com.telepathicgrunt.worldblender.blocks;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class WBBlocks
{
	public static final Block WORLD_BLENDER_PORTAL = new WBPortalBlock();
	public static final BlockEntityType<WBPortalBlockEntity> WORLD_BLENDER_PORTAL_BE = FabricBlockEntityTypeBuilder.create(WBPortalBlockEntity::new, WORLD_BLENDER_PORTAL).build(null);

	public static void register()
	{
		Registry.register(Registry.BLOCK, new Identifier(WorldBlender.MODID, "world_blender_portal"), WORLD_BLENDER_PORTAL);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(WorldBlender.MODID, "world_blender_portal"), WORLD_BLENDER_PORTAL_BE);
	}

}