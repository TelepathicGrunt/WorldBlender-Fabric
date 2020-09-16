package com.telepathicgrunt.world_blender.dimension;

import com.telepathicgrunt.world_blender.WorldBlender;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.function.BiFunction;


@EventBusSubscriber(modid = WorldBlender.MODID, bus = Bus.MOD)
public class WBDimensionRegistration {

	public static final ModDimension WORLD_BLENDER_DIMENSION = new ModDimension() {
        @Override
        public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
            return WBDimension::new;
        }
    };

    private static final Identifier WORLD_BLENDER_DIMENSION_RL = new Identifier(WorldBlender.MODID, "world_blender");
	
    
    public static void registerDimensions(RegisterDimensionsEvent event) {
        if (DimensionType.byId(WORLD_BLENDER_DIMENSION_RL) == null) {
            DimensionManager.registerDimension(WORLD_BLENDER_DIMENSION_RL, WORLD_BLENDER_DIMENSION, null, true);
        }
    }
    
    @SubscribeEvent
    public static void registerModDimensions(RegistryEvent.Register<ModDimension> event) {
        RegUtil.generic(event.getRegistry()).add(WorldBlender.MODID, WORLD_BLENDER_DIMENSION);
    }

    public static DimensionType worldblender() {
        return DimensionType.byId(WORLD_BLENDER_DIMENSION_RL);
    }
    
}
