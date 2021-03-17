package com.telepathicgrunt.worldblender.entities;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WBEntities {

    public static final EntityType<ItemClearingEntity> ITEM_CLEARING_ENTITY = EntityType.Builder.<ItemClearingEntity>
                    create(ItemClearingEntity::new, SpawnGroup.MISC).setDimensions(0, 0).maxTrackingRange(0).build("item_clearing_entity");

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(WorldBlender.MODID, "item_clearing_entity"), ITEM_CLEARING_ENTITY);
    }
}
