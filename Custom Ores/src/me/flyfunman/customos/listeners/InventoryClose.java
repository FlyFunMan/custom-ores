package me.flyfunman.customos.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.flyfunman.customos.CreateLang;
import me.flyfunman.customos.commands.Create;
import me.flyfunman.customos.inventories.Creation;

public class InventoryClose implements Listener {
	public static List<UUID> exempt = new ArrayList<>();

	@EventHandler
	public void closeEvent(InventoryCloseEvent e) {
		String title = e.getView().getTitle();
		if (title.equals(CreateLang.getString(ChatColor.DARK_AQUA, "Create Recipe"))) {
			for (int i : Creation.get().slots) {
				ItemStack item = e.getView().getItem(i);
				if (item != null && item.getType() != null && item.getType() != Material.AIR)
					e.getPlayer().getInventory().addItem(item);
			}
		}
		if (e.getPlayer() == null || e.getPlayer().getUniqueId() == null
				|| exempt.contains(e.getPlayer().getUniqueId()))
			return;
		if (Create.items.containsKey(e.getPlayer().getUniqueId())) {
			Create.items.remove(e.getPlayer().getUniqueId());
			e.getPlayer().sendMessage(CreateLang.getString(ChatColor.RED, "Item Cancel"));
		} else if (Create.ores.containsKey(e.getPlayer().getUniqueId())) {
			Create.ores.remove(e.getPlayer().getUniqueId());
			e.getPlayer().sendMessage(CreateLang.getString(ChatColor.RED, "Ore Cancel"));
		}
	}

	@EventHandler
	public void leaveEvent(PlayerQuitEvent e) {
		if (e.getPlayer() == null || e.getPlayer().getUniqueId() == null) return;
		
		if (exempt.contains(e.getPlayer().getUniqueId()))
			exempt.remove(e.getPlayer().getUniqueId());
		if (Create.items.containsKey(e.getPlayer().getUniqueId()))
			Create.items.remove(e.getPlayer().getUniqueId());
		if (Create.ores.containsKey(e.getPlayer().getUniqueId()))
			Create.ores.remove(e.getPlayer().getUniqueId());
	}
}
