package me.flyfunman.customos;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.flyfunman.customos.commands.ConstructTabCompleter;
import me.flyfunman.customos.commands.Create;
import me.flyfunman.customos.commands.Delete;
import me.flyfunman.customos.commands.Generation;
import me.flyfunman.customos.commands.Give;
import me.flyfunman.customos.inventories.Creation;
import me.flyfunman.customos.listeners.BlockBreak;
import me.flyfunman.customos.listeners.Inventories;
import me.flyfunman.customos.listeners.InventoryClose;
import me.flyfunman.customos.listeners.PlayerMove;
import me.flyfunman.customos.listeners.Recipes;
import me.flyfunman.customos.objects.ChunkLoc;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.CustomConfig;
import me.flyfunman.customos.utils.RecipeCreator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin implements Listener {
	ChunkLoc chunkLoc = new ChunkLoc();

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		updateConfig();

		CustomConfig.setup();
		CustomConfig.storage().options().copyDefaults(true);
		CustomConfig.items().options().copyDefaults(true);
		CustomConfig.recipes().options().copyDefaults(true);
		CustomConfig.saveAll();
		Item.load();
		RecipeCreator.get().createRecipes();

		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new Inventories(), this);
		pm.registerEvents(new PlayerMove(), this);
		pm.registerEvents(new Recipes(), this);
		pm.registerEvents(new InventoryClose(), this);
		pm.addPermission(new Permission("customores.create"));
		pm.addPermission(new Permission("customores.delete"));
		pm.addPermission(new Permission("customores.clear"));
		pm.addPermission(new Permission("customores.generate"));
		pm.addPermission(new Permission("customores.give"));
		pm.addPermission(new Permission("customores.reload"));

		getCommand("customores").setTabCompleter(new ConstructTabCompleter());

		Creation.get().setup();

		getServer().getConsoleSender()
				.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCustom &aOres &eLoaded Successfully"));
	}

	@Override
	public void onDisable() {
		saveChunks();
		getServer().getConsoleSender()
				.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCustom &aOres &eShut Down Successfully"));
	}

	public void updateConfig() {
		try {
			if (new File(getDataFolder() + "/config.yml").exists()) {
				boolean changesMade = false;
				YamlConfiguration tmp = new YamlConfiguration();
				tmp.load(getDataFolder() + "/config.yml");
				for (String str : getConfig().getKeys(true)) {
					if (!tmp.getKeys(true).contains(str)) {
						tmp.set(str, getConfig().get(str));
						changesMade = true;
					}
				}
				if (changesMade)
					tmp.save(getDataFolder() + "/config.yml");
				getConfig().options().header("Explanation for the config can be found on the plugin page");
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveChunks() {
		for (ChunkLoc chunk : chunkLoc.getChunks()) {
			chunk.addToConfig();
		}

		chunkLoc.getChunks().clear();
		CustomConfig.storage().options().copyDefaults(true);
		CustomConfig.saveStorage();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("customores"))
			return false;
		if (args.length >= 1) {
			switch (args[0].toLowerCase()) {
			case "give":
				Give.giveItem(sender, args);
				return true;
			case "create":
				Create.get().start(sender, args);
				return true;
			case "delete":
				Delete.get().start(sender, args);
				return true;
			case "clear":
			case "generate":
				Generation.command(sender, args);
				return true;
			case "reload":
				if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)
						&& !sender.hasPermission("customores.reload")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
				} else {
					reloadConfig();
					RecipeCreator.get().clearRecipes();
					CustomConfig.setup();
					Item.load();
					RecipeCreator.get().createRecipes();
					sender.sendMessage(ChatColor.GREEN + "Reloaded Successfully!");
				}
				return true;
			}
		}
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCustom &aOres &eHelp Menu"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/customores: &aOpens the help menu"));
		player(sender, "&b/customores give: &aOpens the Get Item menu");
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&b/customores give <item> [amount] [player]: &aGrants a custom item or ore"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&b/customores clear <world>: &aClears all ores in a world"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&b/customores generate <world>: &aRegenerates ores in a world"));
		player(sender, "&b/customores create <name> <type>: &aOpens the item/ore/recipe creation menu");
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&b/customores delete <type> <name>: &aUsed to delete an item/ore/recipe."
						+ " This is not reversable!"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/customores reload: &aReloads the config"));
		if (sender instanceof Player) {
			TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', 
					"&2&lClick &e&ohere &2&lto open the Custom Ores Wiki"));
			message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://custom-ores.fandom.com/wiki/Custom_Ores_Wiki"));
			((Player) sender).spigot().sendMessage(message);
		}
		return true;
	}

	private void player(CommandSender sender, String string) {
		if (sender instanceof Player)
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
	}
}