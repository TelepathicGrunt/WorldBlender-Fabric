package com.telepathicgrunt.worldblender.mixin.worldgen;

import net.minecraft.util.collection.WeightedPicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedPicker.Entry.class)
public interface EntryAccessor {
    @Accessor
    int getWeight();
}
