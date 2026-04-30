package studios.paragonn.system.sistemas.spawners;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import studios.paragonn.system.apis.ItemAPI;
import studios.paragonn.system.configuracoes.Mensagens;
import studios.paragonn.system.configuracoes.Settings;
import studios.paragonn.system.enums.EntityName;

public class SistemaDeSpawners implements Listener {

	private static double RANGE = Settings.Raio_De_Distancia;
	private static int MAX_STACK = Settings.Limite_De_Mobs_Agrupados;

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void aoQuebrarSpawner(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (b.getType() == Material.MOB_SPAWNER) {
			if (p.getItemInHand().getType().name().contains("PICKAXE")) {
				if (p.getItemInHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
					Location loc = b.getLocation();
					int amount = SpawnerStackManager.getAmount(loc);
					SpawnerStackManager.remove(loc);
					
					CreatureSpawner mobSpawner = (CreatureSpawner) b.getState();
					String type = mobSpawner.getSpawnedType().name();
					ItemStack spawner = MobSpawner.get(type, amount);
					
					e.setExpToDrop(0);
					e.setCancelled(true);
					b.setType(Material.AIR);
					
					for (ItemStack is : p.getInventory().addItem(spawner).values()) {
						b.getWorld().dropItem(loc, is);
						p.sendMessage(Mensagens.Inventario_Cheio_Quebrou);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void aoColocarSpawner(BlockPlaceEvent e) {
		Block b = e.getBlock();
		if (b.getType() == Material.MOB_SPAWNER) {
			try {
				Player p = e.getPlayer();
				CreatureSpawner mobSpawner = (CreatureSpawner) b.getState();
				
				EntityType spawnedType = mobSpawner.getSpawnedType();
				if (spawnedType == null || spawnedType.name().equals("PIG")) {
					try {
						String type = ItemAPI.getInfo(e.getItemInHand(), "Entity");
						if (type != null && !type.isEmpty()) spawnedType = EntityType.valueOf(type);
					} catch (Throwable ignored) {}
				}
				if (spawnedType == null) {
					try {
						short type = e.getItemInHand().getDurability();
						EntityType et = EntityType.fromId(type);
						if (et != null) spawnedType = et;
					} catch (Throwable ignored) {}
				}
				if (spawnedType == null) spawnedType = EntityType.PIG;

				mobSpawner.setSpawnedType(spawnedType);
				mobSpawner.update(true);

				Location placeLoc = b.getLocation();
				String typeString = spawnedType.name();
				String typeName = typeString;
				try {
					typeName = EntityName.valueOf(typeString).getName();
				} catch (Throwable ignored) {}

				for (Map.Entry<Location, Integer> entry : SpawnerStackManager.getSpawners().entrySet()) {
					Location loc = entry.getKey();
					if (loc.getWorld() != null && loc.getWorld().equals(placeLoc.getWorld()) && loc.distance(placeLoc) <= RANGE) {
						Block blockAtLoc = loc.getBlock();
						if (blockAtLoc.getType() == Material.MOB_SPAWNER) {
							CreatureSpawner cs = (CreatureSpawner) blockAtLoc.getState();
							if (cs.getSpawnedType() == spawnedType) {
								int toAdd = p.isSneaking() ? e.getItemInHand().getAmount() : 1;
								int currentAmount = entry.getValue();
								int amount = currentAmount + toAdd;
								
								if (amount > MAX_STACK) {
									toAdd = MAX_STACK - currentAmount;
									amount = MAX_STACK;
								}
								
								if (toAdd > 0) {
									e.setCancelled(true);
									SpawnerStackManager.setAmount(loc, amount, typeName);
									
									ItemStack hand = e.getItemInHand();
									if (hand.getAmount() > toAdd) {
										hand.setAmount(hand.getAmount() - toAdd);
									} else {
										p.setItemInHand(null);
									}
									p.updateInventory();
									p.sendMessage("§aVocê agrupou um Spawner de " + typeName + "! Total: " + amount + "x.");
									return;
								}
							}
						}
					}
				}

				int initialAmount = p.isSneaking() ? e.getItemInHand().getAmount() : 1;
				if (initialAmount > MAX_STACK) initialAmount = MAX_STACK;
				
				SpawnerStackManager.setAmount(placeLoc, initialAmount, typeName);
				
				if (initialAmount > 1) {
					ItemStack hand = e.getItemInHand();
					int toRemoveExtra = initialAmount - 1; // 1 already taken by Spigot natively because event not cancelled
					if (hand.getAmount() > toRemoveExtra) {
						hand.setAmount(hand.getAmount() - toRemoveExtra);
					} else {
						p.setItemInHand(null);
					}
					p.updateInventory();
					p.sendMessage("§aVocê colocou um Spawner de " + typeName + "! Total: " + initialAmount + "x.");
				}

			} catch (Throwable ignored) {}
		}
	}

	@org.bukkit.event.EventHandler
	public void onSpawnerSpawn(org.bukkit.event.entity.SpawnerSpawnEvent e) {
		studios.paragonn.system.utils.ReflectionUtils.freezeEntity(e.getEntity());
	}
	
}