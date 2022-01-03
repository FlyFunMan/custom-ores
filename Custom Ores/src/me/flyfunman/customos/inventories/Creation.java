package me.flyfunman.customos.inventories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.flyfunman.customos.Main;

public class Creation {
	ItemStack gray;
	public ItemStack light;
	public static Inventory ore = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Choose a texture method");
	public static Inventory item = Bukkit.createInventory(null, 9, ChatColor.DARK_GREEN + "Select an item");
	public static Inventory lore = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Would you like a lore?");
	public Integer[] slots = { 10, 11, 12, 19, 20, 21, 28, 29, 30, 23 };
	public List<String> enchants = new ArrayList<>();
	public List<String> enPlugins = new ArrayList<>();
	static Creation creation = new Creation();
	Plugin plugin = Main.getPlugin(Main.class);

	public static Creation get() {
		return creation;
	}

	public void setup() {
		gray = namedItem(Material.GRAY_STAINED_GLASS_PANE, " ");
		light = namedItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "Smelt Item Goes Here");

		// ore
		setGray(ore, 9);
		Creation.ore.setItem(0, namedItem(Material.SIGN, ChatColor.BLUE + "Type texture value in chat"));
		Creation.ore.setItem(8, loreItem(Material.STONE, ChatColor.BLUE + "Type texture value in config",
				ChatColor.DARK_BLUE + "The ore will not generate until the texture value is set"));

		// item
		setGray(item, 9);
		
		boolean messaged = false;

		// enchants
		for (Enchantment en : Enchantment.values()) {
			String name = en.getKey().toString();
			String name1 = name.substring(name.indexOf(":")+1);
			name1.trim();

			enchants.add(ChatColor.LIGHT_PURPLE
					+ WordUtils.capitalize(name1.replace('_', ' ')) + ":");
			String key = en.getKey().getNamespace();
			
			if (key != "minecraft" && !enPlugins.contains(key)) {
				enPlugins.add(key);
				
				if (messaged == false && !plugin.getConfig().getBoolean("Custom Enchantments Display")) {
					messaged = true;
					
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&bCustom &aOres: &cYou appear to have a custom enchantments plugin, if you would like to use this plugin "
							+ "with Custom Ores, you should probably enable &o&eCustom Enchantments Display &r&cin the config"));
				}
			}
		}
		Collections.sort(enchants);

		// lore
		setGray(lore, 9);
		lore.setItem(0, namedItem(Material.SIGN, ChatColor.BLUE + "Add Lore"));
		lore.setItem(8, namedItem(Material.BARRIER, ChatColor.RED + "No Lore"));
	}

	public void setGray(Inventory inv, int i) {
		for (int x = 0; x < i; x++) {
			inv.setItem(x, gray);
		}
	}

	public ItemStack namedItem(Material mat, String s) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(s);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack loreItem(Material mat, String name, String lore) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		List<String> list = new ArrayList<String>();
		list.add(lore);
		meta.setLore(list);
		item.setItemMeta(meta);
		return item;
	}

	public Inventory ore() {
		Inventory ore2 = Bukkit.createInventory(null, 36, ChatColor.AQUA + "Ore Settings");
		setGray(ore2, 36);
		ore2.setItem(0,
				loreItem(Material.STONE, ChatColor.DARK_GREEN + "Spawn in Overworld", ChatColor.GREEN + "True"));
		ore2.setItem(4,
				loreItem(Material.NETHER_QUARTZ_ORE, ChatColor.DARK_RED + "Spawn in Nether", ChatColor.RED + "False"));
		ore2.setItem(8, loreItem(Material.END_STONE, ChatColor.YELLOW + "Spawn in End", ChatColor.RED + "False"));
		ore2.setItem(20, loreItem(Material.GRASS_BLOCK, ChatColor.GREEN + "Max Spawn Height", "Y: 60"));
		ore2.setItem(22, light);
		ore2.setItem(24, loreItem(Material.SIGN, ChatColor.BOLD + "Amount Per Chunk", "3"));
		ore2.setItem(29, loreItem(Material.BEDROCK, ChatColor.GREEN + "Min Spawn Height", "Y: 0"));
		ore2.setItem(31, loreItem(Material.FURNACE, ChatColor.AQUA + "Ore Type", "Smeltable"));
		ore2.setItem(35, get().arrow());
		return ore2;
	}

	public Inventory Recipe(String r) {
		Inventory recipe = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Create Recipe");
		setGray(recipe, 45);
		for (int i : slots) {
			ItemStack air = new ItemStack(Material.AIR);
			recipe.setItem(i, air);
		}
		recipe.setItem(44, namedItem(Material.ARROW, "Create " + r));
		return recipe;
	}

	public Inventory Enchant(int page) {
		Inventory item2 = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Enchantments Page " + (page + 1));
		int num = 0;
		setGray(item2, 54);
		item2.setItem(53, arrow());
		
		//fill enchants
		for (int i = page * 45; i < enchants.size() && i < page * 45 + 45; i++) {
			item2.setItem(num, createBook(enchants.get(i), 0));
			num++;
		}
		
		//add page arrows
		if (enchants.size() > page * 45 + 45) item2.setItem(50, arrow("Page " + (page + 2)));
		if (page != 0) item2.setItem(48, arrow("Page " + page));
		
		return item2;
	}

	private ItemStack arrow() {
		return arrow("Next");
	}
	
	private ItemStack arrow(String name) {
		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemMeta aMeta = arrow.getItemMeta();
		aMeta.setDisplayName(name);
		arrow.setItemMeta(aMeta);
		return arrow;
	}

	public ItemStack createBook(String name, int level) {
		return loreItem(Material.ENCHANTED_BOOK, name, "Level: " + level);
	}
}
