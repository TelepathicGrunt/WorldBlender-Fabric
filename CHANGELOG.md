### **(V.3.0.10 Changes) (1.17.1 Minecraft)**

##### Misc:
Switched to using FabricEntityTypeBuilder to not get "DataFixer" log spam for World Blender's ItemClearingEntity.

Removed some deprecated API usage with Mod Menu and fabric API to prevent issues with future versions of those mods. 


### **(V.3.0.9 Changes) (1.17.1 Minecraft)**

##### Configs:
Added ability for the blanket blacklist to not import from biomes based on their category. See config for more info on how. (uses # to work)


### **(V.3.0.8 Changes) (1.17.0 Minecraft)**

##### Misc:
Changed world blender dimension's noise setting json file to have 320 as height instead of 384 because the terrain is limited to 320 range of height already.


### **(V.3.0.7 Changes) (1.17.0 Minecraft)**

##### Config:
Added some Better End's stuff to World Blender's config blacklist default values to keep the dimension looking nicer and not overwhelmed.

##### Blending:
Fixed crash if someone's configuredfeature json has a state field that is not a json object.


### **(V.3.0.6 Changes) (1.17.0 Minecraft)**

##### Dimension:
Liquids exposed to the sky will not be contained much better.

##### Teleportation:
Teleporting to World Blender will make sure that the portal is not placed right at world bottom.
 A stone block will be placed below portal so player won't die.


### **(V.3.0.5 Changes) (1.17.0 Minecraft)**

##### Dimension:
Fixed the dimensiontype json so World Blender dimension allows blocks up to 256 again.

##### Mod Compat:
Added a few BetterEnd structures to World Blender's blacklist to stop the world being encased entirely by End Stone

##### Config:
Term blacklisting now uses Regex so you can do more advanced blacklisting


### **(V.3.0.4 Changes) (1.17.0 Minecraft)**

##### Dimension:
Moved biomeSurfacesLayerOrder config option into Omega's config as Cloth Config breaks arrays.

Fixed roadLayers config so it isn't adding 1 less roads than config asks for.


### **(V.3.0.3 Changes) (1.17.0 Minecraft)**

##### Dimension:
Added roadLayers, roadThickeness, biomeSurfacesLayerOrder config option and merged two config entries into allowImportingAnySurfaces.
  This gives players more power over what blocks are imported and makes up the surface of the dimension as well as the shape of the roads.


### **(V.3.0.2 Changes) (1.17.0 Minecraft)**

##### Dimension:
Improved removeWorldBottomStructures config to impact more structures.

Added text translation for removeWorldBottomStructures config option.
  
Added config option to remove structure pillars that would've hit world bottom.


### **(V.3.0.1 Changes) (1.17.0 Minecraft)**

##### Dimension:
Imported structure spacings will no longer overwrite the existing spacing for structures you set for world blender's dimension.

Added config option to remove structure pieces at world bottom.


### **(V.3.0.0 Changes) (1.17.0 Minecraft)**

##### Dimension:
Min Y has been lowered to -64

##### Biomes:
Biomes are larger with Mountainous Blended biome now taller.

##### Blocks:
World Blender Portal Block now has a new overlay when inside the block!
