package me.flyfunman.customos.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import io.th0rgal.oraxen.items.OraxenItems;
import me.flyfunman.customos.Main;
import me.flyfunman.customos.commands.Create;
import me.flyfunman.customos.inventories.Creation;
import me.flyfunman.customos.listeners.Inventories;
import me.flyfunman.customos.objects.Item;

public class ItemCreator {
	Plugin plugin = Main.getPlugin(Main.class);
	private static ItemCreator instance;

	public ItemStack getItem(String name, int amount) {
		for (Item item : Item.items) {
			if (ChatColor.stripColor(item.getName().replace('_', ' '))
					.equalsIgnoreCase(ChatColor.stripColor(name.replace('_', ' ')))) {
				return item.getStack(amount);
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getStack(String name) {
		if (CustomConfig.items().getKeys(false).isEmpty())
			return null;
		for (String item : CustomConfig.items().getKeys(false)) {
			if (clearColor(item.replace('_', ' ')).equalsIgnoreCase(clearColor(name.replace('_', ' ')))) {
				if (CustomConfig.items().contains(item + ".Enabled")
						&& CustomConfig.items().getBoolean(item + ".Enabled")) {
					ItemStack i;
					if (CustomConfig.items().contains(item + ".Value")) {
						if (!CustomConfig.storage().contains("UUIDS." + item)) {
							CustomConfig.storage().set("UUIDS." + item, UUID.randomUUID().toString());
							CustomConfig.saveStorage();
						}
						String url = CustomConfig.items().getString(item + ".Value");
						i = Skulls.get().createSkull(url, item);
					} else {
						name = CustomConfig.items().getString(item + ".Type").toLowerCase().replace(' ', '_');
						if (RecipeCreator.get().oraxen() && OraxenItems.exists(name)) {
							i = OraxenItems.getItemById(name).build();
						} else if (isCustomColor(name) && 
								!clearColor(name).equalsIgnoreCase(clearColor(item.replace(' ', '_')))) {
							i = getStack(CustomConfig.items().getString(item + ".Type"));
						}
						else {
							Material m = Material.getMaterial(name.toUpperCase());
							
							if (m != null && m.isItem())
								i = new ItemStack(m);
							
							else {
								Bukkit.getServer().getConsoleSender().sendMessage(
										ChatColor.translateAlternateColorCodes('&', "&e[&bCustom &aOres&e] The type " + name
												+ " for the item " + item + " was not recognized!"));
								return null;
							}
						}
					}
					List<String> lore = new ArrayList<String>();
					
					//add enchants
					List<String> enchants = CustomConfig.items().getStringList(item + ".Enchantments.Types");
					
					boolean hide = false;
					
					if (enchants != null && !enchants.isEmpty()) {
						List<Integer> levels = CustomConfig.items().getIntegerList(item + ".Enchantments.Levels");
						
						for (int x = 0; x < enchants.size(); x++) {
							Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchants.get(x).toLowerCase()
									.replace(" ", "_")));
							
							int level = 1;
							if (levels != null && levels.size() > x) level = levels.get(x);
							
							if (ench == null) {
								for (String plugin : Creation.get().enPlugins)
									ench =  Enchantment.getByKey(new NamespacedKey(plugin, enchants.get(x).toLowerCase()
										.replace(" ", "_")));
							}
							if (ench == null)
								Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
										"&bCustom &aOres: &cThe item " + item + "'s enchantment, " + enchants.get(x)) + 
										", was not recognized. This enchantment will not be added");
							else {
								i.addUnsafeEnchantment(ench, level);
								
								//replace enchantment display with a custom one if enabled
								if (plugin.getConfig().contains("Custom Enchantments Display") 
										&& plugin.getConfig().getBoolean("Custom Enchantments Display")) {
									hide = true;
								
									lore.add(ChatColor.GRAY + WordUtils.capitalizeFully(enchants.get(x).replace('_', ' '))
										+ ' ' + level);
								}
							}
						}
					}

					//get the meta to alter
					ItemMeta iMeta = i.getItemMeta();
							
					if (hide)
						iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					
					iMeta.setDisplayName(ChatColor.YELLOW + item);
					if (CustomConfig.items().contains(item + ".Lore")
							&& CustomConfig.items().getStringList(item + ".Lore") != null) {
						lore.add(ChatColor.translateAlternateColorCodes('&',
								CustomConfig.items().getString(item + ".Lore")));
					}
					
					if (!lore.isEmpty())
						iMeta.setLore(lore);
					
					i.setItemMeta(iMeta);
					return i;
				}
			}
		}
		return null;
	}

	public String clearColor(String s) {
		return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', s));
	}

	public ItemStack getFromString(String name, int amount) {
		String n = name.toUpperCase().replace(' ', '_');
		if (isCustomColor(name)) {
			return getItem(name, amount);
		} else {
			Material m = Material.getMaterial(n);
			
			if (m != null && m.isItem()) 
				return new ItemStack(m, amount);
		}
		return null;
	}

	public Boolean isCustom(String name) {
		if (contains(CustomConfig.items().getKeys(false), name) && getItem(name, 1) != null)
			return true;
		return false;
	}

	public Boolean isCustomColor(String name) {
		String n = ItemCreator.get().clearColor(name.replace(' ', '_'));
		for (String path : CustomConfig.items().getKeys(false)) {
			if (ItemCreator.get().clearColor(path.replace(' ', '_')).equalsIgnoreCase(n))
				return true;
		}
		return false;
	}

	public String getFromItem(ItemStack item) {
		if (item == null)
			return "air";
		for (Item i : Item.items) {
			if (item.getType() != null && item.getType() == Material.PLAYER_HEAD && i.isOre()) {
				if ((i.getValue().equals(Skulls.get().getSkullValue(item))) && checkItemStrings(item, i))
					return i.getName();
			} else {
				String type = i.getType().toString();
				if (item.getType().toString().equalsIgnoreCase(type)
						|| (RecipeCreator.get().oraxen() && OraxenItems.getIdByItem(item) != null
								&& OraxenItems.getIdByItem(item).equalsIgnoreCase(type))) {
					if (checkItemStrings(item, i))
						return i.getName();

				}
			}
		}
		return item.getType().toString();
	}

	private boolean checkItemStrings(ItemStack item, Item i) {
		if (i.getLore() != null) {
			if (item.getItemMeta().hasLore()
					&& clearColor(i.getLore()).equals(ChatColor.stripColor(item.getItemMeta().getLore().get(0)))) {
				return true;
			}
		} else if (clearColor(i.getName().replace('_', ' '))
				.equals(ChatColor.stripColor(item.getItemMeta().getDisplayName()))) {
			return true;
		}
		return false;
	}

	public boolean contains(Collection<? extends String> list, String string) {
		for (String string2 : list) {
			if (string.equalsIgnoreCase(string2)) {
				return true;
			}
		}
		return false;
	}

	public void addItem(UUID uuid, String lore) {
		//remove from being created
		Inventories.addLore.remove(uuid);
		
		//Check if it is an ore
		if (Create.ores.containsKey(uuid)) {
			//get the name of the ore and the inventory that was created
			String name = Create.ores.get(uuid);
			List<ItemStack> view = Inventories.settings.get(uuid);
			
			//remove from ore creation registry
			Create.ores.remove(uuid);
			
			//add all values to the config
			addToConfig(name, uuid, lore);
			CustomConfig.items().set(name + ".Value", Inventories.values.get(uuid));
			CustomConfig.items().set(name + ".NumberPerChunk",
					Integer.parseInt(ChatColor.stripColor(view.get(24).getItemMeta().getLore().get(0))));
			CustomConfig.items().set(name + ".MaxY", Integer
					.parseInt(ChatColor.stripColor(view.get(20).getItemMeta().getLore().get(0).replace("Y: ", ""))));
			CustomConfig.items().set(name + ".MinY", Integer
					.parseInt(ChatColor.stripColor(view.get(29).getItemMeta().getLore().get(0).replace("Y: ", ""))));
			CustomConfig.items().set(name + ".Overworld", toBoolean(view.get(0).getItemMeta().getLore().get(0)));
			CustomConfig.items().set(name + ".Nether", toBoolean(view.get(4).getItemMeta().getLore().get(0)));
			CustomConfig.items().set(name + ".End", toBoolean(view.get(8).getItemMeta().getLore().get(0)));
			CustomConfig.items().set(name + ".Smelt Amount", view.get(22).getAmount());
			
			//determine whether to drop or smelt
			CustomConfig.items().set(name + ".Drop Not Smelt", view.get(31).getType() == Material.DROPPER);
			
			String result = null;
			if (RecipeCreator.get().oraxen() && OraxenItems.getIdByItem(view.get(22)) != null
					&& !contains(CustomConfig.items().getKeys(false), ItemCreator.get().getFromItem(view.get(22)))) {
				result = OraxenItems.getIdByItem(view.get(22));
			} else
				result = ItemCreator.get().getFromItem(view.get(22));
			if (result == null || result.replace('_', ' ').equalsIgnoreCase("light gray stained glass pane"))
				result = "air";
			Inventories.settings.remove(uuid);
			Bukkit.getPlayer(uuid).sendRawMessage("Ore Created");
			if (result != "air") {
				CustomConfig.items().set(name + ".Smelt Item", result);
				CustomConfig.saveItems();
				
				//Add to item registry
				if (ItemCreator.get().getStack(name) != null)
					Item.items.add(new Item(name));
				
				RecipeCreator.get().addFurnace(name);
			}
			else {
				//Add to item registry
				if (ItemCreator.get().getStack(name) != null)
					Item.items.add(new Item(name));
				
				CustomConfig.saveItems();
			}
		// check if it is an item
		} else if (Create.items.containsKey(uuid)) {
			String name = Create.items.get(uuid);
			Create.items.remove(uuid);
			addToConfig(name, uuid, lore);
			CustomConfig.items().set(name + ".Type", Inventories.materials.get(uuid));
			if (Inventories.enchant.containsKey(uuid)) {
				for (String enchant : Inventories.enchant.get(uuid).keySet()) {
					CustomConfig.items().set(name + ".Enchantments.Types",
							ChatColor.stripColor(enchant).replace(":", ""));
					CustomConfig.items().set(name + ".Enchantments.Levels", Inventories.enchant.get(uuid).get(enchant));
				}
				List<String> types = new ArrayList<>();
				List<Integer> levels = new ArrayList<>();
				for (String type : Inventories.enchant.get(uuid).keySet()) {
					types.add(type);
					levels.add(Inventories.enchant.get(uuid).get(type));
				}
				if (!types.isEmpty()) {
					CustomConfig.items().set(name + ".Enchantments.Types", types);
					CustomConfig.items().set(name + ".Enchantments.Levels", levels);
				}
				Inventories.enchant.remove(uuid);
			}
			Bukkit.getPlayer(uuid).sendRawMessage("Item Created");
			CustomConfig.saveItems();
			if (ItemCreator.get().getStack(name) != null)
				Item.items.add(new Item(name));
		}
	}

	public boolean toBoolean(String s) {
		return Boolean.parseBoolean((ChatColor.stripColor(s.toLowerCase())));
	}

	public void addToConfig(String name, UUID uuid, String lore) {
		CustomConfig.items().set(name + ".Enabled", true);
		if (lore != null)
			CustomConfig.items().set(name + ".Lore", lore);
	}

	public static ItemCreator get() {
		if (instance == null) {
			instance = new ItemCreator();
		}
		return instance;
	}
}
