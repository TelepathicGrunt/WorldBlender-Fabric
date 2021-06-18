package com.telepathicgrunt.worldblender.mixin.dimensions;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderDragonFight.class)
public interface EnderDragonFightAccessor {

    @Accessor("world")
    ServerWorld worldblender_getWorld();

    @Accessor("endPortalPattern")
    BlockPattern worldblender_getEndPortalPattern();

    @Accessor("exitPortalLocation")
    BlockPos worldblender_getExitPortalLocation();

    @Accessor("exitPortalLocation")
    void worldblender_setExitPortalLocation(BlockPos pos);
}