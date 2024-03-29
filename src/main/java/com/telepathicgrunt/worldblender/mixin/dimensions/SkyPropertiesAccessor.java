package com.telepathicgrunt.worldblender.mixin.dimensions;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionEffects.class)
public interface SkyPropertiesAccessor {

    @Accessor("BY_IDENTIFIER")
    static Object2ObjectMap<Identifier, DimensionEffects> worldblender_getBY_IDENTIFIER() {
        throw new UnsupportedOperationException();
    }
}