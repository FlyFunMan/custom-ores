package me.flyfunman.customos;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.ItemCreator;
import me.flyfunman.customos.utils.Skulls;

public class CustomOresAPI {
	private static CustomOresAPI Null = null;
	private List<Item> items = new ArrayList<>();
	private List<Item> ores = new ArrayList<>();
	Plugin plugin = Main.getPlugin(Main.class);

	public List<Item> getItems() {
		items.clear();
		for (Item item : Item.items) {
			if (item.isEnabled() && !item.isOre()) {
				items.add(item);
			}
		}
		return items;
	}

	public List<Item> getOres() {
		ores.clear();
		for (Item item : Item.items) {
			if (item.isEnabled() && item.isOre()) {
				ores.add(item);
			}
		}
		return ores;
	}

	public Boolean setBlock(Location loc, String ore) {
		Item item = Item.getItem(ore, true);
		if (item.isOre() && item != null) {
			// Is an ore
			Skulls.get().setHead(loc.getBlock(), item.getValue());
		}
		// Not an ore
		return false;
	}

	public ItemStack getStack(String name, int amount) {
		return ItemCreator.get().getItem(name, amount);
	}

	public String toString(ItemStack item) {
		return ItemCreator.get().getFromItem(item);
	}

	public FileConfiguration config() {
		return plugin.getConfig();
	}

	public FileConfiguration itemConfig() {
		return CustomConfig.items();
	}

	public FileConfiguration recipeConfig() {
		return CustomConfig.recipes();
	}

	public FileConfiguration storageConfig() {
		return CustomConfig.storage();
	}

	public void saveConfigs() {
		CustomConfig.saveAll();
		plugin.saveConfig();
	}

	public static CustomOresAPI get() {
		if (Null == null) {
			Null = new CustomOresAPI();
		}
		return Null;
	}
}
