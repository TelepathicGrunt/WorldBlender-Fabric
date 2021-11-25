package com.telepathicgrunt.worldblender.dimension;

import com.telepathicgrunt.worldblender.features.WBFeatures;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;


public class AltarManager {
	private boolean altarMade;
	private final ServerWorld world;

	public AltarManager(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.altarMade = WBWorldSavedData.get(serverWorld).getWBAltarState();
	}

	public boolean isAltarMade(){
		return altarMade;
	}

	public void tick() {
		if (!this.altarMade) {
			boolean flag = this.isWorldOriginTicking();
			if(flag) {
				WBFeatures.WB_PORTAL_ALTAR.generate(new FeatureContext<>(this.world, this.world.getChunkManager().getChunkGenerator(), this.world.random, new BlockPos(0, 255, 0), FeatureConfig.DEFAULT));
				this.altarMade = true;
				this.saveWBAltarData(this.world);
			}
		}
	}

	private boolean isWorldOriginTicking() {
		for (int x = -1; x <= 0; ++x) {
			for (int z = -1; z <= 0; ++z) {
				Chunk chunk = this.world.getChunk(x, z, ChunkStatus.FULL, false);
				if (!(chunk instanceof WorldChunk)) {
					return false;
				}

				ChunkHolder.LevelType levelType = ((WorldChunk) chunk).getLevelType();
				if (!levelType.isAfter(ChunkHolder.LevelType.TICKING)) {
					return false;
				}
			}
		}

		return true;
	}
	
	public void saveWBAltarData(World world) {
		WBWorldSavedData.get(world).setWBAltarState(this.altarMade);
		WBWorldSavedData.get(world).markDirty();
	}
}
