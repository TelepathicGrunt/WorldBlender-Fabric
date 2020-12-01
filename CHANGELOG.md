# Made for Minecraft v.1.16.4
# Created by TelepathicGrunt

Welcome to the Github! If you are looking for the most recent stable version, then checkout the master branch! Branches dedicated to the latest version of Minecraft may be unstable or broken as I test and experiment so stick with the master branch instead.

------------------------------------------------
#    | World Blender changelog |

## (V.1.0.12 Changes) (1.16.4 Minecraft)

##### Backend:
- Moved some of my code around so if it causes issues, it now shows up in stacktraces for easier debugging.

## (V.1.0.11 Changes) (1.16.4 Minecraft)

##### Portal:
- Fixed portal not able to be spawned outside development environment.


## (V.1.0.10 Changes) (1.16.4 Minecraft)

##### Dimension:
- Fixed possible issue with End Podium or Altar not spawning.

##### Config:
- Adjusted some config comments.

##### Backend:
- Removed Angerable Patch as it is better for other mods 
  to depend on it instead of World Blender.


## (V.1.0.9 Changes) (1.16.4 Minecraft)

##### Features:
- Optimized World Blender's feature slightly.

##### Portal:
- You now can specify multiple activation items! 
  Just separate their identifiers with a comma.
  If you make activation item config empty, any 
  crouch right clicking can make the portal now too.
  
##### Config:
- Clarified and fixed some config comments.

##### Misc:
- Removed vote screen as voting is over.


## (V.1.0.8 Changes) (1.16.3 Minecraft)

##### Dimension:
- Fixed sky color.
 
##### Features:
- Fixed Coral not spawning in dimension.


## (V.1.0.7 Changes) (1.16.3 Minecraft)
     
##### Portal:

- Fixed serverside crash when attempting to make portal. 
  Special thanks to adoxentor for finding this crash!

- Fixed crash with Pistons because I didn't register
  my Block Entity Renderer properly.

 
## (V.1.0.6 Changes) (1.16.3 Minecraft)
     
##### Portal:

- Make crouch right clicking without activation item not 
  deny the item's behavior.

##### SurfaceBuilders:
 
- Added null check in case mods put null into their surfacebuilder
  config when they really should not be doing that.
  
##### Mod Compat:
 
- Fixed possible issues with other mod's Block Entities.

  
## (V.1.0.5 Changes) (1.16.3 Minecraft)
     
##### Config:
 
- Bamboo is now automatically blacklisted by disallowLaggyFeatures properly
  
##### Blender:
 
- Unregistered ConfiguredFeatures, ConfiguredStructures, and/or ConfiguredCarvers
  will no longer be spawned in WB's dimension due to unregistered stuff possibly
  wiping out other mod's registered stuff from biomes.
 
- Significantly reduce the log spam when other mods have unregistered
  ConfiguredFeatures, ConfiguredStructures, and/or ConfiguredCarvers.


## (V.1.0.4 Changes) (1.16.3 Minecraft)
     
##### Dimension:
 
- Fixed bug where End Podium and Altar may randomly not spawn 
  at all in World Blender's dimension at world origin.

##### Blocks:
 
- Fixed potential startup crash with some mod's Block Entities.
    
##### Structures:
 
- Attempted a fix to make structure spawn properly in 
  WB dimension for mods whose structures didn't spawn previously.


## (V.1.0.3 Changes) (1.16.3 Minecraft)
   
    Backend:

- Applied fix that now lets WorldBlender stop giving
  false positive warnings for unregistered worldgen 
  stuff from other mods that are indeed registered.

    Dimension:
- Cleaned up the json format for the dimension's json file.


## (V.1.0.2 Changes) (1.16.3 Minecraft)
   
##### Blender:

- Fixed modded features and structures so that they spawn in World blender now.
  Also fixed Log spam with other worldgen mod. They were registering their 
  stuff but the features themselves are different from the ones in the registry.
  A workaround was done for that.
  
- Mostly fixed bug where entering a world after leaving without exiting Minecraft
  on single player would break World Blender and fill the dimension with duplicate
  configured features.

##### Misc:

- Jar-in-jar'd Modmuss's Voyager to fix a rare bug that crashes servers
  running Java 11 when they try to load two chunks with structures in 
  two dimensions at the same time.

- Jar-in-jar'd Draylar's Angerable Patch to fix a mojang bug where angerable
  entities in mob spawners will spam the logs like heck and won't render the
  mob in the spawner itself.

## (V.1.0.1 Changes) (1.16.3 Minecraft)
   
##### Major:

- Fixed crash on servers.

