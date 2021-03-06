package com.telepathicgrunt.worldblender.configs;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "world_blender")
public class WBConfig implements ConfigData {

    @ConfigEntry.Category("blending")
    @ConfigEntry.Gui.TransitiveObject
    public WBBlendingConfigs WBBlendingConfig = new WBBlendingConfigs();

    @ConfigEntry.Category("dimension")
    @ConfigEntry.Gui.TransitiveObject
    public WBDimensionConfigs WBDimensionConfig = new WBDimensionConfigs();

    @ConfigEntry.Category("portal")
    @ConfigEntry.Gui.TransitiveObject
    public WBPortalConfigs WBPortalConfig = new WBPortalConfigs();
}
