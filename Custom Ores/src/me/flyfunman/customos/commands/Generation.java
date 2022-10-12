package me.flyfunman.customos.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import me.flyfunman.customos.CreateLang;
import me.flyfunman.customos.Main;
import me.flyfunman.customos.objects.ChunkLoc;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.ItemCreator;
import me.flyfunman.customos.utils.Skulls;

public class Generation {
	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	Plugin plugin = Main.getPlugin(Main.class);
	int taskDIV = 1;
	int taskID = -1;
	int taskIT;

	public static void command(CommandSender sender, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)
				&& !sender.hasPermission("customores." + args[0].toLowerCase())) {
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "No Permission"));
			return;
		}
		if (args.length < 2 || Bukkit.getWorld(args[1]) == null) {
			sender.sendMessage(CreateLang.getString(ChatColor.DARK_RED, "Incorrect Command Syntax")
					.replace("[command]", "/customores " + args[0] + " <world>"));
			return;
		}
		if (args.length == 2 || !args[2].equalsIgnoreCase("confirm")) {
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "Clear/Generate Confirm 1"));//Clear/Generate Confirm
			if (args[0].equalsIgnoreCase("generate")) {
				sender.sendMessage(CreateLang.getString(ChatColor.YELLOW, "Generate Confirm 1"));
				sender.sendMessage((CreateLang.getString(ChatColor.YELLOW, "Generate Confirm 2"))
						.replace("[command]", "/customores clear " + args[1]));
			} else {
				sender.sendMessage(CreateLang.getString(ChatColor.YELLOW, "Clear Confirm 1"));
				sender.sendMessage(CreateLang.getString(ChatColor.YELLOW, "Clear Confirm 2"));
			}
			sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Clear/Generate Confirm 2")
					.replace("[command]", "/customores " + args[0] + " " + args[1] + " confirm"));
			return;
		}
		Generation get = new Generation();
		if (args[0].equalsIgnoreCase("generate")) {
			sender.sendMessage(CreateLang.getString(ChatColor.GOLD, "Generate Start"));
			get.generate(args[1]);
			sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Finished"));
		} else {
			sender.sendMessage(CreateLang.getString(ChatColor.GOLD, "Clear Start"));
			get.clear(sender, args[1], null);
		}
		CustomConfig.storage().set("Chunks." + args[1], null);
		CustomConfig.saveStorage();
	}

	private void generate(String world) {
		List<ChunkLoc> chunks = new ArrayList<>();
		chunks.addAll(ChunkLoc.chunk);
		for (ChunkLoc chunk : chunks) {
			if (chunk.getWorld().getName().equalsIgnoreCase(world)) {
				ChunkLoc.chunk.remove(chunk);
			}
		}
		chunks.clear();
	}

	public void clear(CommandSender sender, String world, String ore) {
		World wrld = Bukkit.getWorld(world);
		
		boolean config = CustomConfig.storage().contains("Chunks." + wrld.getName());
		boolean locs = !ChunkLoc.chunk.isEmpty();

		List<Integer> xLs = null;
		List<Integer> zLs = null;
				
		if (locs) {
			xLs = new ArrayList<Integer>();
			zLs = new ArrayList<Integer>();
			
			for (ChunkLoc c : ChunkLoc.chunk) {
				if (c.getWorld().equals(wrld)) {
					xLs.add(c.getLoc().getBlockX());
					zLs.add(c.getLoc().getBlockZ());
				}
			}
		}
			
		startClear(wrld, config, locs, ore, xLs, zLs, plugin.getConfig().getLong("Clear Speed"), sender);
	}
	

	public void startClear(World world, boolean config, boolean locs, String ore, List<Integer> xLs, List<Integer> zLs, Long delay, CommandSender sender) {
		int minY = findMinY(world);
		
		List<Integer> xs = new ArrayList<Integer>();
		List<Integer> zs = new ArrayList<Integer>();
		
		if (config) for (String x : CustomConfig.storage().getConfigurationSection("Chunks." + world.getName()).getKeys(false)) {
			if (NumberUtils.isNumber(x)) {
				for (int z : CustomConfig.storage().getIntegerList("Chunks." + world.getName() + "." + x)) {
		
					xs.add(Integer.parseInt(x));
					zs.add(z);
				}
			}
		}

		if (locs) {
			xs.addAll(xLs);
			zs.addAll(zLs);
		}
		
		taskIT = 0;
		
		//replacing the for with a timer
		taskID = scheduler.scheduleSyncRepeatingTask(plugin, () -> {
			if (taskIT >= xs.size()) {
				if (ore == null) {
					generate(world.getName());
					sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Finished"));
				}
				else sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Delete Ore 2").replace("[name]", ore)
						.replace("[world]", world.getName()));
				
				finishClear();
				return;
			}

			if (taskIT >= xs.size() * taskDIV/4) {
				sender.sendMessage(taskIT + "/" + xs.size());
				
				taskDIV++;
			}

			removeOres(world.getBlockAt(xs.get(taskIT), 100, zs.get(taskIT)).getChunk(), ore, minY);
			
			taskIT++;
		}, 0L, delay);
	}
	
	public void finishClear() {
		Bukkit.getScheduler().cancelTask(taskID);
	}
	

	public static void removeOres(Chunk chunk, String ore, int minY) {
		List<Integer> list = IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList());
		List<Integer> Ylist = IntStream.rangeClosed(minY, 255).boxed().collect(Collectors.toList());
		for (int x : list) {
			for (int z : list) {
				for (int y : Ylist) {
					Block blok = chunk.getBlock(x, y, z);
					if (blok != null) {
						if (blok.getType() == Material.PLAYER_HEAD || blok.getType() == Material.PLAYER_WALL_HEAD) {
							for (ItemStack it : blok.getDrops()) {
								for (Item item : Item.items) {

									if (!item.isOre() || (ore != null
											&& !ItemCreator.get().clearColor(ore.replace(' ', '_')).equalsIgnoreCase(
													ItemCreator.get().clearColor(item.getName().replace(' ', '_')))))
										continue;

									if (Skulls.get().getSkullValue(it).equals(item.getValue()))
										setToNormal(blok, chunk, x, z);
									
								}
							}
						}
						else if(Delete.get().plugin.getConfig().getBoolean("Ores as Chests") 
								&& blok.getType() == Material.CHEST) {
							ItemStack[] chest = ((Chest) blok.getState()).getInventory().getContents();
							
							if(chest[13] != null && chest[13].getType() != null && chest[13].getType() == Material.PLAYER_HEAD) 
							{
								boolean cont = false;
								
								for(int i = 0; i < 27; i++) 
								{
							        if(chest[i] != null && chest[i].getType() != null && chest[i].getType() != Material.AIR) 
							        {
							        	if (i == 13) continue;
							        	
							        	cont = true;
							        			
							        	break;
							        }
							    }
								
								if (!cont)
									setToNormal(blok, chunk, x, z);
							}
						}
					}
				}
			}
		}
	}
	
	public static void setToNormal(Block blok, Chunk chunk, int x, int z) {
		if (chunk.getWorld().getBiome(x, z) == (Biome.NETHER)) {
			blok.setType(Material.NETHERRACK, false);
		} else if (chunk.getWorld().getBiome(x, z).name().contains("END"))
			blok.setType(Material.END_STONE, false);
		else {
			blok.setType(Material.STONE, false);
		}
	}
	
	public int findMinY(World world) {
		int minY = 0;
		
		int temp;
		
		for (String oreName : CustomConfig.items().getKeys(false)) {
			temp = CustomConfig.items().getInt(oreName + ".MinY");
			
			if (temp < minY) {
				if (world.getBlockAt(0, temp, 0).getType() != Material.VOID_AIR)
					minY = temp;
			}
		}
		
		if (minY > -64 && world.getBlockAt(0, -64, 0).getType() != Material.VOID_AIR)
			minY = -64;
		
		return minY;
	}

	private static String getValue(String from, String key) {
		String value = null;
		int valueStart = from.indexOf(key + "=");
		if (valueStart == -1)
			return null;

		int valueEnd = from.indexOf(",", valueStart + key.length());
		if (valueEnd == -1)
			valueEnd = from.indexOf("}", valueStart + key.length());
		if (valueEnd == -1)
			return value;

		value = from.substring(valueStart + key.length() + 1, valueEnd);

		return value;
	}

	public static Location deserializeLocation(String location) {
		return new Location(Bukkit.getWorld(getValue(location, "world")), Double.parseDouble(getValue(location, "x")),
				Double.parseDouble(getValue(location, "y")), Double.parseDouble(getValue(location, "z")),
				Float.parseFloat(getValue(location, "pitch")), Float.parseFloat(getValue(location, "yaw")));
	}
}
