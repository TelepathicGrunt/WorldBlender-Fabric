package com.telepathicgrunt.world_blender.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.carver.Carver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(EnderDragonFight.class)
public interface EnderDragonFightAccessor {

    @Accessor("world")
    ServerWorld wb_getworld();

    @Accessor("endPortalPattern")
    BlockPattern wb_getendPortalPattern();

    @Accessor("exitPortalLocation")
    BlockPos wb_getexitPortalLocation();

    @Accessor("exitPortalLocation")
    void wb_setexitPortalLocation(BlockPos pos);
}