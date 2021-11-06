package com.telepathicgrunt.worldblender.configs;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

@Config(name = "blending")
public class WBBlendingConfigs implements ConfigData {

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             This option can let you blacklist entire biomes or mods to
             prevent any importing of any kind from them. You can also use
             terms to ban any biome that contains the terms too.
            
             To blacklist a mod's biome, you would enter the identifier
             for the biome. That means you need to enter the mod's ID first,
             then put a : (semicolon), and then the ID of the biome.
             For example, to blacklist just vanilla's Ice Spike biome, you
             would put in minecraft:ice_spike and nothing will be imported
             from that specific biome.
            
             If you want to blacklist an entire mod itself so no importing
             will happen for any of its biome, just enter the mod's ID and then
             put an * at the end.
            
             To blacklist by key terms, just enter the term alone such as "ocean"
             and all biomes with ocean in their name will not be imported.
             This uses Regex so you could do "cold\\w+plateau" to blacklist any name
             that starts with 'cold' and ends in 'plateau'.
            
             To blacklist by biome categories, just enter the category with a # in front like "#DESERT"
             and all biomes that are desert category will not be imported.
             The categories you can use are: DESERT, FOREST, SWAMP, ICY, TAIGA, EXTREME_HILLS,
             JUNGLE, MESA, PLAINS, SAVANNA, BEACH, RIVER, OCEAN, MUSHROOM, THE_END, NETHER, NONE
                                    
