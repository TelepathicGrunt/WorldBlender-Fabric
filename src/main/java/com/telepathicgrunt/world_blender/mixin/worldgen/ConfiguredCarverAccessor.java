package com.telepathicgrunt.world_blender.mixin.worldgen;

import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConfiguredCarver.class)
public interface ConfiguredCarverAccessor{

    @Accessor("carver")
    <WC extends CarverConfig>
    Carver<WC> wb_getcarver();
}