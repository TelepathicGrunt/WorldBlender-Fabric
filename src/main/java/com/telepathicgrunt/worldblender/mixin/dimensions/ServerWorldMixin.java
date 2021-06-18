package com.telepathicgrunt.worldblender.mixin.dimensions;

import com.telepathicgrunt.worldblender.WorldBlender;
import com.telepathicgrunt.worldblender.dimension.AltarManager;
import com.telepathicgrunt.worldblender.dimension.WBBiomeProvider;
import com.telepathicgrunt.worldblender.utils.ServerWorldAccess;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldAccess {

	@Mutable
	@Final
	@Shadow
	private EnderDragonFight enderDragonFight;

	@Shadow
	public abstract ServerChunkManager getChunkManager();

	@Unique
	public AltarManager WORLDBLENDER_ALTAR = null;

	@Override
	public AltarManager worldblender_getAltar() {
		return WORLDBLENDER_ALTAR;
	}

	@Inject(method = "<init>",
			at = @At(value = "TAIL"))
	private void worldblender_setupWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long seed, List<Spawner> list, boolean bl2, CallbackInfo ci) {
		if(getChunkManager().getChunkGenerator().getBiomeSource() instanceof WBBiomeProvider){
			ServerWorld serverWorld = (ServerWorld)(Object)this;
			if(WorldBlender.WB_CONFIG.WBDimensionConfig.spawnEnderDragon) {
				((DimensionTypeAccessor)dimensionType).worldblender_setEnderDragonFight(true);
				enderDragonFight = new EnderDragonFight(serverWorld, server.getSaveProperties().getGeneratorOptions().getSeed(), server.getSaveProperties().getDragonFight());
			}

			WORLDBLENDER_ALTAR = new AltarManager(serverWorld);
		}
	}


	//Generate altar here in our dimension
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(value = "HEAD")
	)
	private void worldblender_tickAltar(CallbackInfo ci) {
		if(getChunkManager().getChunkGenerator().getBiomeSource() instanceof WBBiomeProvider) {
			WORLDBLENDER_ALTAR.tick();
		}
	}
}
