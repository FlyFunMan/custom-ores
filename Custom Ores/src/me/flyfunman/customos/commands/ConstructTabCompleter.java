package me.flyfunman.customos.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.flyfunman.customos.CustomOresAPI;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.ItemCreator;

public class ConstructTabCompleter implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (sender instanceof Player && cmd.getName().equalsIgnoreCase("customores")) {
			List<String> list = new ArrayList<>();
			if (args.length == 1) {
				list.add("give");
				list.add("clear");
				list.add("generate");
				list.add("create");
				list.add("delete");
				list.add("reload");
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("give")) {
					for (Item item : Item.items) {
						if (item.isEnabled() && item.getStack(1) != null)
							list.add(ChatColor.stripColor(item.getName().replace(' ', '_')));
					}
				} else if (args[0].equalsIgnoreCase("generate") || args[0].equalsIgnoreCase("clear")) {
					for (World world : Bukkit.getWorlds()) {
						list.add(world.getName());
					}
				} else if (args[0].equalsIgnoreCase("create")) {
					list.add("<name>");
				} else if (args[0].equalsIgnoreCase("delete")) {
					list.add("ore");
					list.add("item");
					list.add("recipe");
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("create")) {
					list.add("ore");
					list.add("item");
					list.add("recipe");
				} else if (args[0].equalsIgnoreCase("give")) {
					list.add("[amount]");
				} else if (args[0].equalsIgnoreCase("delete")) {
					if (args[1].equalsIgnoreCase("recipe")) {
						for (String recipe : CustomConfig.recipes().getKeys(false)) {
							if (CustomConfig.recipes().contains(recipe + ".Enabled")
									&& CustomConfig.recipes().getBoolean(recipe + ".Enabled")) {
								list.add(recipe.replace(' ', '_'));
							}
						}
					} else if (args[1].equalsIgnoreCase("ore")) {
						for (Item ore : CustomOresAPI.get().getOres()) {
							list.add(ItemCreator.get().clearColor(ore.getName().replace(' ', '_')));
						}
					} else if (args[1].equalsIgnoreCase("item")) {
						for (Item item : CustomOresAPI.get().getItems()) {
							list.add(ItemCreator.get().clearColor(item.getName().replace(' ', '_')));
						}
					}
				}
			} else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					list.add(player.getDisplayName());
				}
			}
			List<String> result = new ArrayList<>();
			for (String r : list) {
				if (r.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(r);
			}
			return result;
		}
		return null;
	}
}