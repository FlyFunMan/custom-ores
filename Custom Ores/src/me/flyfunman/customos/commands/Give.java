package me.flyfunman.customos.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.flyfunman.customos.inventories.Storage;
import me.flyfunman.customos.utils.ItemCreator;

public class Give {
	public static void giveItem(CommandSender sender, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender) && !sender.hasPermission("customores.give")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
			return;
		}

		if (args.length == 1) {
			if (sender instanceof Player)
				Storage.open((Player) sender);
			else
				sender.sendMessage("Please specify an item");
			return;
		}

		if (ItemCreator.get().getItem(args[1], 1) == null) {
			sender.sendMessage(
					ChatColor.RED + args[1] + " was not recognized. Please make sure it's enabled in the config.");
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
					sender.sendMessage(player.getDisplayName() + " recieved " + amount + " "
							+ ChatColor.translateAlternateColorCodes('&', args[1]).replace('_', ' '));
				}
			}
			if (player == null) {
				sender.sendMessage(args[3] + " is not online.");
				return;
			}
		} else if (sender instanceof Player) {
			player = (Player) sender;
			player.sendMessage("You recieved " + amount + " "
					+ ChatColor.translateAlternateColorCodes('&', args[1]).replace('_', ' '));
		} else {
			sender.sendMessage("Please specify a player");
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
