package com.telepathicgrunt.worldblender.theblender;

import com.telepathicgrunt.worldblender.WorldBlender;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigBlacklisting
{
	private static Map<BlacklistType, List<String>> TYPE_TO_BLACKLIST;
	public enum BlacklistType 
	{
		BLANKET,
		FEATURE,
		STRUCTURE,
		CARVER,
		SPAWN,
		SURFACE_BLOCK
	}

	public static void setupBlackLists() 
	{
		List<String> blanketBL = parseConfigAndAssignEntries(WorldBlender.WB_CONFIG.WBBlendingConfig.blanketBlacklist);
		List<String> featureBL = parseConfigAndAssignEntries(WorldBlender.WB_CONFIG.WBBlendingConfig.blacklistedFeatures);
		List<String> structureBL = parseConfigAndAssignEntries(WorldBlender.WB_CONFIG.WBBlendingConfig.blacklistedStructures);
		List<String> carverBL = parseConfigAndAssignEntries(WorldBlender.WB_CONFIG.WBBlendingConfig.blacklistedCarvers);
		List<String> spawnBL = parseConfigAndAssignEntries(WorldBlender.WB_CONFIG.WBBlendingConfig.blacklistedSpawns);
		List<String> surfaceBL = parseConfigAndAssignEntries(WorldBlender.WB_CONFIG.WBBlendingConfig.blacklistedBiomeSurfaces);

		TYPE_TO_BLACKLIST = new HashMap<>();
		TYPE_TO_BLACKLIST.put(BlacklistType.BLANKET, blanketBL);
		TYPE_TO_BLACKLIST.put(BlacklistType.FEATURE, featureBL);
		TYPE_TO_BLACKLIST.put(BlacklistType.STRUCTURE, structureBL);
		TYPE_TO_BLACKLIST.put(BlacklistType.CARVER, carverBL);
		TYPE_TO_BLACKLIST.put(BlacklistType.SPAWN, spawnBL);
		TYPE_TO_BLACKLIST.put(BlacklistType.SURFACE_BLOCK, surfaceBL);
	}
	
	/**
	 * Takes config string and chops it up into individual entries and returns the array of the entries.
	 * Splits the incoming string on commas, trims white spaces on end, turns inside whitespace to _, and lowercases entry.
	 */
	private static List<String> parseConfigAndAssignEntries(String configEntry) {
		String[] entriesArray = configEntry.split(",");
		Arrays.parallelSetAll(entriesArray, (i) -> entriesArray[i].trim().toLowerCase(Locale.ROOT).replace(' ', '_'));
		return Arrays.asList(entriesArray);
	}
	
	/**
	 * Helper method that will perform the actual RL match, mod specific match, 
	 * and term matching based on the format of the blacklisted entry string
	 */
	private static boolean matchFound(String blacklistedEntry, Identifier IdentifierToCheck)
	{
		//cannot do any matching. RIP
		if(IdentifierToCheck == null || blacklistedEntry.isEmpty())
		{
			return false;
		}
		
		//full resource location specific ban
		if(blacklistedEntry.contains(":")) 
		{
			return blacklistedEntry.equals(IdentifierToCheck.toString());
		}
		//mod specific ban
		else if(blacklistedEntry.contains("*")) 
		{
			return blacklistedEntry.replace(":", "").replace("*", "").equals(IdentifierToCheck.getNamespace());
		}
		//term specific ban
		return Pattern.compile(blacklistedEntry).matcher(IdentifierToCheck.getPath()).find();
	}

	private static boolean biomeMatchFound(String blacklistedEntry, Biome biome, Identifier resourceLocationToCheck)
	{
		if(blacklistedEntry.contains("#"))
		{
			return biome.getCategory().getName().toLowerCase(Locale.ROOT).equals(blacklistedEntry.replace("#", ""));
		}

		return matchFound(blacklistedEntry, resourceLocationToCheck);
	}

	public static boolean isIdentifierBlacklisted(BlacklistType type, Identifier incomingRL)
	{
		List<String> listToUse = TYPE_TO_BLACKLIST.get(type);
		return listToUse.stream().anyMatch(banEntry -> matchFound(banEntry, incomingRL));
	}

	public static boolean isBiomeBlacklisted(BlacklistType type, Biome biome, Identifier incomingRL)
	{
		List<String> listToUse = TYPE_TO_BLACKLIST.get(type);
		return listToUse.stream().anyMatch(banEntry -> biomeMatchFound(banEntry, biome, incomingRL));
	}
}
