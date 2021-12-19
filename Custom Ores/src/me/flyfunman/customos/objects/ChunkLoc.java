package me.flyfunman.customos.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import me.flyfunman.customos.utils.CustomConfig;

public class ChunkLoc {
	public static List<ChunkLoc> chunk = new ArrayList<>();

	private World world;
	private int x;
	private int z;

	public ChunkLoc() {

	}

	public ChunkLoc(Location loc) {
		world = loc.getWorld();
		x = loc.getBlockX();
		z = loc.getBlockZ();

		chunk.add(this);
	}

	public ChunkLoc(World w, int xVal, int zVal) {
		world = w;
		x = xVal;
		z = zVal;

		chunk.add(this);
	}

	public static boolean isInConfig(ChunkLoc loc) {
		if (!CustomConfig.storage().contains(loc.getPath()))
			return false;
		if (CustomConfig.storage().getIntegerList(loc.getPath()).contains(loc.getZ()))
			return true;

		return false;
	}

	public static boolean isInConfig(Location loc) {
		String path = "Chunks." + loc.getWorld().getName() + "." + loc.getBlockX();
		if (!CustomConfig.storage().contains(path))
			return false;
		if (CustomConfig.storage().getIntegerList(path).contains(loc.getBlockZ()))
			return true;

		return false;
	}

	public static boolean isChunkLoc(Location l) {
		for (ChunkLoc c : chunk) {
			if (l.getWorld().equals(c.getWorld()) && l.getBlockX() == c.getX() && l.getBlockZ() == c.getZ())
				return true;

		}

		return false;
	}

	public Location getLoc() {
		return new Location(world, x, 100, z);
	}

	public World getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public List<ChunkLoc> getChunks() {
		return chunk;
	}

	public String getPath() {
		return "Chunks." + world.getName() + "." + x;
	}

	public void addToConfig() {
		List<Integer> list;

		if (CustomConfig.storage().contains(getPath()))
			list = CustomConfig.storage().getIntegerList(getPath());
		else
			list = new ArrayList<>();

		list.add(z);

		CustomConfig.storage().set(getPath(), list);
	}

	public void remove(Location l) {
		ChunkLoc cl = null;
		for (ChunkLoc c : chunk) {
			if (l.getWorld().equals(c.getWorld()) && l.getX() == c.getX() && l.getZ() == c.getZ()) {
				cl = c;
				break;
			}
		}
		if (cl != null)
			chunk.remove(cl);
	}
}
