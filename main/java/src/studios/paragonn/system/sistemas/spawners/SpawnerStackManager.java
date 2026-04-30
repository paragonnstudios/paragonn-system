package studios.paragonn.system.sistemas.spawners;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import studios.paragonn.system.configuracoes.Settings;
import studios.paragonn.system.enums.EntityName;
import studios.paragonn.system.utils.Utils;
import studios.paragonn.system.utils.manager.DataManager;

public class SpawnerStackManager implements Listener {

	private static Map<Location, Integer> SPAWNERS = new HashMap<>();
	private static Map<Location, ArmorStand> HOLOGRAMS = new HashMap<>();

	private static Location getBlockLocation(Location loc) {
		if (loc == null) return null;
		return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public static void load() {
		SPAWNERS.clear();
		File file = DataManager.getFile("spawners", "data");
		if (file.exists()) {
			FileConfiguration config = DataManager.getConfiguration(file);
			for (String key : config.getKeys(false)) {
				Location loc = Utils.deserializeLocation(config.getString(key + ".Loc"));
				int amount = config.getInt(key + ".Amount");
				SPAWNERS.put(getBlockLocation(loc), amount);
			}
		}
	}

	public static void save() {
		File file = DataManager.getFile("spawners", "data");
		FileConfiguration config = DataManager.getConfiguration(file);
		for (String key : config.getKeys(false)) config.set(key, null);
		
		int i = 0;
		for (Map.Entry<Location, Integer> entry : SPAWNERS.entrySet()) {
			config.set("spawner-" + i + ".Loc", Utils.serializeLocation(entry.getKey()));
			config.set("spawner-" + i + ".Amount", entry.getValue());
			i++;
		}
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getAmount(Location loc) {
		return SPAWNERS.getOrDefault(getBlockLocation(loc), 1);
	}

	public static void setAmount(Location loc, int amount, String typeName) {
		SPAWNERS.put(getBlockLocation(loc), amount);
		updateHologram(loc, amount, typeName);
		updateSpawnerBlock(loc, amount);
		save();
	}

	public static void remove(Location loc) {
		SPAWNERS.remove(getBlockLocation(loc));
		removeHologram(loc);
		save();
	}
	
	private static void updateSpawnerBlock(Location loc, int amount) {
		Block b = loc.getBlock();
		if (b.getType() == Material.MOB_SPAWNER) {
			try {
				CreatureSpawner cs = (CreatureSpawner) b.getState();
				
				// Aumenta a velocidade e quantidade de forma moderada
				double boost = 1.0 + (amount - 1) * 0.02; 
				int minDelay = (int) (200 / boost);
				int maxDelay = (int) (800 / boost);
				int spawnCount = (int) (4 * (1.0 + (amount - 1) * 0.01));
				
				if (minDelay < 40) minDelay = 40; 
				if (maxDelay < 100) maxDelay = 100;
				if (spawnCount > 24) spawnCount = 24;

				String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
				Class<?> craftCreatureSpawnerClass = Class.forName("org.bukkit.craftbukkit." + version + ".block.CraftCreatureSpawner");
				Object craftCreatureSpawner = craftCreatureSpawnerClass.cast(cs);
				
				java.lang.reflect.Field tileEntityField = craftCreatureSpawnerClass.getDeclaredField("spawner");
				tileEntityField.setAccessible(true);
				Object tileEntityMobSpawner = tileEntityField.get(craftCreatureSpawner);
				
				java.lang.reflect.Method getSpawnerMethod = tileEntityMobSpawner.getClass().getMethod("getSpawner");
				Object mobSpawnerAbstract = getSpawnerMethod.invoke(tileEntityMobSpawner);
				
				Class<?> msaClass = mobSpawnerAbstract.getClass();
				// No 1.8.8 os campos geralmente são esses nomes mesmos
				setField(msaClass, mobSpawnerAbstract, "minSpawnDelay", minDelay);
				setField(msaClass, mobSpawnerAbstract, "maxSpawnDelay", maxDelay);
				setField(msaClass, mobSpawnerAbstract, "spawnCount", spawnCount);
				
				cs.update();
			} catch (Throwable ignored) {}
		}
	}
	
	private static void setField(Class<?> clazz, Object obj, String fieldName, Object value) {
		try {
			java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (NoSuchFieldException e) {
			// Tenta na superclasse se necessário (MobSpawnerAbstract)
			try {
				java.lang.reflect.Field field = clazz.getSuperclass().getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(obj, value);
			} catch (Throwable ignored) {}
		} catch (Throwable ignored) {}
	}

	public static Map<Location, Integer> getSpawners() {
		return SPAWNERS;
	}

	public static void updateHologram(Location loc, int amount, String typeName) {
		removeHologram(loc);
		
		Location holoLoc = loc.clone().add(0.5, 1.2, 0.5);
		for (Entity entity : loc.getWorld().getNearbyEntities(holoLoc, 1.0, 1.0, 1.0)) {
			if (entity instanceof ArmorStand) {
				ArmorStand as = (ArmorStand) entity;
				if (as.getCustomName() != null && (as.getCustomName().contains("x ") || as.getCustomName().contains("Spawner"))) {
					as.remove();
				}
			}
		}

		if (amount > 1) {
			ArmorStand as = loc.getWorld().spawn(holoLoc, ArmorStand.class);
			as.setVisible(false);
			as.setGravity(false);
			as.setMarker(true);
			as.setCustomName(Settings.Nome_Dos_Mobs.replace("%tipo%", typeName).replace("%quantia%", String.valueOf(amount)));
			as.setCustomNameVisible(true);
			HOLOGRAMS.put(loc, as);
		}
	}

	public static void removeHologram(Location loc) {
		ArmorStand as = HOLOGRAMS.remove(loc);
		if (as != null && !as.isDead()) {
			as.remove();
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		for (Map.Entry<Location, Integer> entry : SPAWNERS.entrySet()) {
			Location loc = entry.getKey();
			if (loc.getWorld() != null && loc.getWorld().equals(e.getWorld()) && e.getChunk().getX() == loc.getBlockX() >> 4 && e.getChunk().getZ() == loc.getBlockZ() >> 4) {
				Block b = loc.getBlock();
				if (b.getType() == Material.MOB_SPAWNER) {
					CreatureSpawner cs = (CreatureSpawner) b.getState();
					String type = cs.getSpawnedType().name();
					String typeName = EntityName.valueOf(type).getName();
					updateHologram(loc, entry.getValue(), typeName);
					updateSpawnerBlock(loc, entry.getValue());
				}
			}
		}
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		for (Map.Entry<Location, Integer> entry : SPAWNERS.entrySet()) {
			Location loc = entry.getKey();
			if (loc.getWorld() != null && loc.getWorld().equals(e.getWorld()) && e.getChunk().getX() == loc.getBlockX() >> 4 && e.getChunk().getZ() == loc.getBlockZ() >> 4) {
				removeHologram(loc);
			}
		}
	}
}
