package me.flyfunman.customos.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import me.flyfunman.customos.CustomOresAPI;
import me.flyfunman.customos.Main;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.ItemCreator;
import me.flyfunman.customos.utils.RecipeCreator;

public class Delete {
	private static Delete delete = new Delete();
	private Generation gen = new Generation();
	Plugin plugin = Main.getPlugin(Main.class);

	public void start(CommandSender sender, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender) && !sender.hasPermission("customores.delete")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
			return;
		}

		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Incorrect Command Syntax. Use /customores delete <type> <name>");
			return;
		}

		String path = null;

		if (args[1].equalsIgnoreCase("recipe")) {
			for (String string : CustomConfig.recipes().getKeys(false)) {
				if (colorEquals(RecipeCreator.get().simplify(args[2]), RecipeCreator.get().simplify(string))) {
					path = string;
					break;
				}
			}
		} else if (args[1].equalsIgnoreCase("item") && Item.getItem(args[2], false) != null) {
			for (Item item : CustomOresAPI.get().getItems()) {
				if (colorEquals(item.getName().replace(' ', '_'), args[2])) {
					path = item.getName();
					break;
				}
			}
		} else if (args[1].equalsIgnoreCase("ore") && Item.getItem(args[2], true) != null) {
			for (Item item : CustomOresAPI.get().getOres()) {
				if (colorEquals(item.getName().replace(' ', '_'), args[2])) {
					path = item.getName();
					break;
				}
			}
		}
		if (path == null) {
			sender.sendMessage(ChatColor.RED + args[2] + " was not recognized as a " + args[1] + ".");
			return;
		}

		if (args.length < 4 || !args[3].equalsIgnoreCase("confirm")) {
			sender.sendMessage(ChatColor.YELLOW + "Are you sure you want to delete " + args[2] + "?");
			sender.sendMessage(ChatColor.RED + "This action cannot be undone");
			if (args[1].equalsIgnoreCase("ore"))
				sender.sendMessage(ChatColor.YELLOW + args[2] + " will be automatically cleared from all worlds,"
						+ " this may cause some lag");
			sender.sendMessage(ChatColor.GREEN + "If you want to delete " + args[2] + " type /co delete " + args[1]
					+ " " + args[2] + " confirm");
			return;
		}

		if (args[1].equalsIgnoreCase("ore")) {
			sender.sendMessage(ChatColor.GREEN + "Starting to remove " + args[2] + " from all worlds");
			for (World world : Bukkit.getWorlds()) {
				if (!plugin.getConfig().getBoolean("All Worlds")
						&& !plugin.getConfig().getList("Worlds").contains(world.getName())) {
					continue;
				}
				if (plugin.getConfig().getBoolean("All Worlds")
						&& plugin.getConfig().getList("Worlds").contains(world.getName())) {
					continue;
				}

				gen.clear(world.getName(), path);
			}
			CustomConfig.storage().set("UUIDS." + path, null);
			RecipeCreator.get().removeRecipe(path);
			sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[2] + " from all worlds");
		}

		if (args[1].equalsIgnoreCase("recipe")) {
			CustomConfig.recipes().set(path, null);
			RecipeCreator.get().removeRecipe(path);
			CustomConfig.saveRecipes();
		} else {
			CustomConfig.items().set(path, null);
			CustomConfig.saveItems();
			Item.items.remove(Item.getItem(path, false));
		}
		sender.sendMessage(ChatColor.GREEN + args[2] + " was removed successfully.");
	}

	public List<String> itemToName(List<Item> items) {
		List<String> list = new ArrayList<String>();
		for (Item item : items) {
			list.add(item.getName());
		}
		return list;
	}

	public boolean colorEquals(String s, String string) {
		s = ItemCreator.get().clearColor(s.replace(' ', '_'));
		string = ItemCreator.get().clearColor(string.replace(' ', '_'));

		if (string.equalsIgnoreCase(s))
			return true;
		return false;
	}

	public static Delete get() {
		return delete;
	}
}
