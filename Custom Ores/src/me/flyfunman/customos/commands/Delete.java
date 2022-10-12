package me.flyfunman.customos.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import me.flyfunman.customos.CreateLang;
import me.flyfunman.customos.CustomOresAPI;
import me.flyfunman.customos.Main;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.ItemCreator;
import me.flyfunman.customos.utils.RecipeCreator;

public class Delete {
	private static Delete delete = new Delete();
	Plugin plugin = Main.getPlugin(Main.class);

	public void start(CommandSender sender, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender) && !sender.hasPermission("customores.delete")) {
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "No Permission"));
			return;
		}

		if (args.length < 3) {
			sender.sendMessage(CreateLang.getString(ChatColor.DARK_RED, "Incorrect Command Syntax")
					.replace("[command]", "/customores delete <type> <name>"));
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
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "Name Not Recognized")
					.replace("[name]", args[2]).replace("[type]", args[1]));
			return;
		}

		if (args.length < 4 || !args[3].equalsIgnoreCase("confirm")) {
			sender.sendMessage(CreateLang.getString(ChatColor.YELLOW, "Delete Confirm 1").replace("[name]", args[2]));
			sender.sendMessage(CreateLang.getString(ChatColor.RED, "Cannot Undo"));
			if (args[1].equalsIgnoreCase("ore"))
				sender.sendMessage(CreateLang.getString(ChatColor.YELLOW, "Delete Ore Confirm").replace("[name]", args[2]));
			sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Delete Confirm 2").replace("[name]", args[2])
					.replace("[command]", "/co delete " + args[1] + " " + args[2] + " confirm"));
			return;
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

		if (args[1].equalsIgnoreCase("ore")) {
			sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Delete Ore 1").replace("[name]", args[2]));
			for (World world : Bukkit.getWorlds()) {
				if (!plugin.getConfig().getBoolean("All Worlds")
						&& !plugin.getConfig().getList("Worlds").contains(world.getName())) {
					continue;
				}
				if (plugin.getConfig().getBoolean("All Worlds")
						&& plugin.getConfig().getList("Worlds").contains(world.getName())) {
					continue;
				}

				Generation gen = new Generation();
				
				gen.clear(sender, world.getName(), path);
			}
			CustomConfig.storage().set("UUIDS." + path, null);
			RecipeCreator.get().removeRecipe(path);
		}
		
		else sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Delete Success").replace("[name]", args[2]));
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
