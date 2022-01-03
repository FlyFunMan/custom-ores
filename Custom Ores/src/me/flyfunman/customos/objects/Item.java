package me.flyfunman.customos.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.th0rgal.oraxen.items.OraxenItems;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.ItemCreator;
import me.flyfunman.customos.utils.RecipeCreator;

public class Item {
	public static List<Item> items = new ArrayList<>();

	private Boolean enabled;
	private Boolean overworld;
	private Boolean nether;
	private Boolean end;

	private String name;
	private String lore;
	private String value;

	private int numPerChunk;
	private int maxY;
	private int minY;

	private ItemStack item;
	private ItemStack smelt;

	private HashMap<String, Integer> enchants = new HashMap<>();

	public Item(String name) {
		enabled = CustomConfig.items().getBoolean(name + ".Enabled");
		this.name = name;
		if (CustomConfig.items().contains(name + ".Lore"))
			lore = CustomConfig.items().getString(name + ".Lore");

		if (CustomConfig.items().contains(name + ".Value")) {
			value = CustomConfig.items().getString(name + ".Value");

			overworld = CustomConfig.items().getBoolean(name + ".Overworld");
			nether = CustomConfig.items().getBoolean(name + ".Nether");
			end = CustomConfig.items().getBoolean(name + ".End");

			numPerChunk = CustomConfig.items().getInt(name + ".NumberPerChunk");
			maxY = CustomConfig.items().getInt(name + ".MaxY");
			minY = CustomConfig.items().getInt(name + ".MinY");
		} else {
			int level = 0;
			for (String enchant : CustomConfig.items().getStringList(name + ".Enchantments.Types")) {
				enchants.put(enchant, CustomConfig.items().getIntegerList(name + ".Enchantments.Levels").get(level));
				level++;
			}
		}
		item = ItemCreator.get().getStack(name);
		
		if (CustomConfig.items().contains(name + ".Smelt Item")) {
			String s = CustomConfig.items().getString(name + ".Smelt Item").toLowerCase().replace(' ', '_');
			int amount = 1;
			if (CustomConfig.items().contains(name + ".Smelt Amount"))
				amount = CustomConfig.items().getInt(name + ".Smelt Amount");
			
			smelt = ItemCreator.get().getFromString(s, amount);
			if (smelt == null || !smelt.getType().isItem()) {
				if (RecipeCreator.get().oraxen() && OraxenItems.exists(s.toLowerCase())) {
					smelt = OraxenItems.getItemById(s.toLowerCase()).build();
					smelt.setAmount(amount);
				}
			} 
		}
	}

	public static void load() {
		items.clear();
		for (String item : CustomConfig.items().getKeys(false)) {
			if (ItemCreator.get().getStack(item) != null)
				items.add(new Item(item));
		}
	}

	public static Item getItem(String name, Boolean ore) {
		for (Item item : Item.items) {
			if (ChatColor.stripColor(item.getName()).replace(' ', '_')
					.equalsIgnoreCase(ChatColor.stripColor(name.replace(' ', '_'))) && (!ore || item.isOre())) {
				return item;
			}
		}
		return null;
	}

	public boolean isOre() {
		if (value == null) {
			return false;
		}
		return true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Boolean isOverworld() {
		return overworld;
	}

	public Boolean isNether() {
		return nether;
	}

	public Boolean isEnd() {
		return end;
	}

	public String getName() {
		return name;
	}
	
	public ItemStack getSmelt() {
		return smelt;
	}

	public String getLore() {
		return lore;
	}

	public String getValue() {
		return value;
	}

	public int getNumPerChunk() {
		return numPerChunk;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinY() {
		return minY;
	}

	public Material getType() {
		return item.getType();
	}

	public ItemStack getStack(int amount) {
		item.setAmount(amount);
		return item;
	}
}
