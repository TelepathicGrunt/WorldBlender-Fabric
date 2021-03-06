package com.telepathicgrunt.worldblender.mixin.dimensions;

import com.telepathicgrunt.worldblender.WBIdentifiers;
import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.dimension.AltarManager;
import com.telepathicgrunt.worldblender.utils.ServerWorldAccess;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldAccess {

	@Mutable
	@Final
	@Shadow
	private EnderDragonFight enderDragonFight;

	@Unique
	public AltarManager ALTAR = null;

	@Override
	public AltarManager getAltar() {
		return ALTAR;
	}

	@Inject(method = "<init>",
			at = @At(value = "TAIL"))
	private void setupWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long seed, List<Spawner> list, boolean bl2, CallbackInfo ci) {

		if(registryKey.getValue().equals(WBIdentifiers.MOD_DIMENSION_ID) &&
				WorldBlender.WB_CONFIG.WBDimensionConfig.spawnEnderDragon)
		{
			((DimensionTypeAccessor)dimensionType).wb_setEnderDragonFight(true);
			enderDragonFight = new EnderDragonFight((ServerWorld)(Object)this, server.getSaveProperties().getGeneratorOptions().getSeed(), server.getSaveProperties().getDragonFight());
		}

		ALTAR = new AltarManager((ServerWorld)(Object)this);
	}


	//Generate altar here in our dimension
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(value = "HEAD")
	)
	private void tickAltar(CallbackInfo ci) {
		if(((ServerWorld)(Object)this).getRegistryKey().getValue().equals(WBIdentifiers.MOD_DIMENSION_ID))
			ALTAR.tick();
	}
}