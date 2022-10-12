package me.flyfunman.customos.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.flyfunman.customos.CreateLang;
import me.flyfunman.customos.inventories.Storage;
import me.flyfunman.customos.utils.ItemCreator;

public class Give {
	public static void giveItem(CommandSender sender, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender) && !sender.hasPermission("customores.give")) {
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "No Permission"));
			return;
		}

		if (args.length == 1) {
			if (sender instanceof Player)
				Storage.open((Player) sender);
			else
				sender.sendMessage(CreateLang.getString(ChatColor.RED, "Specify Item"));
			return;
		}

		if (ItemCreator.get().getItem(args[1], 1) == null) {
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "Not Recognized").replace("[name]", args[1]));
			return;
		}
		Player player = null;
		int amount = 1;

		if (args.length >= 3 && isNumeric(args[2])) {
			amount = Integer.parseInt(args[2]);
		}

		if (args.length >= 4) {
			for (Player play : Bukkit.getOnlinePlayers()) {
				if (play.getDisplayName().equalsIgnoreCase(args[3])) {
					player = Bukkit.getPlayer(args[3]);
					sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Recieved").replace("[player]", player.getDisplayName())
							.replace("[amount]", "" + amount)
							.replace("[name]", ChatColor.translateAlternateColorCodes('&', args[1]).replace('_', ' ')));
				}
			}
			if (player == null) {
				sender.sendMessage(CreateLang.getString(ChatColor.DARK_RED, "Not Online").replace("[player]", args[3]));
				return;
			}
		} else if (sender instanceof Player) {
			player = (Player) sender;
			sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Recieved").replace("[player]", player.getDisplayName())
					.replace("[amount]", "" + amount)
					.replace("[name]", ChatColor.translateAlternateColorCodes('&', args[1]).replace('_', ' ')));
		} else {
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "Specify Player"));
			return;
		}

		player.getInventory().addItem(ItemCreator.get().getItem(args[1], amount));
	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			int d = Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
