package com.telepathicgrunt.world_blender.blocks;

import com.telepathicgrunt.world_blender.WorldBlender;
import com.telepathicgrunt.world_blender.configs.WBConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

import java.util.*;
import java.util.stream.Collectors;


@Mod.EventBusSubscriber(modid = WorldBlender.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WBPortalSpawning
{
	private static List<Block> requiredBlocks = new ArrayList<Block>();
	private static List<String> invalidResourceLocations = new ArrayList<String>();

	/**
	 * Takes config string and chops it up into individual entries and returns the array of the entries.
	 * Splits the incoming string on commas, trims white spaces on end, turns inside whitespace to _, and lowercases entry.
	 */
	public static void generateRequiredBlockList(String configEntry) {
		String[] entriesArray = configEntry.split(",");
		Arrays.parallelSetAll(entriesArray, (i) -> entriesArray[i].trim().toLowerCase(Locale.ROOT).replace(' ', '_'));
		
		//test and make sure the entries exists
		//if not, add it to an invalid rl list so we can warn user later 
		for(String rlString : entriesArray)
		{
			if(rlString.isEmpty()) continue;
			
			if(ForgeRegistries.BLOCKS.containsKey(new Identifier(rlString)))
			{
				requiredBlocks.add(ForgeRegistries.BLOCKS.getValue(new Identifier(rlString)));
			}
			else
			{
				invalidResourceLocations.add(rlString);
			}
		}
	}
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void BlockRightClickEvent(PlayerInteractEvent.RightClickBlock event)
	{
		World world = event.getWorld();
		BlockPos position = event.getPos();
		
		//cannot create portals in WB world type nor run this code on client
		if(world.isClient || world.getGeneratorType() == WorldBlender.WBWorldType) {
			return;
		}

		// Checks to see if player uses right click on a chest while crouching while holding nether star
		if (event.getPlayer().isInSneakingPose() && world.getBlockState(position).getBlock().getTags().contains(Tags.Blocks.CHESTS.getId()))
		{
			//checks to make sure the activation item is a real item before doing the rest of the checks
			Identifier activationItem = new Identifier(WBConfig.activationItem);
			if (!ForgeRegistries.ITEMS.containsKey(activationItem))
			{
				WorldBlender.LOGGER.log(Level.INFO, "World Blender: Warning, the activation item set in the config does not exist. Please make sure it is a valid resource location to a real item as the portal cannot be created now.");
				Text message = new LiteralText("�eWorld Blender: �fWarning, the activation item set in the config does not exist. Please make sure it is a valid resource location to a real item as the portal cannot be created now.");
				event.getPlayer().sendMessage(message);
				return;
			}
			else if((event.getPlayer().getMainHandStack().getItem() != ForgeRegistries.ITEMS.getValue(activationItem) && event.getHand() == Hand.MAIN_HAND) ||
					(event.getPlayer().getOffHandStack().getItem() != ForgeRegistries.ITEMS.getValue(activationItem) && event.getHand() == Hand.OFF_HAND))
			{
				return;
			}
			
			
			BlockPos.Mutable cornerOffset = new BlockPos.Mutable(1, 1, 1);
			boolean eightChestsFound = checkForValidChests(world, position, cornerOffset);

			//8 chests found, time to check their inventory.
			if (eightChestsFound)
			{
				Set<Item> uniqueBlocksSet = new HashSet<Item>();
				Set<Item> invalidItemSet = new HashSet<Item>();
				Set<Item> duplicateBlockSlotSet = new HashSet<Item>();

				for (BlockPos blockpos : BlockPos.iterate(position, position.add(cornerOffset)))
				{
					ChestBlockEntity chestTileEntity = (ChestBlockEntity) world.getBlockEntity(blockpos);
					for (int index = 0; index < chestTileEntity.getInvSize(); index++)
					{
						Item item = chestTileEntity.getInvStack(index).getItem();

						//if it is a valid block, it would not return air
						if (Block.getBlockFromItem(item) != Blocks.AIR)
						{
							if(uniqueBlocksSet.contains(item)) 
							{
								duplicateBlockSlotSet.add(item); // save what block is taking up multiple slots
							}
							else 
							{
								uniqueBlocksSet.add(item); 
							}
						}
						//not a valid block item.
						else
						{
							invalidItemSet.add(item);
						}
					}
				}

				if(!invalidResourceLocations.isEmpty())
				{
					WorldBlender.LOGGER.log(Level.INFO, "World Blender: Warning, error reading the required blocks config entry. Please make sure the blocks specified in that config are valid resource locations and points to real blocks as the portal cannot be created now. The problematic entries are: " + String.join(", ", invalidResourceLocations));
					Text message = new LiteralText("�eWorld Blender: �fWarning, error reading the required blocks config entry. Please make sure the blocks specified in that config are valid resource locations and points to real blocks as the portal cannot be created now. The problematic entries are: �6" + String.join(", ", invalidResourceLocations));
					event.getPlayer().sendMessage(message);
					return;
				}
				
				List<Block> listOfRequireBlocksNotFound = new ArrayList<>(requiredBlocks);
				boolean isMissingRequiredBlocks = false;
				
				//all unique blocks in chests must be a part of the require blocks list
				if(WBConfig.uniqueBlocksNeeded <= requiredBlocks.size())
				{
					for(Item blockItem : uniqueBlocksSet)
					{
						listOfRequireBlocksNotFound.remove(Block.getBlockFromItem(blockItem));
					}
					
					if(WBConfig.uniqueBlocksNeeded > requiredBlocks.size() - listOfRequireBlocksNotFound.size())
					{
						isMissingRequiredBlocks = true;
					}
				}
				//all blocks in the required blocks list must be present
				else
				{
					for(Item blockItem : uniqueBlocksSet)
					{
						listOfRequireBlocksNotFound.remove(Block.getBlockFromItem(blockItem));
					}
					
					if(listOfRequireBlocksNotFound.size() != 0)
					{
						isMissingRequiredBlocks = true;
					}
				}
				
				//warn player that they do not have enough required blocks for the portal
				if(isMissingRequiredBlocks)
				{
					WorldBlender.LOGGER.log(Level.INFO, "World Blender: There are not enough required blocks in the chests. Please add the needed required blocks and then add any other unique blocks until you have "+WBConfig.uniqueBlocksNeeded+" unique blocks. The require blocks specified in the config are " + String.join(", ", requiredBlocks.stream().map(entry -> entry.getRegistryName().toString()).collect(Collectors.toList())));
					Text message = new LiteralText("�eWorld Blender: �fThere are not enough required blocks in the chests. Please add the needed required blocks and then add any other unique blocks until you have �c"+WBConfig.uniqueBlocksNeeded+"�f unique blocks. The require blocks specified in the config are �6" + String.join(", ", requiredBlocks.stream().map(entry -> entry.getRegistryName().toString()).collect(Collectors.toList())));
					event.getPlayer().sendMessage(message);
					return;
				}
				
				
				
				//enough unique blocks were found. Make portal now
				if (uniqueBlocksSet.size() >= WBConfig.uniqueBlocksNeeded)
				{
					for (BlockPos blockpos : BlockPos.iterate(position, position.add(cornerOffset)))
					{
						//consume chest and contents if config says so
						if (WBConfig.consumeChests)
						{
							ChestBlockEntity chestTileEntity = (ChestBlockEntity) world.getBlockEntity(blockpos);
							for (int index = chestTileEntity.getInvSize(); index >= 0; index--)
							{
								chestTileEntity.removeInvStack(index);
							}
						}
						else
						{
							world.breakBlock(blockpos, true, event.getPlayer());
						}

						//create portal but with cooldown so players can grab items before they get teleported
						world.setBlockState(blockpos, WBBlocks.WORLD_BLENDER_PORTAL.get().getDefaultState(), 3);
						WBPortalTileEntity wbtile = (WBPortalTileEntity) world.getBlockEntity(blockpos);
						wbtile.triggerCooldown();
						
						event.getPlayer().getActiveItem().decrement(1); //consume item in hand
					}
				}
				//throw error and list all the invalid items in the chests
				else
				{

					if (!event.getWorld().isClient)
					{
						String msg = "�eWorld Blender: �fThere are not enough unique block items in the chests. (stacks or duplicates are ignored) You need �c" + WBConfig.uniqueBlocksNeeded + "�f block items to make the portal but there is only �a" + uniqueBlocksSet.size() + "�f unique block items right now.";
						
						if(invalidItemSet.size() > 1) 
						{
							//collect the items names into a list of strings
							List<String> invalidItemString = new ArrayList<String>();
							invalidItemSet.remove(Items.AIR); //We don't need to list air
							invalidItemSet.stream().forEach(item -> invalidItemString.add(item.getName(new ItemStack(item)).getString()));
							msg += "�f Also, here is a list of non-block items that were found and should be removed: �6" + String.join(", ", invalidItemString);
						}

						if(duplicateBlockSlotSet.size() != 0) 
						{
							//collect the items names into a list of strings
							List<String> duplicateSlotString = new ArrayList<String>();
							duplicateBlockSlotSet.remove(Items.AIR); //We dont need to list air
							duplicateBlockSlotSet.stream().forEach(blockitem -> duplicateSlotString.add(blockitem.getName(new ItemStack(blockitem)).getString()));
							msg += "�f There are some slots that contains the same blocks and should be removed. These blocks are: �6" + String.join(", ", duplicateSlotString);
						}
						
						WorldBlender.LOGGER.log(Level.INFO, msg);
						((ServerPlayerEntity)event.getPlayer()).sendMessage(new LiteralText(msg));
					}
				}
			}
		}
	}


	/**
	 * Checks all 8 configurations that a 2x2 area of chests could be around incoming position. If 2x2 is all chests,
	 * returns true and the offset blockpos will be set to that configeration's corner.
	 */
	private static boolean checkForValidChests(World world, BlockPos position, BlockPos.Mutable offset)
	{
		boolean eightChestsFound = true;
		for (; offset.getX() >= -1; offset.setOffset(Direction.WEST, 2))
		{
			for (; offset.getY() >= -1; offset.setOffset(Direction.DOWN, 2))
			{
				for (; offset.getZ() >= -1; offset.setOffset(Direction.NORTH, 2))
				{
					//checks if this 2x2 has 8 chests
					for (BlockPos blockpos : BlockPos.iterate(position, position.add(offset)))
					{
						if (!world.getBlockState(blockpos).getBlock().getTags().contains(Tags.Blocks.CHESTS.getId()))
						{
							eightChestsFound = false;
							break;
						}
					}

					//is only true if no spot was not a chest
					if (eightChestsFound)
					{
						return true;
					}

					//reset to true for next 2x2 to be checked
					eightChestsFound = true;
				}
				offset.setOffset(Direction.SOUTH, 4); //move back. have to do 4 because the loop's move will fire when exiting loop too
			}
			offset.setOffset(Direction.UP, 4); //move back. have to do 4 because the loop's move will fire when exiting loop too
		}

		return false;
	}
}
