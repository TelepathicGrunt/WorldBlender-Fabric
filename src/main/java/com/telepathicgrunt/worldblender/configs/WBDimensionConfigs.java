package com.telepathicgrunt.worldblender.configs;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "dimension")
public class WBDimensionConfigs implements ConfigData {

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Attempts to remove any nbt piece or structure being place at the bottom of the world.
             Best for floating island World Blender terrain
            """)
    public boolean removeWorldBottomStructures = false;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Attempts to remove the pillars from nether fortress and desert temples and the likes.
             Best for floating island World Blender terrain
            """)
    public boolean removeStructurePillars = false;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             The size of the different kinds of surfaces.
             Higher numbers means each surface will be larger but might make some surfaces harder to find.
             Lower numbers means the surfaces are smaller but could become too chaotic or small for some features to spawn on.
            """)
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100000)
    public double surfaceScale = 240D;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Determines which surfaces will make up the road through the dimension.
             See biomeSurfacesLayerOrder config for what surfaces will be part of the road.
             Example, 3 roadLayers means the first 3 surface entries in biomeSurfacesLayerOrder will make up the road.
             0 will mean no road at all.
            """)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100000)
    public int roadLayers = 2;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             The thickness of each road surface in the dimension.
            """)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
    public double roadThickeness = 0.025D;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             If true, the Enderdragon will spawn at world origin in the
             World Blender dimension. Once killed, the podium's portal
             will take you to the End where you can battle the End's Enderdragon.
            
             And yes, you can respawn the EnderDragon by placing 4 End Crystals
             on the edges of the Bedrock Podium.
            
             If set to false, the Enderdragon will not spawn.
             NOTE: Once the Enderdragon is spawned, changing this to false will not despawn the Enderdragon.
            """)
    public boolean spawnEnderDragon = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             If true, carvers (mainly vanilla caves and ravines) can now carve
             out Netherrack, End Stone, and some modded blocks as well.
            
             If turned off, you might see Vanilla caves and stuff gets cutoff
             by a wall of End Stone, Netherrack, or modded blocks.
            """)
    public boolean carversCanCarveMoreBlocks = true;


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Will try its best to place Terracotta blocks under all floating
             fallable blocks to prevent lag when the blocks begins to fall.
            """)
    public boolean preventFallingBlocks = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             This will also place Terracotta next to fluids to try and prevent.
             them from floating and then flowing downward like crazy.
            
             It isn't perfect but it does do mostly a good job with how
             messy and chaotic having all features and carvers together is.
            """)
    public boolean containFloatingLiquids = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Will place Obsidian to separate lava tagged fluids
             from water tagged fluids underground.
            """)
    public boolean preventLavaTouchingWater = true;

}
