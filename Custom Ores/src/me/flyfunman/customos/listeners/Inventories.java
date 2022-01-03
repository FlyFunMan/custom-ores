package me.flyfunman.customos.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import io.th0rgal.oraxen.items.OraxenItems;
import me.flyfunman.customos.Main;
import me.flyfunman.customos.inventories.Creation;
import me.flyfunman.customos.utils.Conv;
import me.flyfunman.customos.utils.ItemCreator;
import me.flyfunman.customos.utils.RecipeCreator;

public class Inventories implements Listener {
	Conv conv = new Conv();
	Plugin plugin = Main.getPlugin(Main.class);
	ConversationFactory cf = new ConversationFactory(plugin);
	public static HashMap<UUID, String> materials = new HashMap<>();
	public static List<UUID> addtexture = new ArrayList<>();
	public static List<UUID> addLore = new ArrayList<>();
	public static HashMap<UUID, String> values = new HashMap<>();
	public static HashMap<UUID, HashMap<String, Integer>> enchant = new HashMap<>();
	public static HashMap<UUID, List<Inventory>> enInv = new HashMap<>();
	public static HashMap<UUID, List<ItemStack>> settings = new HashMap<>();
	public ItemStack air = new ItemStack(Material.AIR);
	public static Inventories inventories;
	public String Default = "ewogICJ0aW1lc3RhbXAiIDogMTU5MjE4MDAxMDU2OCwKICAicHJvZm"
			+ "lsZUlkIiA6ICIyMWUzNjdkNzI1Y2Y0ZTNiYjI2OTJjNGEzMDBhNGRlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJHZXlzZXJNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVk"
			+ "IiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS"
			+ "9kNDk1NWU1YjBiMGRkYzZiMDJiODdiYWIwYWE5MTIyMzk3OWQwYTBkZDdlMDU0ODI5ZGEyZmM4OTAxNGRkNGYwIgogICAgfQogIH0KfQ==";

	@EventHandler
	public void DragEvent(InventoryDragEvent e) {
		if (!(e.getWhoClicked() instanceof Player))
			return;
		if (e.getView().getTitle().equals(ChatColor.GREEN + "Get Item")) {
			for (Integer i : e.getRawSlots()) {
				if (e.getView().getTopInventory().getSize() > i) {
					e.setCancelled(true);
					((Player) e.getWhoClicked()).updateInventory();
					if (e.getRawSlots().size() == 1 && e.getView().getItem(i) != null
							&& e.getView().getItem(i).getType() != Material.AIR && e.getCursor() != null
							&& e.getCursor().getType() != Material.AIR) {
						e.setCursor(giveItem(ClickType.LEFT, e.getCursor().clone(), e.getView().getItem(i).clone()));
					}
					return;
				}
			}
		}
	}

