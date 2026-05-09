package studios.paragonn.system.updater;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import studios.paragonn.system.Main;

/**
 * Dispara a checagem de updates no GitHub na primeira entrada de um jogador com {@link SystemUpdater#UPDATER_PERMISSION}.
 */
public class UpdaterJoinListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission(SystemUpdater.UPDATER_PERMISSION)) {
			Main.get().scheduleUpdaterWhenFirstAdminJoins();
		}
	}
}
