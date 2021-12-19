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
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;

import me.flyfunman.customos.objects.ChunkLoc;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.ItemCreator;
import me.flyfunman.customos.utils.Skulls;

public class Generation {

	public static void command(CommandSender sender, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)
				&& !sender.hasPermission("customores." + args[0].toLowerCase())) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
			return;
		}
		if (args.length < 2 || Bukkit.getWorld(args[1]) == null) {
			sender.sendMessage("Incorrect Command Syntax. Please use /customores " + args[0] + " <world>");
			return;
		}
		if (args.length == 2 || !args[2].equalsIgnoreCase("confirm")) {
			sender.sendMessage(ChatColor.RED + "Are you sure you want to do this?");
			if (args[0].equalsIgnoreCase("generate")) {
				sender.sendMessage(
						ChatColor.YELLOW + "It will cause ores to be able to generate in all chunks of this world");
				sender.sendMessage(ChatColor.YELLOW + "If you do this, /customores clear " + args[1]
						+ " will not clear any ores that were generated before you ran this command");
			} else {
				sender.sendMessage(ChatColor.YELLOW + "It will remove all ores from the world");
				sender.sendMessage(ChatColor.YELLOW + "Ores will still generate if you don't disable it in the config");
			}
			sender.sendMessage(
					ChatColor.GREEN + "If you are sure, type /customores " + args[0] + " " + args[1] + " confirm");
			return;
		}
		Generation get = new Generation();
		if (args[0].equalsIgnoreCase("generate")) {
			sender.sendMessage("Starting to generate...");
			get.generate(args[1]);
		} else {
			sender.sendMessage("Starting to clear...");
			get.clear(args[1], null);
		}
		CustomConfig.storage().set("Chunks." + args[1], null);
		CustomConfig.saveStorage();
		sender.sendMessage("Finished!");
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

	public void clear(String world, String ore) {
		World wrld = Bukkit.getWorld(world);
		
		if (CustomConfig.storage().contains("Chunks." + world)) {
			for (String x : CustomConfig.storage().getConfigurationSection("Chunks." + world).getKeys(false)) {

				if (!NumberUtils.isNumber(x))
					continue;

				for (int z : CustomConfig.storage().getIntegerList("Chunks." + world + "." + x)) 
					removeOres(wrld.getChunkAt(Integer.parseInt(x) / 16, z / 16), ore);
				
			}
		}
		List<ChunkLoc> chunks = new ArrayList<>();
		chunks.addAll(ChunkLoc.chunk);
		for (ChunkLoc chunk : chunks) {

			if (chunk.getWorld().getName().equalsIgnoreCase(world)) {
				removeOres(chunk.getWorld().getChunkAt(chunk.getX() / 16, chunk.getZ() / 16), ore);
				ChunkLoc.chunk.remove(chunk);
			}
		}
		chunks.clear();
	}

	public static void removeOres(Chunk chunk, String ore) {
		List<Integer> list = IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList());
		List<Integer> Ylist = IntStream.rangeClosed(0, 255).boxed().collect(Collectors.toList());
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

									if (Skulls.get().getSkullValue(it).equals(item.getValue())) {
										if (chunk.getWorld().getBiome(x, z) == (Biome.NETHER)) {
											blok.setType(Material.NETHERRACK, false);
										} else if (chunk.getWorld().getBiome(x, z).name().contains("END"))
											blok.setType(Material.END_STONE, false);
										else {
											blok.setType(Material.STONE, false);
										}
									}
								}
							}
						}
					}
				}
			}
		}
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