- Attempted fixes to patch Vanilla bugs with being thread unsafe on servers with people in multiple dimensions.

- Fixed World Blender not pulling in other mod's structures and features they add by mixin to biomes.
  
- Fixed possible crash with govote screen when on macos.
  
## (V.1.0.0 Changes) (1.16.2 Minecraft)
  
##### Major:

-UPDATED AND PORTED TO 1.16.3 FABRIC!!!! WOOOOOO!!!!!!! 
  
  ---forge version below---
  
## (V.1.3.7 Changes) (1.15.2 Minecraft)
  
##### Compat:
 
-Tried fixing crash with Mcreator due to Mcreator modifying entity spawn stuff.
  
##### Dimension:
 
-Added config to allow map cursor spinning or not in the dimension.
    
## (V.1.3.6 Changes) (1.15.2 Minecraft)
  
##### Mod Compatibility:
 
-Added data files to allow compat with Pokecube
  
## (V.1.3.5 Changes) (1.15.2 Minecraft)
  
##### Mod Compatibility:
  
-Fixed mod compatibility breaking with Dimensional Dungeons and Terraforged if the user adds another mod that didn't properly registered their features.

-Updated backend so World Blender can import any new feature that Terraforged adds in the future.

-Fixed bug where Dimensional Dungeons's doorways would get waterlogged with water if the area already had water before the dungeon was placed.

##### Config:�

-Added option to make World Blender place Obsidian to separate lava tagged fluids from water tagged fluids underground.

##### Surfaces:

-Fixed crash when mods uses a unique surfacebuilder that uses a config that doesn't extend SurfaceBuilderConfig class.

## (V.1.3.4 Changes) (1.15.2 Minecraft)
  
##### Misc:�

-Fixed crash on server when trying to make the portal but there are either non-block items or duplicate items in the chests.

##### Config:�

-The carversCanCarveMoreBlocks config entry now actually works and now can turn off adding extra blocks to the carver's list of blocks that they can carve away.

##### Biomes:�

-Added a new cold hilly land biome so features/mobs that needs cold temperature to spawn can spawn on this freezing land biome.

##### Structures:�

-Portal Altar should now bypass trees and generate more often on the actual terrain surface.

## (V.1.3.3 Changes) (1.15.2 Minecraft)
  
##### Misc:�

-Quick fix to patch a crash at startup due to me forgetting to do deferredtask........
  
## (V.1.3.2 Changes) (1.15.2 Minecraft)
  
##### Misc:�

-Fixed issue where mod would crash at startup when running in certain other foreign languages.

##### Portal:�

-Added a new config option to let players specify certain blocks that must be present in the chests in order to make the portal. Can be used to significantly decrease the number of blocks needed but still maintain balance.

-Teleporting to the other dimension will now place an air block above the newly placed portal so you no longer can get trapped in a tree and stuff.

-When making the portal with not enough blocks, the invalid item part of the error message will not appear if there is no items in the chests anymore.

-Added support for offhand. You now can hold the activation item in your offhand to make the portal.
  
## (V.1.3.1 Changes) (1.15.2 Minecraft)
  
##### Portal:�

-Fixed bug where portal creation and teleporting breaks when in multiplayer/servers. No more crashes should occur from this now.  

-The animation on the Portal Block should now play seamlessly for 99.99999% of the time.

-Altar is made much faster now and I am trying something in backend to make sure the Portal Altar always has the Portal block.

##### Config:�

-The disallowLaggyFeatures config entry will now stop Good Night Sleep's Nether Spread feature from spawning because that feature can generate fire which then can cause an out of control fire lag. (Also does a bit more deeper checks for lava/fire in certain kinds of nested features to disallow them if a mod adds it)

-Improved the containFloatingLiquids config entry so it now contains water much better when the water is floating in midair and is exposed to the sky. 
  
##### Structures:

-Fixed Desert Temples, Jungle Temples, and Strongholds from having missing Chests and fixed Strongholds from missing Silverfish Spawner in Portal Room.

-Fixed what in theory could be a rare potential crash with this mod trying to be compatible with Dimensional Dungeons.


## (V.1.3.0 Changes) (1.15.2 Minecraft)
  
##### Config:�
-Added config option to allow whether to let carvers carve through more kinds of blocks or not.

-Added an option to let users be able to change the scaling of the surfaces to be bigger or smaller.

-Added two options to let users enable/disable placing Terracotta under blocks that can fall or surround floating liquids that could flow everywhere. 

