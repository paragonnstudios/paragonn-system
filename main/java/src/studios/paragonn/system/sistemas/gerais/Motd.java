package studios.paragonn.system.sistemas.gerais;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import studios.paragonn.system.configuracoes.Settings;

public class Motd implements Listener {

	@EventHandler
	public void definirMotd(ServerListPingEvent e) {
		if (Bukkit.hasWhitelist()) {
			e.setMotd(Settings.Motd_Manutencao);
		} else {
			e.setMotd(Settings.Motd_Normal);
		}
	}
	
}