	@EventHandler
	@SuppressWarnings("deprecation")
	public void InventoryEvent(InventoryClickEvent e) {
		if (e.getClickedInventory() == null || e.getWhoClicked() == null || !(e.getWhoClicked() instanceof Player))
			return;

		// Storage
		if (e.getView().getTitle().equals(ChatColor.GREEN + "Get Item")) {
			if (e.getClickedInventory().getType() == InventoryType.CHEST) {
				e.setCancelled(true);
				((Player) e.getWhoClicked()).updateInventory();
				if (e.getCurrentItem() == null || e.getCurrentItem().getType() == null
						|| e.getCurrentItem().getType() == Material.AIR)
					return;
				if (e.getClick() == ClickType.MIDDLE) {
					ItemStack clicked = e.getCurrentItem().clone();
					clicked.setAmount(64);
					e.getView().getBottomInventory().addItem(clicked);
				} else if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.LEFT) {
					e.setCursor(giveItem(e.getClick(), e.getCursor().clone(), e.getCurrentItem().clone()));
				}
			} else if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT
					|| e.getClick() == ClickType.DOUBLE_CLICK) {
				e.setCancelled(true);
			}
		} else if (e.getView().getTitle().equals(ChatColor.DARK_GREEN + "Select an item") && e.getCurrentItem() != null
				&& e.getCurrentItem().getType() != null && e.getCurrentItem().getType() != Material.AIR) {
			Player player = (Player) e.getWhoClicked();
			e.setCancelled(true);
			if (e.getClickedInventory().getType() == InventoryType.PLAYER)
				selectItem(player, e.getCurrentItem());
		} else if (e.getView().getTitle().contains(ChatColor.DARK_GREEN + "Enchantments Page ")) {
			e.setCancelled(true);
			if (e.getClickedInventory().getType() != InventoryType.CHEST)
				return;
			if (e.getCurrentItem().getType() == Material.ENCHANTED_BOOK) {
				e.getCurrentItem().setItemMeta(numChange(e.getCurrentItem(), "Level: ", e.getClick(), false));
				return;
			}
			if (e.getSlot() == 53) {
				HashMap<String, Integer> enchants = new HashMap<>();
				for (int x = 0; x < Enchantment.values().length && x < 45 * enInv.get(e.getWhoClicked().getUniqueId()).size(); x++) {
					
					int page = (int)Math.floor(x/45);
					ItemMeta meta = enInv.get(e.getWhoClicked().getUniqueId()).get(page).getItem(x - 45 * page).getItemMeta();
					if (meta.hasLore() && !ChatColor.stripColor(meta.getLore().get(0)).equals("Level: 0")) {
						enchants.put(ChatColor.stripColor(meta.getDisplayName()).replace(":", ""),
								Integer.parseInt(ChatColor.stripColor(meta.getLore().get(0).replace("Level: ", ""))));
					}
				}
				if (!enchants.isEmpty()) {
					enchant.put(e.getWhoClicked().getUniqueId(), enchants);
				}
				openInventory((Player) e.getWhoClicked(), Creation.lore);
			}
			else if ((e.getSlot() == 50 || e.getSlot() == 48) && e.getCurrentItem().getType() == Material.ARROW) {
				int current = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("Enchantments Page ", "")));
				int change;
				
				//get up or down num
				if (e.getSlot() == 50) change = current;
				else change = current - 2;
				
				List<Inventory> invs = enInv.get(e.getWhoClicked().getUniqueId());
				
				//if list isn't big enough to contain this inventory, create it
				if (invs.size() <= change) {
					invs.add(Creation.get().Enchant(change));
					
					enInv.put(e.getWhoClicked().getUniqueId(), invs);
				}
				
				//now just open it
				openInventory((Player) e.getWhoClicked(), invs.get(change));
			}
		} else if (e.getView().getTitle().equals(ChatColor.DARK_AQUA + "Create Recipe")) {
			if (e.getClickedInventory().getType() == InventoryType.CHEST
					&& !containsInt(Creation.get().slots, e.getSlot())) {
				e.setCancelled(true);
				if (e.getSlot() == 44) {
					if (e.getView().getItem(23) == null || e.getView().getItem(23).getType() == Material.AIR) {
						e.getWhoClicked().sendMessage(ChatColor.RED + "You need to have a result!");
						return;
					}
					InventoryView view = e.getView();
					String p = e.getCurrentItem().getItemMeta().getDisplayName().replace("Create ", "");
					RecipeCreator.get().addRecipe(p, view, Creation.get().slots);
					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						public void run() {
							e.getWhoClicked().closeInventory();
						}
					});
				}
			}
		} else if (e.getView().getTitle().equals(ChatColor.AQUA + "Ore Settings")) {
			if (e.getClickedInventory().getType() == InventoryType.PLAYER)
				return;
			if (e.getSlot() == 22) {
				ItemStack cursor = e.getCursor();
				ItemStack smelt = e.getView().getItem(22);
				if (smelt.getType() == Creation.get().light.getType())
					waitTick(new Runnable() {
						public void run() {
							e.getWhoClicked().setItemOnCursor(air);
						}
					});
				if (cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR)
					waitTick(new Runnable() {
						public void run() {
							if (e.getView().getItem(22) == null || e.getView().getItem(22).getType() == null
									|| e.getView().getItem(22).getType() == Material.AIR) {
								e.getView().setItem(22, Creation.get().light);
							}
						}
					});
				return;
			}
			e.setCancelled(true);
			Integer[] bool = { 0, 4, 8 };
			if (containsInt(bool, e.getSlot())) {
				ItemStack current = e.getCurrentItem();
				current.setItemMeta(boolChange(e.getCurrentItem()));
				e.setCurrentItem(current);
				return;
			}
			if (e.getSlot() == 20 || e.getSlot() == 24 || e.getSlot() == 29) {
				ItemStack current = e.getCurrentItem();
				if (e.getSlot() == 24)
					current.setItemMeta(numChange(e.getCurrentItem(), "", e.getClick(), false));
				else
					current.setItemMeta(numChange(e.getCurrentItem(), "Y: ", e.getClick(), true));
				e.setCurrentItem(current);
				return;
			} else if(e.getSlot() == 31) {	
				ItemMeta lMeta = Creation.get().light.getItemMeta();
				
				if (e.getView().getItem(31).getType() == Material.FURNACE) {
					e.getView().setItem(31, Creation.get().loreItem(Material.DROPPER, ChatColor.AQUA + "Ore Type", "Drops"));
					
					lMeta.setDisplayName("Drop Item Goes Here");
				}	
				else {
					e.getView().setItem(31, Creation.get().loreItem(Material.FURNACE, ChatColor.AQUA + "Ore Type", "Smeltable"));
					
					lMeta.setDisplayName("Smelt Item Goes Here");
				}
				
				
				if (e.getView().getItem(22).getItemMeta().getDisplayName() == Creation.get().light.getItemMeta().getDisplayName()) {
					Creation.get().light.setItemMeta(lMeta);
					
					e.getView().setItem(22, Creation.get().light);
				} 
				else Creation.get().light.setItemMeta(lMeta);
			}
			else if (e.getSlot() == 35) {
				List<ItemStack> list = new ArrayList<>();
				for (int x = 0; x < 36; x++) {
					if (list != null)
						list.add(e.getView().getItem(x));
				}
				settings.put(e.getWhoClicked().getUniqueId(), list);
				openInventory((Player) e.getWhoClicked(), Creation.lore);
			}
		} else if (e.getView().getTitle().equals(ChatColor.AQUA + "Choose a texture method")) {
			e.setCancelled(true);
			if (e.getClickedInventory().getType() == InventoryType.PLAYER)
				return;
			if (e.getSlot() == 0) {
				UUID u = e.getWhoClicked().getUniqueId();
				Conversation verse = cf.withFirstPrompt(conv).withLocalEcho(false)
						.buildConversation((Conversable) e.getWhoClicked());
				Bukkit.getScheduler().runTask(plugin, new Runnable() {
					public void run() {
						addtexture.add(u);
						InventoryClose.exempt.add(u);
						e.getWhoClicked().closeInventory();
						verse.begin();
					}
				});
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					public void run() {
						if (addtexture.contains(u)) {
							addtexture.remove(u);
							setInConfig((Player) e.getWhoClicked());
							verse.abandon();
							e.getWhoClicked().openInventory(Creation.get().ore());
							InventoryClose.exempt.remove(u);
							e.getWhoClicked().sendMessage(ChatColor.RED
									+ "You took too long and the texture was set to default! You can still change it through the config.");
						}
					}
				}, 1200);
			} else if (e.getSlot() == 8) {
				setInConfig((Player) e.getWhoClicked());
			}
		} else if (e.getView().getTitle().equals(ChatColor.BLUE + "Would you like a lore?")) {
			e.setCancelled(true);
			if (e.getClickedInventory().getType() == InventoryType.PLAYER)
				return;
			if (e.getSlot() == 0) {
				Conversation verse = cf.withFirstPrompt(conv).withLocalEcho(false)
						.buildConversation((Conversable) e.getWhoClicked());
				Bukkit.getScheduler().runTask(plugin, new Runnable() {
					public void run() {
						UUID u = e.getWhoClicked().getUniqueId();
						InventoryClose.exempt.add(u);
						addLore.add(u);
						e.getWhoClicked().closeInventory();
						verse.begin();
					}
				});
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					public void run() {
						UUID uuid = e.getWhoClicked().getUniqueId();
						if (addLore.contains(uuid)) {
							ItemCreator.get().addItem(uuid, null);
							verse.abandon();
							InventoryClose.exempt.remove(uuid);
							e.getWhoClicked().sendMessage(ChatColor.RED
									+ "You took too long and a lore was not added! You can still add one through the config.");
						}
					}
				}, 1200);
				return;
			}
			if (e.getSlot() == 8) {
				ItemCreator.get().addItem(e.getWhoClicked().getUniqueId(), null);
				Bukkit.getScheduler().runTask(plugin, new Runnable() {
					public void run() {
						e.getWhoClicked().closeInventory();
					}
				});
			}
		}
	}

	private void setInConfig(Player player) {
		values.put(player.getUniqueId(), Default);
		openInventory(player, Creation.get().ore());
	}

	private ItemMeta numChange(ItemStack item, String prefix, ClickType c, boolean signed) {
		ItemMeta bMeta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		int level = Integer.parseInt(bMeta.getLore().get(0).replace(prefix, "")) + 1;
		if (c == ClickType.RIGHT) {
			if (!signed && (Integer.parseInt(bMeta.getLore().get(0).replace(prefix, "")) - 1) < 0)
				return bMeta;
			level = Integer.parseInt(bMeta.getLore().get(0).replace(prefix, "")) - 1;
		} else if (c == ClickType.SHIFT_RIGHT) {
			if (!signed && (Integer.parseInt(bMeta.getLore().get(0).replace(prefix, "")) - 10) < 0)
				return bMeta;
			level = Integer.parseInt(bMeta.getLore().get(0).replace(prefix, "")) - 10;
		} else if (c == ClickType.SHIFT_LEFT)
			level = Integer.parseInt(bMeta.getLore().get(0).replace(prefix, "")) + 10;
		lore.add(prefix + level);
		bMeta.setLore(lore);
		return bMeta;
	}

	private ItemMeta boolChange(ItemStack item) {
		ItemMeta iMeta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if (iMeta.getLore().get(0).equalsIgnoreCase(ChatColor.GREEN + "True"))
			lore.add(ChatColor.RED + "False");
		else
			lore.add(ChatColor.GREEN + "True");
		iMeta.setLore(lore);
		return iMeta;
	}

	private void selectItem(Player player, ItemStack item) {
		if (RecipeCreator.get().oraxen() && OraxenItems.getIdByItem(item) != null)
			materials.put(player.getUniqueId(), OraxenItems.getIdByItem(item));
		else
			materials.put(player.getUniqueId(), item.getType().toString());
		
		//create inventory, add it to list, and open it
		Inventory en = Creation.get().Enchant(0);
		
		List<Inventory> invs = new ArrayList<Inventory>();
		
		invs.add(en);
		
		enInv.put(player.getUniqueId(), invs);
		
		openInventory(player, en);
	}

	private ItemStack giveItem(ClickType click, ItemStack cursor, ItemStack item) {
		if (click == ClickType.SHIFT_LEFT) {
			ItemStack clicked = item;
			clicked.setAmount(64);
			return item;
		} else if (click == ClickType.LEFT && cursor.getAmount() != 64) {
			if (cursor.getType() == item.getType()
					&& cursor.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
				item.setAmount(cursor.getAmount() + 1);
			} else {
				item.setAmount(1);
			}
			return item;
		}
		return null;
	}

	public void openInventory(Player player, Inventory inventory) {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			public void run() {
				Boolean exempt = false;
				if (InventoryClose.exempt.contains(player.getUniqueId()))
					exempt = true;
				else
					InventoryClose.exempt.add(player.getUniqueId());
				player.openInventory(inventory);
				if (exempt == false)
					InventoryClose.exempt.remove(player.getUniqueId());
			}
		});
	}

	public void waitTick(Runnable run) {
		Bukkit.getScheduler().runTask(plugin, run);
	}

	public Boolean containsInt(Integer[] list, int n) {
		for (int i : list) {
			if (i == n)
				return true;
		}
		return false;
	}

	public static Inventories get() {
		if (inventories == null)
			inventories = new Inventories();
		return inventories;
	}
}
