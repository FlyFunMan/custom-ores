package me.flyfunman.customos.listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.flyfunman.customos.Main;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
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
							finalDrop = item.getSmelt();
							
						//fix drop item if it is null, then drop it
						if (finalDrop == null)
							finalDrop = item.getStack(1);
						
						e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), finalDrop);
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
			if (contains(plugin.getConfig().getStringList("Tools"),
					e.getPlayer().getInventory().getItemInMainHand().getType().name())) {
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

	public boolean contains(List<String> list, String s) {
		for (String string : list) {
			if (string.replace(' ', '_').equalsIgnoreCase(s.replace(' ', '_')))
				return true;
		}
		return false;
	}
}
