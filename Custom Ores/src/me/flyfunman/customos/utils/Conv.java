package me.flyfunman.customos.utils;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.flyfunman.customos.Main;
import me.flyfunman.customos.inventories.Creation;
import me.flyfunman.customos.listeners.Inventories;
import me.flyfunman.customos.listeners.InventoryClose;

public class Conv extends StringPrompt {
	Plugin plugin = Main.getPlugin(Main.class);

	@Override
	public String getPromptText(ConversationContext arg0) {
		return ChatColor.AQUA + "Type in the chat. You have 60 seconds.";
	}

	@Override
	public Prompt acceptInput(ConversationContext cc, String ans) {
		if (cc.getForWhom() instanceof Player) {
			Player player = (Player) cc.getForWhom();
			UUID uuid = player.getUniqueId();
			if (Inventories.addtexture.contains(uuid)) {
				if (ans.contains(" ")) {
					cc.getForWhom().sendRawMessage(ChatColor.RED + "Please don't use spaces in the texture!");
					return this;
				} else {
					Inventories.values.put(uuid, ans);
					Inventories.addtexture.remove(uuid);
					CustomConfig.saveItems();
					player.openInventory(Creation.get().ore());
				}
			} else if (Inventories.addLore.contains(uuid)) {
				ItemCreator.get().addItem(uuid, ans);
			}
			InventoryClose.exempt.remove(uuid);
		}
		return END_OF_CONVERSATION;
	}
}
