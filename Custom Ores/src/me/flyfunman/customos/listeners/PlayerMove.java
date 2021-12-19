package me.flyfunman.customos.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import me.flyfunman.customos.Main;
import me.flyfunman.customos.objects.ChunkLoc;
import me.flyfunman.customos.objects.Item;
import me.flyfunman.customos.utils.Skulls;

public class PlayerMove implements Listener {
	private static HashMap<String, Integer> tries = new HashMap<>();
	private static HashMap<String, Integer> time = new HashMap<>();
	private static HashMap<String, Integer> taskID = new HashMap<>();
	Plugin plugin = Main.getPlugin(Main.class);
	public static List<String> chunk = new ArrayList<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkEnter(PlayerMoveEvent e) {
		if (plugin.getConfig().getBoolean("Enabled")) {
			int x = 16 * e.getTo().getChunk().getX();
			int z = 16 * e.getTo().getChunk().getZ();
			Location l = e.getTo().getChunk().getWorld().getBlockAt(x, 100, z).getLocation();
			if (!ChunkLoc.isChunkLoc(l) && !ChunkLoc.isInConfig(l)) {
				if (!plugin.getConfig().getBoolean("All Worlds")
						&& !plugin.getConfig().getList("Worlds").contains(e.getTo().getWorld().getName())) {
					return;
				}
				if (plugin.getConfig().getBoolean("All Worlds")
						&& plugin.getConfig().getList("Worlds").contains(e.getTo().getWorld().getName())) {
					return;
				}
				new ChunkLoc(l);
				for (Item ore : Item.items) {
					if (ore.isEnabled() && ore.isOre() && !ore.getValue().equals(Inventories.get().Default)) {
						if ((l.getWorld().getEnvironment() == Environment.NORMAL && ore.isOverworld())
								|| (l.getWorld().getEnvironment() == Environment.NETHER && ore.isNether())
								|| (l.getWorld().getEnvironment() == Environment.THE_END && ore.isEnd())) {
							startTimer(ore, x, z, l.getWorld(), ore.getNumPerChunk());
						}
					}
				}
			}
		}
	}

	public void startTimer(Item ore, int x, int z, World world, int t) {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		Random rand = new Random();
		String bl = world.getName() + x + z + ore.getName() + rand.nextInt();
		time.put(bl, t);
		tries.put(bl, t * 3);
		taskID.put(bl, scheduler.scheduleSyncRepeatingTask(plugin, () -> {
			if (tries.get(bl) == 0 || time.get(bl) == 0) {
				stopTimer(bl);
				return;
			} else {
				int rand1 = rand.nextInt(14);
				int rand2 = rand.nextInt(14);
				int randy = ore.getMinY() + rand.nextInt(1 + ore.getMaxY() - ore.getMinY());
				int randx = 1 + rand1 + x;
				int randz = 1 + rand2 + z;
				Block blo = world.getBlockAt(randx, randy, randz);
				if (blo.getType() == Material.STONE || blo.getType() == Material.DIORITE
						|| blo.getType() == Material.ANDESITE || blo.getType() == Material.GRANITE
						|| blo.getType() == Material.NETHERRACK || blo.getType() == Material.END_STONE) {
					Skulls.get().setHead(blo, ore.getValue());
					time.put(bl, time.get(bl) - 1);
				}
				tries.put(bl, tries.get(bl) - 1);
			}
		}, 0L, 2L));
	}

	public static void stopTimer(String bl) {
		Bukkit.getScheduler().cancelTask(taskID.get(bl));
		time.remove(bl);
		tries.remove(bl);
	}
}