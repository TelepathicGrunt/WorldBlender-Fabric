package com.telepathicgrunt.worldblender.mixin.worldgen;

import net.minecraft.block.Block;
import net.minecraft.world.gen.carver.Carver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(Carver.class)
public interface CarverAccessor {

    @Accessor("alwaysCarvableBlocks")
    Set<Block> wb_getalwaysCarvableBlocks();

    @Accessor("alwaysCarvableBlocks")
    void wb_setalwaysCarvableBlocks(Set<Block> blockSet);
}