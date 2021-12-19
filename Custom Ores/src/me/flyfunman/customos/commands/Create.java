package me.flyfunman.customos.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.flyfunman.customos.inventories.Creation;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.ItemCreator;
import me.flyfunman.customos.utils.RecipeCreator;

public class Create {
	public static HashMap<UUID, String> ores = new HashMap<>();
	public static HashMap<UUID, String> items = new HashMap<>();
	private static Create create = new Create();

	public void start(CommandSender sender, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender) && !sender.hasPermission("customores.create")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
			return;
		}
		if (args.length >= 2) {
			Player player = (Player) sender;
			if (ores.containsKey(player.getUniqueId()) || items.containsKey(player.getUniqueId())) {
				sender.sendMessage("You are already creating something!");
				return;
			}
			if (args.length == 2 || (!args[2].equalsIgnoreCase("ore") && !args[2].equalsIgnoreCase("item")
					&& !args[2].equalsIgnoreCase("recipe"))) {
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect command syntax. Please specify a type!");
				return;
			}
			if (!args[2].equalsIgnoreCase("recipe") && ItemCreator.get().isCustomColor(args[1])) {
				player.sendMessage(ChatColor.DARK_RED + "An item/ore with this name already exists!");
				return;
			}
			if (args[1].contains(".")) {
				player.sendMessage(ChatColor.DARK_RED + "You have an illegal character in your name: .");
				return;
			}
			if (args[2].equalsIgnoreCase("ore")) {
				player.openInventory(Creation.ore);
				ores.put(player.getUniqueId(), ChatColor.translateAlternateColorCodes('&', args[1].replace('_', ' ')));
			} else if (args[2].equalsIgnoreCase("item")) {
				player.openInventory(Creation.item);
				items.put(player.getUniqueId(), ChatColor.translateAlternateColorCodes('&', args[1].replace('_', ' ')));
			} else {
				if (ItemCreator.get().contains(CustomConfig.recipes().getKeys(false), args[1].replace('_', ' '))
						|| simpleContains(CustomConfig.recipes().getKeys(false), args[1].replace(' ', '_'))) {
					sender.sendMessage(ChatColor.DARK_RED + "A recipe with that name already exists!");
					return;
				}
				player.openInventory(Creation.get().Recipe(args[1].replace('_', ' ')));
			}
		} else
			sender.sendMessage(
					ChatColor.DARK_RED + "Incorrect command syntax. Please use /customores create <name> <type>");
	}

	public boolean simpleContains(Collection<? extends String> c, String string) {
		for (String path : c) {
			if (RecipeCreator.get().simplify(path).equalsIgnoreCase(RecipeCreator.get().simplify(string))) {
				return true;
			}
		}
		return false;
	}

	public static Create get() {
		return create;
	}
}
