package me.flyfunman.customos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
	public static YamlConfiguration LANG;
	public static File LANG_FILE;

	@Override
	public void onEnable() {
		loadLang();
		
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		updateConfig();

		CustomConfig.setup();
		CustomConfig.storage().options().copyDefaults(true);
		CustomConfig.items().options().copyDefaults(true);
		CustomConfig.recipes().options().copyDefaults(true);
		CustomConfig.saveAll();

		Creation.get().setup();
		
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
				getConfig().options().header(CreateLang.getString(ChatColor.DARK_RED, "Config Warning"));
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
					sender.sendMessage(CreateLang.getString(ChatColor.RED, "No Permission"));
				} else {
					reloadConfig();
					
					File file = new File(Bukkit.getServer().getPluginManager().getPlugin("CustomOres").getDataFolder(),
							"Lang.yml");
					Lang.setFile(YamlConfiguration.loadConfiguration(file));
					
					RecipeCreator.get().clearRecipes();
					CustomConfig.setup();
					Item.load();
					RecipeCreator.get().createRecipes();
					sender.sendMessage(CreateLang.getString(ChatColor.GREEN, "Reloaded"));
				}
				return true;
			}
		}
		sender.sendMessage(CreateLang.getString(ChatColor.AQUA, "Help Menu"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				"&b/customores: " + CreateLang.getString(ChatColor.GREEN, "/customores")));
		player(sender, "&b/customores give: " + CreateLang.getString(ChatColor.GREEN, "/customores give"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&b/customores give <item> [amount] [player]: ") 
				+ CreateLang.getString(ChatColor.GREEN, "/customores give <item> [amount] [player]"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&b/customores clear <world>: ") + CreateLang.getString(ChatColor.GREEN, "/customores clear <world>"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&b/customores generate <world>: ") + CreateLang.getString(ChatColor.GREEN, "/customores generate <world>"));
		player(sender, "&b/customores create <name> <type>: " + CreateLang.getString(ChatColor.GREEN, "/customores create <name> <type>"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&b/customores delete <type> <name>: ") + CreateLang.getString(ChatColor.GREEN, "/customores delete <type> <name>"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/customores reload: ")
				+ CreateLang.getString(ChatColor.GREEN, "/customores reload"));
		if (sender instanceof Player) {
			TextComponent message = new TextComponent(CreateLang.getString(ChatColor.ITALIC, "Wiki Link"));
			message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://custom-ores.fandom.com/wiki/Custom_Ores_Wiki"));
			((Player) sender).spigot().sendMessage(message);
		}
		return true;
	}

	private void player(CommandSender sender, String string) {
		if (sender instanceof Player)
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
	}
	
	public YamlConfiguration loadLang() {
		File lang = new File(getDataFolder(), "lang.yml");
		if (!lang.exists()) {
			try {
				getDataFolder().mkdir();
				lang.createNewFile();
				InputStream defConfigStream = this.getResource("lang.yml");
				if (defConfigStream != null) {
					YamlConfiguration defConfig = YamlConfiguration
							.loadConfiguration(new InputStreamReader(defConfigStream));
					defConfig.save(lang);
					Lang.setFile(defConfig);
					return defConfig;
				}
			} catch (IOException e) {
				e.printStackTrace(); // So they notice
				getLogger().severe(ChatColor.RED + "[Custom Ores] Couldn't create language file.");
				getLogger().severe(ChatColor.RED + "[Custom Ores] This is a fatal error. Now disabling");
				this.setEnabled(false); // Without it loaded, we can't send them messages
			}
		}
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		for (Lang item : Lang.values()) {
			if (conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}
		Lang.setFile(conf);
		LANG = conf;
		LANG_FILE = lang;
		try {
			conf.options().header("Use § for color codes (e. g. §3)");
			conf.save(getLangFile());
		} catch (IOException e) {
			getLogger().warning(ChatColor.RED + "[Custom Ores] Failed to save lang.yml.");
			getLogger().warning(ChatColor.RED + "[Custom Ores] Report this stack trace to the developer.");
			e.printStackTrace();
		}
		return conf;
	}

	public YamlConfiguration getLang() {
		return LANG;
	}

	public File getLangFile() {
		return LANG_FILE;
	}
}