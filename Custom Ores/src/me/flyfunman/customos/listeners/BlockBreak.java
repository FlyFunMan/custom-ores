package me.flyfunman.customos.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.th0rgal.oraxen.items.OraxenItems;
import me.flyfunman.customos.Main;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.RecipeCreator;
import me.flyfunman.customos.utils.Skulls;

public class BlockBreak implements Listener {
	Plugin plugin = Main.getPlugin(Main.class);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreakBlock(BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE && !plugin.getConfig().getBoolean("Drop in Creative"))
			return;
		if (e.getBlock().getType() == Material.PLAYER_HEAD || e.getBlock().getType() == Material.PLAYER_WALL_HEAD) {
			for (Item item : Item.items) {
				for (ItemStack drop : e.getBlock().getDrops())
					if (item.isOre() && (item.getValue().equals(Skulls.get().getSkullValue(drop)))) {
						e.setCancelled(true);
						e.getBlock().setType(Material.AIR);
						
						ItemStack finalDrop = null;
						
						if (CustomConfig.items().contains(item.getName() + ".Smelt Item") &&
								CustomConfig.items().contains(item.getName() + ".Drop Not Smelt") && 
								CustomConfig.items().getBoolean(item.getName() + ".Drop Not Smelt"))
							finalDrop = item.getSmelt().clone();
							
						//fix drop item if it is null, otherwise apply fortune
						if (finalDrop == null || (plugin.getConfig().getBoolean("Allow Silk Touch") 
								&& e.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(
										Enchantment.SILK_TOUCH)))
							
							finalDrop = item.getStack(1);
						
						else if (plugin.getConfig().getBoolean("Allow Fortune") && e.getPlayer().getInventory().getItemInMainHand()
								.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
							
							Random rand = new Random();
							
							float chance = rand.nextFloat();
							
							float level = e.getPlayer().getInventory().getItemInMainHand().getEnchantments()
									.get(Enchantment.LOOT_BONUS_BLOCKS);
							if (chance > (2f/(level + 2f))) {
								int amount = item.getSmelt().getAmount() * ((int) Math.ceil((1f/(level + 2f) + (level + 1f)/2f)));
								
								if (amount > 0) finalDrop.setAmount(amount);
							}
						}
						
						//drop it
						e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), finalDrop);
						return;
					}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPickuse(BlockDamageEvent e) {
		if (e.isCancelled() || e.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (e.getBlock() != null && e.getBlock().getType() != null && (e.getBlock().getType() == Material.PLAYER_HEAD
				|| e.getBlock().getType() == Material.PLAYER_WALL_HEAD)) {
			if (isTool(e.getPlayer().getInventory().getItemInMainHand())) {
				for (Item item : Item.items) {
					for (ItemStack it : e.getBlock().getDrops()) {
						if (item.isOre() && item.getValue().equals(Skulls.get().getSkullValue(it))) {
							e.setInstaBreak(true);
							e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_BREAK, 10,
									1);
						}
					}
				}
			}
		}
	}
	
	private boolean isTool(ItemStack tool) {
		//Check oraxen (priority)
		if (RecipeCreator.get().oraxen() && OraxenItems.getIdByItem(tool) != null) {
			if (contains(plugin.getConfig().getStringList("Tools"), OraxenItems.getIdByItem(tool)))
				return true;
			
			return false;
		}
		
		//Check type
		if (contains(plugin.getConfig().getStringList("Tools"), tool.getType().name())) return true;
		
		//Neither
		return false;
	}

	public boolean contains(List<String> list, String s) {
		for (String string : list) {
			if (string.replace(' ', '_').equalsIgnoreCase(s.replace(' ', '_')))
				return true;
		}
		return false;
	}
}
