package com.telepathicgrunt.world_blender.the_blender;

import com.telepathicgrunt.world_blender.WorldBlender;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;

public class IdentifierPrinting
{

	/**
	 * Prints out all the resource location that the blacklists for World Blender config uses.
	 * Will create 6 sections: Biomes, Features, Structures, Carvers, Entities, and Blocks.
	 * 
	 * The resource location will be printed into a file call identifierDump.txt
	 * and can be found below the world's save folder in the Minecraft folder.
	 */
	public static void printAllIdentifiers(DynamicRegistryManager registryManager)
	{
		try(PrintStream printStream = new PrintStream("identifierDump.txt"))
		{ 
			printOutSection(printStream, registryManager.get(Registry.BIOME_KEY), "BIOMES");

			printStream.println();
			printOutSection(printStream, registryManager.get(Registry.CONFIGURED_FEATURE_WORLDGEN), "CONFIGURED FEATURES");
			
			printStream.println();
			printOutSection(printStream, registryManager.get(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN), "CONFIGURED STRUCTURES");

			printStream.println();
			printOutSection(printStream, registryManager.get(Registry.CARVER_KEY), "CARVERS");
			
			printStream.println();
			printOutSection(printStream, Registry.ENTITY_TYPE, "ENTITIES");
			
			printStream.println();
			printOutSection(printStream, Registry.BLOCK, "BLOCKS");
			
		}
		catch (FileNotFoundException e)
		{
			WorldBlender.LOGGER.warn("FAILED TO CREATE AND WRITE TO identifierDump.txt. SEE STACKTRACE AND SHOW IT TO MOD MAKER.");
			e.printStackTrace();
		} 
	}
	/**
	 * Will go through that registry passed in and print out all the resource locations of every entry inside of it.
	 *
	 * @param printStream - the place we are printing the resource locations to
	 * @param registry - the registry to go through and get all entries
	 * @param section - name of this section. Will be put into the header and printed into the printStream
	 */
	private static <T> void printOutSection(PrintStream printStream, Registry<T> registry, String section)
	{
		String previousNameSpace = "minecraft";
		Identifier entryID;
		
		//title of the section
		printStream.println("######################################################################"); 
		printStream.println("######      "+section+" RESOURCE LOCATION (IDs)        ######"); 
		printStream.println();

		for (Map.Entry<RegistryKey<T>, T> entry : registry.getEntries())
		{
			entryID = entry.getKey().getValue();
			
			// extra check to just make sure. Probably never possible to be null
			if(entryID == null) continue;
			
			//prints a space between different Mod IDs
			previousNameSpace = printSpacingBetweenMods(printStream, previousNameSpace, entryID.getNamespace());
			
			//prints the actual entry's resource location
			printStream.println(entryID.toString());
		}
	}
	
	/**
	 * helper method to print spacing between different mod's resource location section
	 */
	private static String printSpacingBetweenMods(PrintStream printStream, String previousModID, String currentModID) 
	{
		if(!currentModID.isEmpty() && !previousModID.equals(currentModID))
		{
			printStream.println();
			return currentModID;
		}
		
		return previousModID;
	}
}
