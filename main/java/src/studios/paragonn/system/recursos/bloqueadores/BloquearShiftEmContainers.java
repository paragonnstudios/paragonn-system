package studios.paragonn.system.recursos.bloqueadores;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import studios.paragonn.system.configuracoes.Mensagens;
import studios.paragonn.system.configuracoes.Settings;
import studios.paragonn.system.enums.InventoryName;

public class BloquearShiftEmContainers implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void aoUsarShift(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		if (e.getClick().isShiftClick()) {
			if (Settings.Bloquear_Shift_Em_Containers_Containers.contains(e.getInventory().getType())) {
				if (!(e.getWhoClicked().hasPermission("system.bypass.shiftemcontainer"))) {
					String tipo = InventoryName.valueOf(e.getInventory()).getName();
					Player player = (Player) e.getWhoClicked();
					player.sendMessage(Mensagens.Shift_Bloqueado_No_Container.replace("%tipo%", tipo));
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
}