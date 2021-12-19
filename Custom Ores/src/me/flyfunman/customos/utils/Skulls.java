package me.flyfunman.customos.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Skulls {
	public String getSkullValue(ItemStack head) {
		if (head.getType() != Material.PLAYER_HEAD)
			return null;
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		String url = null;
		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			GameProfile profile = (GameProfile) profileField.get(headMeta);
			Collection<Property> properties = profile.getProperties().get("textures");
			for (Property property : properties) {
				url = property.getValue();
			}
		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
			error.printStackTrace();
		}
		return url;
	}

	public void setHead(Block b, String url) {
		b.setType(Material.PLAYER_HEAD, false);
		BlockState headData = b.getState();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);

		profile.getProperties().put("textures", new Property("textures", url));
		try {
			Field profileField = headData.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headData, profile);
		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
			error.printStackTrace();
		}
		headData.update(false, false);
	}

	public ItemStack createSkull(String url, String name) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		if (url.isEmpty())
			return head;

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();

		if (!CustomConfig.storage().contains("UUIDS." + name)) {
			CustomConfig.storage().set("UUIDS." + name, UUID.randomUUID().toString());
			CustomConfig.saveStorage();
		}
		
		GameProfile profile = new GameProfile(UUID.fromString(CustomConfig.storage().getString("UUIDS." + name)), null);

		profile.getProperties().put("textures", new Property("textures", url));

		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);

		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
			error.printStackTrace();
		}
		
		head.setItemMeta(headMeta);
		return head;
	}

	public static Skulls get() {
		Skulls skulls = new Skulls();
		return skulls;
	}
}