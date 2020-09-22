package com.telepathicgrunt.world_blender.mixin;

import com.telepathicgrunt.world_blender.WBIdentifiers;
import com.telepathicgrunt.world_blender.WorldBlender;
import com.telepathicgrunt.world_blender.dimension.AltarManager;
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
public class ServerWorldMixin {

	@Mutable
	@Final
	@Shadow
	private EnderDragonFight enderDragonFight;

	@Unique
	private AltarManager ALTAR = null;

	@Inject(method = "<init>",
			at = @At(value = "TAIL"))
	private void setupWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long seed, List<Spawner> list, boolean bl2, CallbackInfo ci) {

		if(registryKey.getValue().equals(WBIdentifiers.MOD_DIMENSION_ID) &&
				WorldBlender.WB_CONFIG.WBDimensionConfig.spawnEnderDragon)
		{
			((DimensionTypeAccessor)dimensionType).setEnderDragonFight(true);
			enderDragonFight = new EnderDragonFight((ServerWorld)(Object)this, server.getSaveProperties().getGeneratorOptions().getSeed(), server.getSaveProperties().getDragonFight());
		}

		ALTAR = new AltarManager((ServerWorld)(Object)this);
	}


	@Inject(
			method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(value = "HEAD")
	)
	private void tickAltar(CallbackInfo ci) {
		ALTAR.tick();
	}
}
