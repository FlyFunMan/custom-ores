package me.flyfunman.customos;

import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {
	CONFIG("Config Warning", "Explanation for the config can be found on the plugin page"),
	INCORRECT("Incorrect Command Syntax", "Incorrect command syntax. Please use [command]"),
    NOPERMS("No Permission", "You do not have permission to run this command!"),
    RELOAD("Reloaded", "Reloaded Successfully"),
    HELP("Help Menu", "§bCustom §aOres §eHelp Menu"),
	COMMAND("/customores", "Opens the help menu"),
	GIVE("/customores give", "Opens the Get Item menu"),
	GIVEITEM("/customores give <item> [amount] [player]", "Grants a custom item or ore"),
	CLEAR("/customores clear <world>", "Clears all ores in a world"),
	GENERATE("/customores generate <world>", "Regenerates ores in a world"),
	CREATE("/customores create <name> <type>", "Opens the item/ore/recipe creation menu."),
	DELETE("/customores delete <type> <name>", "Used to delete an item/ore/recipe. This is not reversable!"),
	RELOADS("/customores reload", "Reloads the config"),
	WIKI("Wiki Link", "§2§lClick §e§ohere §2§lto open the Custom Ores Wiki"),
	ALREADY("Already Creating", "You are already creating something!"),
	EXISTS("Already Exists", "A [type] with this name already exists!"),
	ILLEGAL("Illegal Character", "[character] is an illegal character and cannot be included in your name"),
	NOTREC("Not Recognized", "[name] was not recognized. Please make sure it is enabled in the config"),
	RECOGNIZEDN("Name Not Recognized", "[name] was not recognized as a [type]"),
	RECOGNIZEDT("Type Not Recognized", "The type [type] for the item [name] was not recognized!"),
	RECOGNIZEDE("Enchant Not Recognized", "The item [name]'s enchantment, [enchant], was not recognized. This enchantment will not be added"),
	RECOGNIZEDS("Smelt Not Recognized", "The Smelt Item for [name], [result] was not recognized. This smelting recipe cannot be created."),
	RECOGNIZEDR("Recipe Not Recognized", "The recipe [name]''s result, [result], was not recognized. This recipe cannot be created."),
	RECOGNIZEDIN("Ingredient Not Recognized", "The ingredient [ingredient] in the recipe [name] was not recognized. It will be set as air in the recipe."),
	DELCON("Delete Confirm 1", "Are you sure you want to delete [name]?"),
	DELCON2("Delete Confirm 2", "If you want to delete [name] then type [command]"),
	UNDO("Cannot Undo", "This action cannot be undone"),
	OREDELCON("Delete Ore Confirm", "[name] will be automatically cleared from all worlds, this may cause some lag"),
	OREDEL1("Delete Ore 1", "Starting to remove [name] from all worlds"),
	OREDEL2("Delete Ore 2", "Successfully removed [name] from [world]"),
	DELYES("Delete Success", "[name] was removed successfully."),
	CGENCONFIRM1("Clear/Generate Confirm 1", "Are you sure you want to do this?"),
	CGENCONFIRM2("Clear/Generate Confirm 2", "If you are sure, type [command]"),
	GENCONFIRM1("Generate Confirm 1", "It will cause ores to be able to generate in all chunks of this world"),
	GENCONFIRM2("Generate Confirm 2", "If you do this, [command] will not clear any ores that were generated before you ran this command"),
	CCONFIRM1("Clear Confirm 1", "It will remove all ores from the world"),
	CCONFIRM2("Clear Confirm 2", "Ores will still generate if you do not disable it in the config"),
	STARTGEN("Generate Start", "Starting to generate..."),
	STARTCLEAR("Clear Start", "Starting to clear..."),
	FINISH("Finished", "Finished!"),
	SPECIFY("Specify Item", "Please specify an item!"),
	RECIEVED("Recieved", "[player] recieved [amount] [name]"),
	NOTON("Not Online", "[player] is not online"),
	SPECIFYP("Specify Player", "Please specify a player"),
	TMETHOD("Texture Method", "Choose a texture method"),
	TCHAT("Texture in Chat", "Type texture value in chat"),
	TCONFIG1("Texture in Config 1", "Type texture value in config"),
	TCONFIG2("Texture in Config 2", "The ore will not generate until the texture value is set"),
	SEITEM("Select Item", "Select an item"),
	LORECH("Lore Choice", "Would you like a lore?"),
	CENCHANTS("Custom Enchants", "§cYou appear to have a custom enchantments plugin, if you would like to use this plugin with Custom Ores, you should probably enable §o§eCustom Enchantments Display §r§cin the config"),
	TRUE("True", "True"),
	FALSE("False", "False"),
	ENABLE("Enable All", "Enable All"),
	DISABLE("Disable All", "Disable All"),
	YVAL("Y Value", "Y"),
	ORESET("Ore Settings", "Ore Settings"),
	OVER("Overworld", "Spawn in Overworld"),
	NETHER("Nether", "Spawn in Nether"),
	END("End", "Spawn in End"),
	MAX("Max Spawn", "Max Spawn Height"),
	MIN("Min Spawn", "Min Spawn Height"),
	AMNT("Amount Per Chunk", "Amount Per Chunk"),
	ORET("Ore Type", "Ore Type"),
	SMELT("Smeltable", "Smeltable"),
	SMELTI("Smelt Item", "Smelt Item Goes Here"),
	DROPS("Drops", "Drops"),
	DROPSI("Drop Item", "Drop Item Goes Here"),
	CREATES("Create", "Create [name]"),
	CREATER("Create Recipe", "Create Recipe"),
	ENCHP("Enchantments Page", "Enchantments Page"),
	BP("Biomes Page", "Biomes Page"),
	LEVEL("Level", "Level"),
	NEXT("Next", "Next"),
	NOITS("No Items", "You haven't created any items yet!"),
	GETIT("Get Item", "Get Item"),
	RESULT("Result Required", "You need to have a result!"),
	CHAT("Chat", "Type in the chat. You have 60 seconds."),
	NOSPACE("No Spaces", "Please don't use spaces in the texture!"),
	TRESET("Texture Reset", "You took too long and the texture was set to default! You can still change it through the config."),
	LRESET("Lore Reset", "You took too long and a lore was not added! You can still add one through the config."),
	ICANCEL("Item Cancel", "Item Creation Cancelled!"),
	OCANCEL("Ore Cancel", "Ore Creation Cancelled!"),
	ICREATE("Item Created", "Item Created"),
	OCREATE("Ore Created", "Ore Created"),
	STHEAD("Storage Header", "DO NOT MESS WITH THIS UNLESS YOU KNOW WHAT YOU'RE DOING!"),
	BADSET("Setup Fail", "Something went wrong during [file] setup. This could cause problems when the server restarts. If this error continues, report it to the developer."),
	BADSAVE("Save Fail", "Couldn't save [file]. This could cause problems when the server restarts. Please report this to the developer.");
	
 
    private String path;
    private String def;
    private static YamlConfiguration LANG;
 
    /**
    * Lang enum constructor.
    * @param path The string path.
    * @param start The default string.
    */
    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }
 
    /**
    * Set the {@code YamlConfiguration} to use.
    * @param config The config to set.
    */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }
 
    @Override
    public String toString() {
        return LANG.getString(this.path, def);
    }
 
    /**
    * Get the default value of the path.
    * @return The default value of the path.
    */
    public String getDefault() {
        return this.def;
    }
 
    /**
    * Get the path to the string.
    * @return The path to the string.
    */
    public String getPath() {
        return this.path;
    }

}
