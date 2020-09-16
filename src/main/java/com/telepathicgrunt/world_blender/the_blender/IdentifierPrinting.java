package com.telepathicgrunt.world_blender.the_blender;

import com.telepathicgrunt.world_blender.WorldBlender;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.telepathicgrunt.worldblender.WorldBlender;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class IdentifierPrinting
{

	/**
	 * Prints out all the resource location that the blacklists for World Blender config uses.
	 * Will create 6 sections: Biomes, Features, Structures, Carvers, Entities, and Blocks.
	 * 
	 * The resource location will be printed into a file call identifierDump.txt
	 * and can be found below the world's save folder in the Minecraft folder.
	 */
	public static void printAllIdentifiers()
	{
		try(PrintStream printStream = new PrintStream("identifierDump.txt"))
		{ 
			printOutSection(printStream, ForgeRegistries.BIOMES, "BIOMES");

			printStream.println();
			printOutSection(printStream, ForgeRegistries.FEATURES, "FEATURES");
			
			printStream.println();
			printOutSection(printStream, ForgeRegistries.FEATURES, "STRUCTURES");

			printStream.println();
			printOutSection(printStream, ForgeRegistries.WORLD_CARVERS, "CARVERS");
			
			printStream.println();
			printOutSection(printStream, ForgeRegistries.ENTITIES, "ENTITIES");
			
			printStream.println();
			printOutSection(printStream, ForgeRegistries.BLOCKS, "BLOCKS");
			
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
	 * @param <T> - generic type of the entries in the registry
	 * @param printStream - the place we are printing the resource locations to
	 * @param registry - the registry to go through and get all entries
	 * @param section - name of this section. Will be put into the header and printed into the printStream
	 */
	private static <T extends IForgeRegistryEntry<T>> void printOutSection(PrintStream printStream, IForgeRegistry<T> registry, String section)
	{
		String previousNameSpace = "minecraft";
		Identifier forgeRL;
		
		//title of the section
		printStream.println("######################################################################"); 
		printStream.println("######      "+section+" RESOURCE LOCATION (IDs)        ######"); 
		printStream.println(""); 

		for (T forgeEntry : registry.getValues())
		{
			forgeRL = ((ForgeRegistryEntry<T>) forgeEntry).getRegistryName();
			
			// extra check to just make sure. Probably never possible to be null
			if(forgeRL == null) continue; 
			
			//prints a space between different Mod IDs
			previousNameSpace = printSpacingBetweenMods(printStream, previousNameSpace, forgeRL.getNamespace());
			
			//prints the actual entry's resource location
			printStream.println(forgeRL.toString()); 
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
