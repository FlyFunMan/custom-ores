package me.flyfunman.customos.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.flyfunman.customos.CreateLang;

public class CustomConfig {
	private static File file;
	private static FileConfiguration storage;
	private static File file2;
	private static FileConfiguration items;
	private static File file3;
	private static FileConfiguration recipes;

	public static void setup() {
		file = new File(Bukkit.getServer().getPluginManager().getPlugin("CustomOres").getDataFolder(), "Storage.yml");
		file2 = new File(Bukkit.getServer().getPluginManager().getPlugin("CustomOres").getDataFolder(), "Items.yml");
		file3 = new File(Bukkit.getServer().getPluginManager().getPlugin("CustomOres").getDataFolder(), "Recipes.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
				storage = YamlConfiguration.loadConfiguration(file);
				createStorage();
			} catch (IOException e) {
				Bukkit.getLogger().warning(CreateLang.getString(ChatColor.RED, "Setup Fail")
						.replace("[file]", "Storage.yml"));
			}
		} else
			storage = YamlConfiguration.loadConfiguration(file);
		if (!file2.exists()) {
			try {
				file2.createNewFile();
				items = YamlConfiguration.loadConfiguration(file2);
				createItemExample();
			} catch (IOException e) {
				Bukkit.getLogger().warning(CreateLang.getString(ChatColor.RED, "Setup Fail")
						.replace("[file]", "Items.yml"));
			}
		} else
			items = YamlConfiguration.loadConfiguration(file2);
		if (!file3.exists()) {
			try {
				file3.createNewFile();
				recipes = YamlConfiguration.loadConfiguration(file3);
				createRecipeExample();
			} catch (IOException e) {
				Bukkit.getLogger().warning(CreateLang.getString(ChatColor.RED, "Setup Fail")
						.replace("[file]", "Recipes.yml"));
			}
		} else
			recipes = YamlConfiguration.loadConfiguration(file3);
		saveAll();
	}
	
	public static void reassign() {
		file = new File(Bukkit.getServer().getPluginManager().getPlugin("CustomOres").getDataFolder(), "Storage.yml");
		file2 = new File(Bukkit.getServer().getPluginManager().getPlugin("CustomOres").getDataFolder(), "Items.yml");
		file3 = new File(Bukkit.getServer().getPluginManager().getPlugin("CustomOres").getDataFolder(), "Recipes.yml");
	}

	public static void saveAll() {
		saveStorage();
		saveItems();
		saveRecipes();
	}

	public static void saveStorage() {
		try {
			storage.save(file);
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(CreateLang.getString(ChatColor.RED, "Save Fail")
					.replace("[file]", "Storage.yml"));
		}
	}

	public static void saveItems() {
		try {
			items.save(file2);
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(CreateLang.getString(ChatColor.RED, "Save Fail")
					.replace("[file]", "Items.yml"));
		}
	}

	public static void saveRecipes() {
		try {
			recipes.save(file3);
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(CreateLang.getString(ChatColor.RED, "Save Fail")
					.replace("[file]", "Recipes.yml"));
		}
	}

	public static FileConfiguration storage() {
		return storage;
	}

	public static FileConfiguration items() {
		return items;
	}

	public static FileConfiguration recipes() {
		return recipes;
	}

	public static void createItemExample() {
		items().options().header(
				"Use https://custom-ores.fandom.com/wiki/Creating_an_Item and https://custom-ores.fandom.com/wiki/Creating_an_Ore Use § for color codes (e. g. §3) !!USING & WILL CAUSE PROBLEMS!!");
		items().set("Example Ore.Enabled", false);
		items().set("Example Ore.Lore", "§7Used as an example. An actual ore would be enabled");
		items().set("Example Ore.Value",
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmNjMTEzZGNjZTE1ZmE0NTRjODQ1ZTk5MDk2MmY1ZjE2YTJhOTZmOTk2NTkwYzE2ZTVlMjQ0M2U0ZjgwMTVjNCJ9fX0=");
		items().set("Example Ore.NumberPerChunk", 5);
		items().set("Example Ore.MaxY", 50);
		items().set("Example Ore.MinY", 0);
		items().set("Example Ore.Overworld", true);
		items().set("Example Ore.Nether", true);
		items().set("Example Ore.End", true);
		List<String> biomes = new ArrayList<>();
		biomes.add("All");
		items().set("Example Ore.Biomes", biomes);
		items().set("Example Ore.Smelt Amount", 1);
		items().set("Example Ore.Smelt Item", "blaze rod");
		items().set("Example Ore.Drop Not Smelt", false);
		items().set("Example Stick.Enabled", false);
		items().set("Example Stick.Type", "stick");
		items().set("Example Stick.Lore", "§aUsed as an example. An actual item would be enabled");
		List<String> list = new ArrayList<>();
		list.add("fire aspect");
		list.add("sharpness");
		List<Integer> integer = new ArrayList<>();
		integer.add(10);
		integer.add(5);
		items().set("Example Stick.Enchantments.Types", list);
		items().set("Example Stick.Enchantments.Levels", integer);
	}

	public static void createRecipeExample() {
		recipes().options().header("Use this tutorial: https://custom-ores.fandom.com/wiki/Creating_a_Recipe");
		recipes().set("Example Recipe.Enabled", false);
		recipes().set("Example Recipe.Result", "Example Stick");
		recipes().set("Example Recipe.Amount", 1);
		recipes().set("Example Recipe.TopLeft", "air");
		recipes().set("Example Recipe.TopCenter", "Example Ore");
		recipes().set("Example Recipe.TopRight", "Air");
		recipes().set("Example Recipe.MiddleLeft", "Example Ore");
		recipes().set("Example Recipe.MiddleCenter", "stick");
		recipes().set("Example Recipe.MiddleRight", "Example Ore");
		recipes().set("Example Recipe.BottomLeft", "AIR");
		recipes().set("Example Recipe.BottomCenter", "Example Ore");
		recipes().set("Example Recipe.BottomRight", "aIR");
	}

	public static void createStorage() {
		storage().options().header(CreateLang.getString(null, "Storage Header"));
	}
}
