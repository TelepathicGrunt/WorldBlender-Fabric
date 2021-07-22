### **(V.3.0.5 Changes) (1.17.0 Minecraft)**

##### Dimension:
* Fixed the dimensiontype json so World Blender dimension allows blocks up to 256 again.

##### Mod Compat:
* Added a few BetterEnd structures to World Blender's blacklist to stop the world being encased entirely by End Stone

##### Config:
* Term blacklisting now uses Regex so you can do more advanced blacklisting


### **(V.3.0.4 Changes) (1.17.0 Minecraft)**

##### Dimension:
* Moved biomeSurfacesLayerOrder config option into Omega's config as Cloth Config breaks arrays.

* Fixed roadLayers config so it isn't adding 1 less roads than config asks for.


### **(V.3.0.3 Changes) (1.17.0 Minecraft)**

##### Dimension:
* Added roadLayers, roadThickeness, biomeSurfacesLayerOrder config option and merged two config entries into allowImportingAnySurfaces.
  This gives players more power over what blocks are imported and makes up the surface of the dimension as well as the shape of the roads.


### **(V.3.0.2 Changes) (1.17.0 Minecraft)**

##### Dimension:
* Improved removeWorldBottomStructures config to impact more structures.

* Added text translation for removeWorldBottomStructures config option.
  
* Added config option to remove structure pillars that would've hit world bottom.


### **(V.3.0.1 Changes) (1.17.0 Minecraft)**

##### Dimension:
* Imported structure spacings will no longer overwrite the existing spacing for structures you set for world blender's dimension.

* Added config option to remove structure pieces at world bottom.


### **(V.3.0.0 Changes) (1.17.0 Minecraft)**

##### Dimension:
* Min Y has been lowered to -64

##### Biomes:
* Biomes are larger with Mountainous Blended biome now taller.

##### Blocks:
* World Blender Portal Block now has a new overlay when inside the block!
