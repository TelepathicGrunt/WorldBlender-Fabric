package com.telepathicgrunt.worldblender.dimension;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;


public class WBWorldSavedData extends PersistentState
{
	private static final String ALTAR_DATA = WorldBlender.MODID + "AltarMade";
	private static final WBWorldSavedData CLIENT_DUMMY = new WBWorldSavedData();
	private boolean wbAltarMade;

	public WBWorldSavedData(boolean altarMade)
	{
		wbAltarMade = altarMade;
	}

	public WBWorldSavedData()
	{
		wbAltarMade = false;
	}

	public static WBWorldSavedData get(World world)
	{
		if (!(world instanceof ServerWorld))
		{
			return CLIENT_DUMMY;
		}

		PersistentStateManager storage = ((ServerWorld)world).getPersistentStateManager();
		return storage.getOrCreate(WBWorldSavedData::fromNbt, WBWorldSavedData::new, ALTAR_DATA);
	}

	public static WBWorldSavedData fromNbt(NbtCompound data) {
		return new WBWorldSavedData(data.getBoolean("WBAltarMade"));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound data)
	{
		data.putBoolean("WBAltarMade", wbAltarMade);
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
}