##### Surfaces:
-Nether, End, and certain modded biome surfaces will now replace all stone in that spot instead of being limited to just the top surface so that mods who's ores and stuff needs certain blocks super low can still generate. (like a mod who's Nether based Ores only spawns when Netherrack is below Y = 30 will now generate as Netherrack will generate for that entire column of blocks instead of just the very surface) �

-All Vanilla carvers and most if not all modded carvers should now be able to carve through Netherrack, End Stone, and other modded blocks that will fully replace Stone underground.

  
## (V.1.2.4 Changes) (1.15.2 Minecraft)
 
##### Misc:�
-Fixed crashing due to me forgetting to do a null check if a mod's feature isn't registered to the Forge registry.
  
  
## (V.1.2.3 Changes) (1.15.2 Minecraft)
 
##### Config:�
-Changed config entry of disableLaggyVanillaFeatures to disableLaggyFeatures as it will now attempt to detect other mod's bamboo, sugar cane, lava, and fire based features and prevent their generation.  
-TerraForged and Dimensional Dungeons is now able to be filtered by more configs that should've be able to filter them.
  
##### Misc:�
-Removed some log spam caused by my mod with Dimensional Dungeons.
-Optimized a bit in backend to use less memory.
  
   
## (V.1.2.2 Changes) (1.15.2 Minecraft)
   
##### Importing Features:�
-Fixed a crash with importing from a certain biome with a certain feature.
-Fixed some vanilla trees being sneaky and still spawning when turning off vanilla features config.  

##### Config:�
-End Spikes and End Podium can only be turned off now by putting their resource location into the feature blacklist due to them being needed for Enderdragon fights. Basically, this is to reduce the chance of people accidentally turning them off when turning off other features and if they really don't want the spike or podium, they have to explicitly tell the game to not spawn it.
  
  
## (V.1.2.1 Changes) (1.15.2 Minecraft)
  
##### Block:�
-Fixed bug where portal block that are player-made cannot be removed by crouch right-clicking with empty hand.

##### Config:�
-End Spikes (Obsidian pillars) will now not be removed when turning off vanilla features config as it is needed for the Enderdragon and is a good way to visually mark world origin.

##### Misc:�
-Removed the log spam caused by World Blender when Dimension Dungeons is generating in the dimension.


## (V.1.2.0 Changes) (1.15.2 Minecraft)
   
##### Importing Features:�
-Added dedicated support for DimDungeon mod!
-Added dedicated support for TerraForged mod!
-Fixed bug where Ocean Monuments would never spawn.
-Fixed bug where turning off config for features could cause structures to not spawn.

##### Dimension:�
-Added option to spawn Enderdragon at world origin in this dimension! (Set to false by default in configs as it is highly experimental)
 
##### Config:�
-Added the ability to blacklist mods, biomes, structures, features, carvers, entities, and surfaces from being import into World Blender.
-Added option to print out the resource location (IDs) into a file called resourceLocationDump.txt so you can target certain features or biomes to blacklist easier.
-Added option to spawn Enderdragon or not at world origin. (false by default)

##### Teleportation:�
-Made World Blender Portal slightly less intense on the eyes add just a tad less laggy.
-World Blender Portal now has the Dragon Immune, Impermeable, Portals, and Wither Immune tags.
-Slightly reduced collision box of World Blender Portal so you have to go more into it to teleport rather than graze the surface of the block.
  
   
## (V.1.1.0 Changes) (1.15.2 Minecraft)
 
##### Importing Features:�
-Fixed bug where some modded features are seen as vanilla features by mistake.
 
##### Importing Structures:�
-Fixed bug where importing structures also need importing features turned on. Now that option works without needing feature option also set to true.

##### Teleportation:�
-Added World Blender Portal to teleport between Overworld and World Blender dimension. You make the portal by placing 8 chests in a 2x2 area and then fill all of their slots with an unique block (stacks of blocks will not count as extra and items without block form will be ignored). Then crouch and right click the chests while holding a Nether Star to create the portal to this overpowered dimension! Crouch right click the portal block without holding any item to remove the portal for good. 
   
##### Dimension:�
-Added World Blender Portal Altar at world origin in the dimension where the World Blender Portal block cannot be removed by crouch right clicking.
  
##### Worldtype:�
-Created worldtype as an alternative for the dimension. For server owners, add "use-modded-worldtype=world-blender" as a new entry in your server.properties file to use this worldtype.

##### Config:�
-Added config to changed the amount of unique items needed to create the portal.
-Added config to changed what item is needed to be held to create the portal.
-Added config to turn off vanilla bamboo, fire, and lava based features to help reduce lag.


## (V.1.0.0 Changes) (1.15.2 Minecraft)

##### Major:�
-FIRST RELEASE OF THIS MOD
