package com.telepathicgrunt.worldblender.mixin.dimensions;

import com.telepathicgrunt.worldblender.WBIdentifiers;
import com.telepathicgrunt.worldblender.dimension.EnderDragonFightModification;
import com.telepathicgrunt.worldblender.utils.ServerWorldAccess;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {

	@Shadow
	@Final
	private ServerWorld world;

	/*
	 * Reduce the laggy chunk load by letting the Altar class do a smaller chunk load
	 */
	@Inject(
			method = "loadChunks()Z",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void loadSmallerChunks(CallbackInfoReturnable<Boolean> cir) {
		if(world.getRegistryKey().equals(WBIdentifiers.WB_WORLD_KEY)){
			if(((ServerWorldAccess)world).getAltar().isAltarMade()){
				cir.setReturnValue(true);
			}
			else{
				cir.setReturnValue(false);
			}
		}
	}


	/*
	 * Skip doing the laggy chunk checks. We will do a different check for portal in findEndPortal
	 */
	@Inject(
			method = "worldContainsEndPortal()Z",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void worldContainsEndPortal(CallbackInfoReturnable<Boolean> cir) {
		if(world.getRegistryKey().equals(WBIdentifiers.WB_WORLD_KEY)){
			cir.setReturnValue(false);
		}
	}


	/*
	 * Loads less chunks now instead of vanilla
	 * Needed so that the portal check does not recognize a pattern in the
	 * Bedrock floor of World Blender's dimension as if it is an End Podium.
	 *
	 * This was the cause of End Podium and Altar not spawning in WB dimension randomly.
	 *
	 * We also moved the code into it's own class so if it crash or lags, my mod's class shows up in stacktrace.
	 */
	@Inject(
			method = "findEndPortal()Lnet/minecraft/block/pattern/BlockPattern$Result;",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void findEndPortal(CallbackInfoReturnable<BlockPattern.Result> cir) {
		if(world.getRegistryKey().equals(WBIdentifiers.WB_WORLD_KEY)){
			BlockPattern.Result result = EnderDragonFightModification.findEndPortal((EnderDragonFight)(Object)this, cir.getReturnValue());
			cir.setReturnValue(result);
		}
	}
}
