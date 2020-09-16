package com.telepathicgrunt.world_blender.dimension;

import com.telepathicgrunt.world_blender.WorldBlender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.UUID;


public class WBWorldSavedData extends PersistentState
{
	private static final String ALTAR_DATA = WorldBlender.MODID + "AltarMade";
	private static final WBWorldSavedData CLIENT_DUMMY = new WBWorldSavedData();
	private boolean wbAltarMade;
	private boolean dragonDataSaved;
	private boolean dragonKilled;
	private boolean dragonPreviouslyKilled;
	private boolean dragonIsRespawning;
	private boolean generatedInitialFight;
	private BlockPos endAltarPosition;
	private UUID dragonUUID;

	public WBWorldSavedData()
	{
		super(ALTAR_DATA);
	}

	public WBWorldSavedData(String s)
	{
		super(s);
	}


	public static WBWorldSavedData get(World world)
	{
		if (!(world instanceof ServerWorld))
		{
			return CLIENT_DUMMY;
		}
		
		ServerWorld wbWorld = ((ServerWorld)world).getServer().getWorld(WBDimensionRegistration.worldblender());
		PersistentStateManager storage = wbWorld.getPersistentStateManager();
		return storage.getOrCreate(WBWorldSavedData::new, ALTAR_DATA);
	}
	
	

	@Override
	public void fromTag(CompoundTag data)
	{
		wbAltarMade = data.getBoolean("wbAltarMade");
		dragonDataSaved = data.getBoolean("dragonDataSaved");
		dragonKilled = data.getBoolean("dragonKilled");
		dragonPreviouslyKilled = data.getBoolean("dragonPreviouslyKilled");
		dragonIsRespawning = data.getBoolean("dragonIsRespawning");
		generatedInitialFight = data.getBoolean("generatedInitialFight");
		endAltarPosition = new BlockPos(data.getInt("endAltarPositionX"), data.getInt("endAltarPositionY"), data.getInt("endAltarPositionZ"));
		dragonUUID = data.getUuid("dragonUUID");
	}

	@Override
	public CompoundTag toTag(CompoundTag data)
	{
		data.putBoolean("wbAltarMade", wbAltarMade);
		data.putBoolean("dragonDataSaved", dragonDataSaved);
		data.putBoolean("dragonKilled", dragonKilled);
		data.putBoolean("dragonPreviouslyKilled", dragonPreviouslyKilled);
		data.putBoolean("dragonIsRespawning", dragonIsRespawning);
		data.putBoolean("generatedInitialFight", generatedInitialFight);
		if(endAltarPosition != null)
		{
			data.putInt("endAltarPositionX", endAltarPosition.getX());
			data.putInt("endAltarPositionY", endAltarPosition.getY());
			data.putInt("endAltarPositionZ", endAltarPosition.getZ());
		}
		if(dragonUUID != null) data.putUuid("dragonUUID", dragonUUID);
		return data;
	}

	public void setWBAltarState(boolean state) 
	{
		this.wbAltarMade = state;
	}
	
	public boolean getWBAltarState() 
	{
		return this.wbAltarMade;
	}
	
	public boolean isDragonKilled()
	{
		return this.dragonKilled;
	}

	public void setDragonKilled(boolean dragonKilled)
	{
		this.dragonKilled = dragonKilled;
	}

	public UUID getDragonUUID()
	{
		return this.dragonUUID;
	}

	public void setDragonUUID(UUID dragonUUID)
	{
		this.dragonUUID = dragonUUID;
	}

	public boolean isDragonDataSaved()
	{
		return this.dragonDataSaved;
	}

	public void setDragonDataSaved(boolean dragonDataSaved)
	{
		this.dragonDataSaved = dragonDataSaved;
	}

	public boolean isDragonPreviouslyKilled()
	{
		return this.dragonPreviouslyKilled;
	}

	public void setDragonPreviouslyKilled(boolean dragonPreviouslyKilled)
	{
		this.dragonPreviouslyKilled = dragonPreviouslyKilled;
	}

	public boolean isDragonRespawning()
	{
		return this.dragonIsRespawning;
	}

	public void setDragonRespawning(boolean dragonIsRespawning)
	{
		this.dragonIsRespawning = dragonIsRespawning;
	}

	public BlockPos getEndAltarPosition()
	{
		return this.endAltarPosition;
	}

	public void setEndAltarPosition(BlockPos endAltarPosition)
	{
		this.endAltarPosition = endAltarPosition;
	}

	public boolean isInitialFightGenerated()
	{
		return generatedInitialFight;
	}

	public void setGeneratedInitialFight(boolean generatedInitialFightIn)
	{
		this.generatedInitialFight = generatedInitialFightIn;
	}

}