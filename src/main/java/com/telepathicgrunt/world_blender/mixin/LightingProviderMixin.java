package com.telepathicgrunt.world_blender.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightingProvider.class)
public class LightingProviderMixin {

	/**
	 * Prevents lighting crashes that is next to impossible to debug. Special thanks to shartte for
	 * figuring out the cause and allowing me to use his mixin workaround!
	 *
	 * @author shartte - https://github.com/AppliedEnergistics/Applied-Energistics-2/pull/4935/files
	 * @reason Required to make sure structures that replaces light blocks does not crash servers
	 */
	@Inject(method = "addLightSource", at = @At("HEAD"), cancellable = true)
	public void onBlockEmissionIncrease(BlockPos blockPos, int lightLevel, CallbackInfo ci) {
		if (lightLevel == 0) {
			ci.cancel();
		}
	}
}
