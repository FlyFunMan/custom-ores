package me.flyfunman.customos.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.recipes.CustomRecipe;
import io.th0rgal.oraxen.recipes.listeners.RecipesEventsManager;
import me.flyfunman.customos.Main;

public class RecipeCreator {
	private static RecipeCreator instance;
	public static List<ShapedRecipe> recipes = new ArrayList<>();
	Plugin plugin = Main.getPlugin(Main.class);

	public void createRecipes() {
		if (!plugin.getConfig().getBoolean("Enabled"))
			return;
		if (CustomConfig.recipes().getKeys(false).isEmpty())
			return;
		for (String path : CustomConfig.recipes().getKeys(false)) {
			if (CustomConfig.recipes().contains(path + ".Enabled")
					&& CustomConfig.recipes().getBoolean(path + ".Enabled")) {
				createRecipe(path);
			}
		}
		for (String path : CustomConfig.items().getKeys(false)) {
			if (CustomConfig.items().contains(path + ".Smelt Item")
					&& !CustomConfig.items().getString(path + ".Smelt Item").equalsIgnoreCase("air")) {
				addFurnace(path);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void addFurnace(String path) {
		if (ItemCreator.get().getFromString(path, 1) != null) {
			ItemStack result = null;
			RecipeChoice rc = new RecipeChoice.ExactChoice(ItemCreator.get().getFromString(path, 1));
			String smelt = CustomConfig.items().getString(path + ".Smelt Item").toLowerCase().replace(' ', '_');
			int amount = 1;
			if (CustomConfig.items().contains(path + ".Smelt Amount"))
				amount = CustomConfig.items().getInt(path + ".Smelt Amount");
			
			result = ItemCreator.get().getFromString(smelt, amount);
			if (result == null || !result.getType().isItem()) {
				if (oraxen() && OraxenItems.exists(smelt.toLowerCase())) {
					result = OraxenItems.getItemById(smelt.toLowerCase()).build();
					result.setAmount(amount);
				} else {
					plugin.getServer().getConsoleSender().sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&bCustom &aOres: &cThe Smelt Item for " + path
									+ ", " + smelt + ", was not recognized. This smelting recipe cannot be created."));
					return;
				}
			} 
			Bukkit.addRecipe(
					new FurnaceRecipe(new NamespacedKey(plugin, "customores_" + simplify(path.replace(' ', '_'))),
							result, rc, (float) 0.2, 120));
		}
	}

	@SuppressWarnings("deprecation")
	public void createRecipe(String path) {
		boolean oraxen = false;
		
		//get strings from config
		String result = CustomConfig.recipes().getString(path + ".Result").replace(' ', '_');
		int amount = CustomConfig.recipes().getInt(path + ".Amount");

		//Check if result is in Custom Ores or Vanilla
		ItemStack resultItem = ItemCreator.get().getFromString(result, amount);
		
		//if is not, check if oraxen or null
		if (resultItem == null || !resultItem.getType().isItem()) {
			if (oraxen() && OraxenItems.exists(result.toLowerCase())) {
				oraxen = true;
				resultItem = OraxenItems.getItemById(result.toLowerCase()).build();
			} else {
				plugin.getServer().getConsoleSender()
						.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCustom &aOres: &cThe recipe " + path
								+ "'s result, " + result + ", was not recognized. This recipe cannot be created."));
				return;
			}
		//check if result is oraxen if it is an item
		} else if (oraxen) {
			if (OraxenItems.exists(OraxenItems.getIdByItem(resultItem)))
				oraxen = true;
		}
		
