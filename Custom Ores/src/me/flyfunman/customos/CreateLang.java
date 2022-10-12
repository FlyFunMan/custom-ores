package me.flyfunman.customos;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class CreateLang {
	Plugin plugin = Main.getPlugin(Main.class);
	
	public static String getString(ChatColor color, String path) {
		for (Lang path1 : Lang.values()) {
			if (path.equalsIgnoreCase(path1.getPath())) {
				String s = ChatColor.translateAlternateColorCodes('&', path1.toString());
				if (color!=null&&!path1.toString().contains("&")) s = color + s;
				s = s.replace("┬", "").replace("Â", "");
				return s;
			}
		}
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"!!!WARNING!!!");
		Bukkit.getLogger().warning("There is a problem with "+path+" in the Lang.yml of Custom Ores!");
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"!!!WARNING!!!");
		return null;
	}
	
	public static String getPath(String item) {
		for (Lang path1 : Lang.values()) {
			String s = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', path1.toString()));
			if (item.equalsIgnoreCase(s)) {
				return path1.getPath();
			}
		}
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"!!!WARNING!!!");
		Bukkit.getLogger().warning("There is a problem with "+item+" in the Lang.yml of Custom Ores!");
		Bukkit.getLogger().warning("Please report this to the developer!");
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED+"!!!WARNING!!!");
		return null;
	}
}