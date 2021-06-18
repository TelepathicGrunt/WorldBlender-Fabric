package com.telepathicgrunt.worldblender.mixin.dimensions;

import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {

    @Mutable
    @Accessor("hasEnderDragonFight")
    void worldblender_setEnderDragonFight(boolean hasDragon);
}