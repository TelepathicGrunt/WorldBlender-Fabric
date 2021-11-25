package com.telepathicgrunt.worldblender.entities;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WBEntities {

    public static final EntityType<ItemClearingEntity> ITEM_CLEARING_ENTITY = FabricEntityTypeBuilder.<ItemClearingEntity>create(SpawnGroup.MISC, ItemClearingEntity::new).dimensions(EntityDimensions.fixed(0, 0)).trackRangeChunks(0).build();

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(WorldBlender.MODID, "item_clearing_entity"), ITEM_CLEARING_ENTITY);
    }
}
