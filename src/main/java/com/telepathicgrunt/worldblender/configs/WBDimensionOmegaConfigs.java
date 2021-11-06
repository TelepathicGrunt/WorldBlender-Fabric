package com.telepathicgrunt.worldblender.configs;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;

import java.util.List;

public class WBDimensionOmegaConfigs implements Config {

    @Override
    public String getName() {
        return "world_blender-fabric-omega_configs";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    @Override
    public void save() {
        Config.super.save();
    }

    @Comment(value = """
             
             
             
             
             // The biome surfaces that will always exist in the dimension and which order they will appear in.
             // Generally, surfaces towards the end are a bit more rare than the surfaces higher up.
             // roadLayers config option determines the first how many surfaces to turn into the road in the dimension.
             
             // Each string entry is one surface that you can find in the dimension.
             
             // Surfaces here are made up of this format typically:
             // "<surface block>, <under the surface block>, <underwater surface block>"
              
             // You can also do just one block and that block will replace all the blocks in that area with it. 
             // Including the entire underground.
             // "<block>"
              
             // Be sure to type the block's registry name correctly.
            """)
    public List<String> biomeSurfacesLayerOrder = List.of(
            "minecraft:netherrack",
            "minecraft:end_stone",
            "minecraft:grass_block, minecraft:dirt, minecraft:gravel",
            "minecraft:podzol, minecraft:dirt, minecraft:gravel",
            "minecraft:red_sand, minecraft:white_terracotta, minecraft:gravel",
            "minecraft:sand, minecraft:sand, minecraft:sandstone",
            "minecraft:mycelium, minecraft:dirt, minecraft:gravel",
            "minecraft:snow_block, minecraft:dirt, minecraft:gravel",
            "minecraft:crimson_nylium, minecraft:netherrack, minecraft:nether_wart_block",
            "minecraft:warped_nylium, minecraft:netherrack, minecraft:warped_wart_block",
            "minecraft:blackstone, minecraft:basalt, minecraft:magma_block",
            "minecraft:coarse_dirt, minecraft:dirt, minecraft:gravel",
            "minecraft:gravel, minecraft:gravel, minecraft:gravel",
            "minecraft:rooted_dirt, minecraft:rooted_dirt, minecraft:gravel"
    );

}
