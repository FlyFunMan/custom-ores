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
	/*
	@EventHandler(priority = EventPriority.HIGH)
	public void onPrepare(PrepareItemCraftEvent e) {
		//check config
		if (plugin.getConfig().getBoolean("CustomItemsOtherRecipes")) return;
		
		if (e.getRecipe() instanceof ShapedRecipe) {
			//check if is a custom ores recipe
			for (int i = 0; i < RecipeCreator.get().recipes.size(); i++)
				if (e.getRecipe == RecipeCreator.get().recipes.get(i).getChoiceMap())
					return;
			
			//check if recipe is whitelisted
			List<ShapedRecipe> recipes = CustomOresAPI.get().whitelistedRecipes;
			
			for (int i = 0; i < recipes.size(); i++)
				if (recipes.get(i).getChoiceMap() == e.getRecipe())
					return;
		}
			
		//cancel recipe if there is a custom item
		for (int i = 0; i < e.getInventory().getMatrix().length; i++)
			if (ItemCreator.get().isCustom(ItemCreator.get().getFromItem(e.getInventory().getMatrix()[i]))) {
				e.getInventory().setResult(Inventories.get().air);
				return;
			}
	}*/
}