             NOTE: You can blacklist multiple things at a time. Just separate
             each entry with a , (comma). Here's an example blacklisting a mod
             a vanilla biome, and all mushroom category biomes at the same time:
             "ultra_amplified_dimension*, minecraft:jungle_edge, #MUSHROOM"
            """)
    public String blanketBlacklist = "ultra_amplified_dimension*";


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Blacklist features by key terms, mod ID, or their identifier
            
             To blacklist by key terms, just enter the term alone such as "tree"
             and all features with tree in their name will not be imported.
             This uses Regex so you could do "tall\\w+tree" to blacklist any name
             that starts with 'fire' and ends in 'tree'.
            
             To blacklist by mod ID, just enter the mod ID with an * on the end such as
             "ultra_amplified_dimension*" and all features from that mod will not be imported.
            
             To blacklist a single feature, enter the mod ID, then :, and then the
             feature's name. For example, "minecraft:icebergs" will prevent vanilla's
             icebergs from being imported but allow other mod's icebergs to be imported.
            
             NOTE: You can blacklist multiple things at a time. Just separate
             each entry with a , (comma). Here's an example blacklisting all trees
             and vanilla's icebergs:
             "tree, minecraft:iceberg"
            """)
    public String blacklistedFeatures =
                    "minecraft:basalt_blobs, " +
                    "minecraft:blackstone_blobs, " +
                    "betterend:purple_polypore_dense, " +
                    "betterend:twisted_umbrella_moss, " +
                    "betterend:umbrella_moss, " +
                    "betterend:sulphuric_lake, " +
                    "betterend:bubble_coral, " +
                    "betterend:bulb_moss, " +
                    "betterend:charnia_red, " +
                    "betterend:creeping_moss, " +
                    "betterend:end_lily, " +
                    "betterend:end_lake_normal, " +
                    "betterend:end_lake, " +
                    "betterend:desert_lake, " +
                    "betterend:end_lake_rare";


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Blacklist structures by key terms, mod ID, or their identifier
            
             To blacklist by key terms, just enter the term alone such as "temple"
             and all features with temple in their name will not be imported.
             This uses Regex so you could do "advanced\\w+village" to blacklist any name
             that starts with 'advanced' and ends in 'village'.
            
             To blacklist by mod ID, just enter the mod ID with an * on the end such as
             "ultra_amplified_dimension*" and all structures from that mod will not be imported.
            
             To blacklist a single feature, enter the mod ID, then :, and then the
             feature's name. For example, "minecraft:igloo" will prevent vanilla's
             igloos from being imported but allow other mod's igloos to be imported.
            
             NOTE: You can blacklist multiple things at a time. Just separate
             each entry with a , (comma). Here's an example blacklisting all temples
             and vanilla's igloos:
             "temple, minecraft:igloo"
            """)
    public String blacklistedStructures = "betterend:painted_mountain, betterend:mountain, betterend:megalake, betterend:megalake_small";


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Blacklist carvers by key terms, mod ID, or their identifier
             Not many mods register their carvers sadly so if a mod has a carver,
             it may not be imported into World Blender's dimension at all.
             
             To blacklist by key terms, just enter the term alone such as "cave"
             and all carvers with cave in their name will not be imported.
             This uses Regex so you could do "hot\\w+cavern" to blacklist any name
             that starts with 'hot' and ends in 'cavern'.
            
             To blacklist by mod ID, just enter the mod ID with an * on the end such as
             "ultra_amplified_dimension*" and all carvers from that mod will not be imported.
            
             To blacklist a single feature, enter the mod ID, then :, and then the
             feature's name. For example, "minecraft:underwater_canyon" will prevent
             vanilla's underwater canyons (ravines) from being imported. For underwater
             caves, use "minecraft:underwater_cave" to stop them from being imported.
            
             NOTE: You can blacklist multiple things at a time. Just separate
             each entry with a , (comma). Here's an example blacklisting all caves
             and vanilla's underwater canyons:
             "cave, minecraft:underwater_canyon"
            """)
    public String blacklistedCarvers = "";


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Blacklist natural spawning mobs by key terms,
             mod ID, or their identifier
            
             To blacklist by key terms, just enter the term alone such as "zombie"
             and all mobs with zombie in their name will not be imported.
             This uses Regex so you could do "turbo\\w+bat" to blacklist any name
             that starts with 'turbo' and ends in 'bat'.
            
             To blacklist by mod ID, just enter the mod ID with an * on the end such as
             "super_duper_mob_mod*" and all mobs from that mod will not be imported.
            
             To blacklist a single mob, enter the mod ID, then :, and then the
             mob's name. For example, "minecraft:ghast" will prevent
             vanilla's ghast from being imported.
            
             NOTE: You can blacklist multiple things at a time. Just separate
             each entry with a , (comma). Here's an example blacklisting all zombies
             and vanilla's ghasts:
             "zombie, minecraft:ghast"
            """)
    public String blacklistedSpawns = "";



    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Blacklist surfaces by key terms, mod ID, or by block's identifier
             This will blacklist based on the very top most block that the surface uses.
             NOTE: this will not remove surfaces defined by biomeSurfacesLayerOrder.
             It will only blacklist the importing of surfaces not defined in biomeSurfacesLayerOrder.
            
             To blacklist by key terms, just enter the term alone such as "sand"
             and all biome surfaces that uses blocks with sand in its name will
             not be imported. After all, sand is coarse and rough and gets everywhere!
             This uses Regex so you could do "raw\\w+ore" to blacklist any name
             that starts with 'raw' and ends in 'ore'.
            
             To blacklist by mod ID, just enter the mod ID with an * on the end such as
             "weird_biome_mod*" and all biome surfaces from that mod will not be imported.
             
             To blacklist a block from being a surface, enter the mod ID, then :, and then the
             block's name. For example, "minecraft:mycelium" will prevent any surfaces that uses
             Mycelium blocks from being imported.
            
             Also, some biomes might add Air block as a surface block which will create pits in
             the surface that looks like it is missing the top layer of land. Add minecraft:air to
             this config to prevent these kinds of surfaces from being added.
            
             NOTE: You can blacklist multiple things at a time. Just separate
             each entry with a , (comma). Here's an example blacklisting all sand
             surfaces and vanilla Mushroom Biome's Mycelium surface:
             "sand, minecraft:mycelium"
            """)
    public String blacklistedBiomeSurfaces = "";


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Decides if the dimension can import anything from modded biomes.
             Note: If the other vanilla stuff options are set to true and you
             this option set to true as well, then vanilla stuff can still
             get imported if a modded biome has vanilla stuff in it.
            """)
    public boolean allowModdedBiomeImport = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension imports features like trees, plants, ores, etc.\n")
    public boolean allowModdedFeatures = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension imports structures like temples, villages, etc.\n")
    public boolean allowModdedStructures = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension imports carvers like caves, ravines, etc.\n")
    public boolean allowModdedCarvers = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension imports natural mob spawns like zombies, cows, etc.\n")
    public boolean allowModdedSpawns = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension automatically imports new surfacebuilder configs in addition to biomeSurfacesLayerOrder's values (it is added to the end of biomeSurfacesLayerOrder internally).\n")
    public boolean allowImportingAnySurfaces = true;




    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Decides if the dimension can import anything from vanilla biomes.
             Note: If the other vanilla stuff options are set to true and you
             have the import from modded biome option set to true as well, then
             vanilla stuff can still get imported if a modded biome has them.
            """)
    public boolean allowVanillaBiomeImport = true;


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension imports features like trees, plants, ores, etc.\n")
    public boolean allowVanillaFeatures = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension imports structures like temples, villages, etc.\n")
    public boolean allowVanillaStructures = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension imports carvers like caves, ravines, etc.\n")
    public boolean allowVanillaCarvers = true;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Decides if the dimension imports natural mob spawns like zombies, cows, etc.\n")
    public boolean allowVanillaSpawns = true;




    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
            Will prevent importing vanilla lava/fire/basalt features and
             will attempt to make modded lava/fire features not spawn at all
             in order to help reduce lag in the world due to fire spreading rapidly.
             Also, basalt is ugly as it overwhelms the world.
             If all else fail, do /gamerule doFireTick false to reduce fire lag.
            """)
    public boolean disallowFireLavaBasaltFeatures = true;


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """
             Dumps all resource locations (IDs) for all mods into a new file
             at config/world_blender-identifier_dump.txt and can be found by
              looking in the config folder in Minecraft's folder. The file is made
             if you set this option to true and started a world.
           
             Use this option to look up the resource location or name of registered
             features, biomes, blocks, carvers, structures, or entities that you want to blacklist.
            """)
    public boolean identifierDump = true;
}
