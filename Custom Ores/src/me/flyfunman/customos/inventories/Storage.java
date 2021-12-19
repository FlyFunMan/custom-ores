package me.flyfunman.customos.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.flyfunman.customos.objects.Item;

public class Storage {
	public static void open(Player player) {
		double size = 0;
		for (Item item : Item.items) {
			if (item.isEnabled()) {
				size++;
			}
		}
		size = roundUp(size, 9);
		if (size == 0) {
			player.sendMessage(ChatColor.RED + "You haven't created any items yet!");
			return;
		}
		Inventory get = Bukkit.createInventory(null, (int) size, ChatColor.GREEN + "Get Item");
		size = 0;
		for (Item item : Item.items) {
			if (item.isEnabled()) {
				get.setItem((int) size, item.getStack(1));
				size++;
			}
		}
		player.openInventory(get);
	}

	public static int roundUp(double i, double v) {
		return (int) (Math.ceil(Math.abs(i / v)) * v);
	}
}
