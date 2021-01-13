package com.telepathicgrunt.world_blender.mixin;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

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