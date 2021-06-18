package com.telepathicgrunt.worldblender.entities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ItemClearingEntityRendering extends EntityRenderer<ItemClearingEntity> implements FeatureRendererContext<ItemClearingEntity, SlimeEntityModel<ItemClearingEntity>> {

    public ItemClearingEntityRendering(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public SlimeEntityModel<ItemClearingEntity> getModel() {
        return null;
    }

    @Override
    public Identifier getTexture(ItemClearingEntity entity) {
        return null;
    }
}

