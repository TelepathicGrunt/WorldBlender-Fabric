package com.telepathicgrunt.world_blender.mixin;

import com.telepathicgrunt.world_blender.WBIdentifiers;
import com.telepathicgrunt.world_blender.blocks.WBPortalBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {

	@Mutable
	@Final
	@Shadow
	private ServerWorld world;

	@Final
	@Shadow
	private BlockPattern endPortalPattern;

	@Mutable
	@Shadow
	private BlockPos exitPortalLocation;


	/**
	 * Skip doing the laggy chunk checks. We will do a different check for portal in findEndPortal
	 */
	@Inject(
			method = "worldContainsEndPortal()Z",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void worldContainsEndPortal(CallbackInfoReturnable<Boolean> cir) {
		if(this.world.getRegistryKey().equals(WBIdentifiers.WB_WORLD_KEY)){
			cir.setReturnValue(false);
		}
	}


	/*
	 * Needed so that the portal check does not recognize a pattern in the
	 * Bedrock floor of World Blender's dimension as if it is an End Podium.
	 *
	 * This was the cause of End Podium and Altar not spawning in WB dimension randomly.
	 */
	@Inject(
			method = "findEndPortal()Lnet/minecraft/block/pattern/BlockPattern$Result;",
			at = @At(value = "RETURN", target = "Lnet/minecraft/util/math/vector/Vector3i;getY()I"),
			cancellable = true
	)
	private void findEndPortal(CallbackInfoReturnable<BlockPattern.Result> cir) {
		if(world.getRegistryKey().getValue().equals(WBIdentifiers.MOD_DIMENSION_ID)){
			WorldChunk worldChunk = this.world.getChunk(0, 0);

			for(BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
				if (blockEntity instanceof WBPortalBlockEntity) {
					if(!((WBPortalBlockEntity) blockEntity).isRemoveable()){
						BlockPattern.Result blockpattern = this.endPortalPattern.searchAround(this.world, blockEntity.getPos());
						if (blockpattern != null) {
							BlockPos blockpos = blockpattern.translate(3, 7, 3).getBlockPos();
							if (this.exitPortalLocation == null && blockpos.getX() == 0 && blockpos.getZ() == 0) {
								this.exitPortalLocation = blockpos;
							}

							cir.setReturnValue(blockpattern);
						}
					}
				}
			}

			cir.setReturnValue(null); // Skip checking the bedrock layer
		}
	}
}
