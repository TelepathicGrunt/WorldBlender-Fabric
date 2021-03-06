package com.telepathicgrunt.worldblender.configs;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "portal")
public class WBPortalConfigs implements ConfigData {

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " Item(s) that you need in your hand when you're crouching and right\r\n"
            +" clicking a chest block to begin the portal creation process.\r\n"
            +" This activation item will then be consumed. You can specify more\r\n"
            +" than 1 item that can be used to make the portal. Just separate the\r\n"
            +" item identifiers with a comma.\r\n"
            +" \r\n"
            +" NOTE: the 8 chests needs to be in a 2x2 pattern before this mod "
            +" starts checking the contents of the chests and then create the"
            +" portal if there are enough unique blocks in the chests."
            +" \r\n"
            +" You can remove a portal by crouch right clicking except for the\r\n"
            +" portal block at world origin in World Blender dimension.\r\n")
    public String activationItem = "minecraft:nether_star";

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " You can specify what specific blocks are required to be in\r\n"
            +" the chests to make the portal. Format is the block's identifiers\r\n"
            +" with each block separated by a comma. Example:\r\n"
            +" \"minecraft:dirt, minecraft:sand, minecraft:stone\"\r\n"
            +" \r\n"
            + "If you specify 1 required block but the portal needs 3 blocks,\r\n"
            +" players will need to place that one required block and any two \r\n"
            +" other blocks into the chests.\r\n"
            +" \r\n"
            +" If you specify 4 required blocks but the portal needs 2 unique blocks,\r\n"
            +" then players only needs to add any 2 of the 4 blocks to make the portal.\r\n")
    public String requiredBlocksInChests = "";

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = " If true, portal creation will destroy the chests and all contents in it\r\n"
            +" Non-block items and stacks of items will still be consumed.\r\n"
            +" \r\n"
            +" If set to false, the chests and contents will be dropped when portal is made.\r\n")
    public boolean consumeChests = true;


    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = "How many kinds of block items needed to be in the eight\r\n"
            +" chests (or other chest tagged blocks) to create the portal. \r\n"
            +" \r\n"
            +" Items with no block form will be ignored and not counted but still be consumed.\r\n"
            +" \r\n"
            +" If you set this to beyond 216 (the maximum number of slots in 8 vanilla chests),\r\n"
            +" make sure you have a mod that has a chest that has much more inventory slots to"
            +" fill or else you cannot create the portal.")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1000)
    public int uniqueBlocksNeeded = 216;

}
