package me.flyfunman.customos.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.flyfunman.customos.CreateLang;
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
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "No Permission"));
			return;
		}
		if (args.length >= 2) {
			Player player = (Player) sender;
			if (ores.containsKey(player.getUniqueId()) || items.containsKey(player.getUniqueId())) {
				sender.sendMessage(CreateLang.getString(ChatColor.RED, "Already Creating"));
				return;
			}
			if (args.length == 2 || (!args[2].equalsIgnoreCase("ore") && !args[2].equalsIgnoreCase("item")
					&& !args[2].equalsIgnoreCase("recipe"))) {
				sender.sendMessage(CreateLang.getString(ChatColor.DARK_RED, "Incorrect Command Syntax")
						.replace("[command]", "/customores create <name> <type>"));
				return;
			}
			if (!args[2].equalsIgnoreCase("recipe") && ItemCreator.get().isCustomColor(args[1])) {
				player.sendMessage(CreateLang.getString(ChatColor.DARK_RED, "Already Exists").replace("[type]", args[2]));
				return;
			}
			if (args[1].contains(".")) {
				player.sendMessage(CreateLang.getString(ChatColor.DARK_RED, "Illegal Character").replace("[character]", "."));
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
					sender.sendMessage(CreateLang.getString(ChatColor.DARK_RED, "Already Exists").replace("[type]", args[2]));
					return;
				}
				player.openInventory(Creation.get().Recipe(args[1].replace('_', ' ')));
			}
		} else
			sender.sendMessage(CreateLang.getString(ChatColor.DARK_RED, "Incorrect Command Syntax")
							.replace("[command]", "/customores create <name> <type>"));
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
