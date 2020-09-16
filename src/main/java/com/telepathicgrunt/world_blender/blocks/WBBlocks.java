package com.telepathicgrunt.world_blender.blocks;

import com.telepathicgrunt.world_blender.WorldBlender;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.telepathicgrunt.worldblender.WorldBlender;


public class WBBlocks
{
	public static final Block WORLD_BLENDER_PORTAL = new WBPortalBlock();
	public static final BlockEntityType<WBPortalBlockEntity> WORLD_BLENDER_PORTAL_BE = BlockEntityType.Builder.create(WBPortalBlockEntity::new, WORLD_BLENDER_PORTAL).build(null));

	public static void registerAll()
	{
		Registry.register(Registry.BLOCK, new Identifier(WorldBlender.MODID, "world_blender_portal"), WORLD_BLENDER_PORTAL);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(WorldBlender.MODID, "world_blender_portal"), WORLD_BLENDER_PORTAL_BE);
	}

}