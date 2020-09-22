package com.telepathicgrunt.world_blender.utils;

import com.telepathicgrunt.world_blender.dimension.AltarManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface ServerWorldAccess
{
	AltarManager getAltar();
}