		//instantialize recipe
		ShapedRecipe custom = new ShapedRecipe(
				new NamespacedKey(plugin, "customores_" + simplify(path.replace(' ', '_'))), resultItem);
		char[] recipeLetters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };
		String[] ingredients = { CustomConfig.recipes().getString(path + ".TopLeft"),
				CustomConfig.recipes().getString(path + ".TopCenter"),
				CustomConfig.recipes().getString(path + ".TopRight"),
				CustomConfig.recipes().getString(path + ".MiddleLeft"),
				CustomConfig.recipes().getString(path + ".MiddleCenter"),
				CustomConfig.recipes().getString(path + ".MiddleRight"),
				CustomConfig.recipes().getString(path + ".BottomLeft"),
				CustomConfig.recipes().getString(path + ".BottomCenter"),
				CustomConfig.recipes().getString(path + ".BottomRight"), };
		for (int x = 0; x < ingredients.length; x++) {
			if (ingredients[x].equalsIgnoreCase("air")) {
				recipeLetters[x] = ' ';
			} else if (ItemCreator.get().getFromString(ingredients[x], 1) == null
					&& !(oraxen() && OraxenItems.exists(ingredients[x].toLowerCase()))) {
				plugin.getServer().getConsoleSender()
						.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&bCustom &aOres: &cThe ingredient " + ingredients[x] + " in the recipe " + path
										+ " was not recognized. It will be set as air in the recipe."));
				recipeLetters[x] = ' ';
			}
		}
		
		//split into rows
		String row1 = "", row2 = "", row3 = "";
		for (int x = 0; x < recipeLetters.length; x++) {
			if (x < 3) {
				row1 += recipeLetters[x];
			} else if (x < 6) {
				row2 += recipeLetters[x];
			} else {
				row3 += recipeLetters[x];
			}
		}
		
		boolean checkMid = false, checkMid2 = false;
		
		//group rows
		String[] rows = {row1, row2, row3};
		
		//check last column
		if (checkSpaces(rows, 2)) {
			for (int i = 0; i < 3; i++)
				//remove last column
				rows[i] = rows[i].substring(0, 2);
			
			checkMid2 = true;
		}
		
		//check first column
		if (checkSpaces(rows, 0)) {
			for (int i = 0; i < 3; i++)
				//remove first column
				rows[i] = rows[i].substring(1);
			
			checkMid = true;
		}
		
		//check middle column if other column is gone
		if (checkMid != checkMid2) {
			if (checkMid && checkSpaces(rows, 0))
				for (int i = 0; i < 3; i++)
					//remove first column
					rows[i] = rows[i].substring(1);
			
			if (checkMid2 && checkSpaces(rows, 1))
				for (int i = 0; i < 3; i++)
					//remove last column
					rows[i] = rows[i].substring(0, 1);
		}
		
		//remove extra rows
		boolean delRow1 = false, delRow3 = false; 
		
		List<String> rowsCopy = new ArrayList<String>(Arrays.asList(rows));
		
		//try delete row 1 & 3
		for (int i = 0; i < 3; i += 2) {
			boolean deleteRow = true;
			for (int column = 0; column < rows[i].length(); column++) {
				if (rows[i].charAt(column) != ' ') {
					deleteRow = false;
					break;
				}
			}
			
			if (deleteRow) {
				if (i == 0) delRow1 = true;
				if (i == 2) delRow3 = true;
				
				rowsCopy.remove(rows[i]);
			}
		}
		
		//delete row 2 if it is exposed
		if (delRow1 != delRow3) {
			boolean deleteRow = true;
			for (int i = 0; i < rows[1].length(); i++) {
				if (rows[1].charAt(i) != ' ') {
					deleteRow = false;
					break;
				}
			}
			
			if (deleteRow) rowsCopy.remove(rows[1]);
		}
		rows = rowsCopy.toArray(new String[0]);
		
		if (rows.length == 1) custom.shape(rows[0]);
		else if (rows.length == 2) custom.shape(rows[0], rows[1]);
		else custom.shape(rows[0], rows[1], rows[2]);
		for (int x = 0; x < ingredients.length; x++) {
			if (recipeLetters[x] != ' ') {
				if (ItemCreator.get().isCustom(ingredients[x])) {
					RecipeChoice ingredient = new RecipeChoice.ExactChoice(
							ItemCreator.get().getFromString(ingredients[x], 1));
					custom.setIngredient(recipeLetters[x], ingredient);
					if (oraxen() 
							&& OraxenItems.exists(CustomConfig.items().getString(getItemCase(ingredients[x]) + ".Type")))
						oraxen = true;
				} else if (oraxen() && OraxenItems.exists(ingredients[x].toLowerCase())
						&& !ingredients[x].replace(' ', '_').equalsIgnoreCase("diamond_sword")) {
					oraxen = true;
					RecipeChoice ingredient = new RecipeChoice.ExactChoice(
							OraxenItems.getItemById(ingredients[x].toLowerCase()).build());
					custom.setIngredient(recipeLetters[x], ingredient);
				} else
					custom.setIngredient(recipeLetters[x], new RecipeChoice.ExactChoice(
							new ItemStack(Material.getMaterial(ingredients[x].toUpperCase().replace(' ', '_')))));
			}
		}
		Bukkit.addRecipe(custom);
		if (oraxen == true) {
			if (recipes.contains(custom))
				return;
			RecipesEventsManager.get().whitelistRecipe(CustomRecipe.fromRecipe(custom));
			recipes.add(custom);
		}
	}
	
	private boolean checkSpaces(String[] rows, int column) {
		
		Boolean remove = true;
		
		for (int i = 0; i < 3; i++)
			if(rows[i].charAt(column) != ' ')  {
				remove = false;
				break;
			}
		
		return remove;
	}
	
	public String getItemCase(String item) {
		String name = item.replace(' ', '_');
		for (String it : CustomConfig.items().getKeys(false)) {
			if (it.replace(' ', '_').equalsIgnoreCase(name))
				return it;
		}
		return null;
	}

	public void addRecipe(String path, InventoryView view, Integer[] slot) {
		CustomConfig.recipes().set(path + ".Enabled", true);
		String result;
		if (oraxen() && OraxenItems.getIdByItem(view.getItem(slot[9])) != null && !ItemCreator.get()
				.contains(CustomConfig.items().getKeys(false), ItemCreator.get().getFromItem(view.getItem(slot[9])))) {
			result = OraxenItems.getIdByItem(view.getItem(slot[9]));
		} else
			result = ItemCreator.get().getFromItem(view.getItem(slot[9]));
		CustomConfig.recipes().set(path + ".Result", result);
		CustomConfig.recipes().set(path + ".Amount", view.getItem(slot[9]).getAmount());
		String[] spot = { ".TopLeft", ".TopCenter", ".TopRight", ".MiddleLeft", ".MiddleCenter", ".MiddleRight",
				".BottomLeft", ".BottomCenter", ".BottomRight", };
		for (int x = 0; x < 9; x++) {
			String ingredient;
			if (oraxen() && OraxenItems.getIdByItem(view.getItem(slot[x])) != null
					&& !ItemCreator.get().contains(CustomConfig.items().getKeys(false),
							ItemCreator.get().getFromItem(view.getItem(slot[x])))) {
				ingredient = OraxenItems.getIdByItem(view.getItem(slot[x]));
			} else
				ingredient = ItemCreator.get().getFromItem(view.getItem(slot[x]));
			CustomConfig.recipes().set(path + spot[x], ingredient);
		}
		CustomConfig.saveRecipes();
		createRecipe(path);
	}

	public boolean oraxen() {
		if (plugin.getServer().getPluginManager().getPlugin("Oraxen") != null)
			return true;
		return false;
	}

	public String simplify(String s) {
		return s.replaceAll("[^a-zA-Z0-9_-]", "");
	}

	public void clearRecipes() {
		Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
		while (it.hasNext()) {
			Recipe r = it.next();
			if (r instanceof Keyed && ((Keyed) r).getKey().toString().contains("customores"))
				it.remove();
		}
	}

	public void removeRecipe(String string) {
		Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
		while (it.hasNext()) {
			Recipe r = it.next();
			if (!(r instanceof Keyed))
				continue;
			String key = simplify(((Keyed) r).getKey().toString().toLowerCase());
			if (key.contains("customores") && key.contains(simplify(string.toLowerCase())))
				it.remove();
		}
	}

	public static RecipeCreator get() {
		if (instance == null) {
			instance = new RecipeCreator();
		}
		return instance;
	}
}
