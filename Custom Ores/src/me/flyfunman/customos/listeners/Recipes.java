package me.flyfunman.customos.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.plugin.Plugin;

import me.flyfunman.customos.Main;

public class Recipes implements Listener {
	Plugin plugin = Main.getPlugin(Main.class);
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRecipe(PlayerRecipeDiscoverEvent e) {
		if (e.getRecipe().getKey().contains("customores")) {
			e.setCancelled(true);
		}
	}
}
