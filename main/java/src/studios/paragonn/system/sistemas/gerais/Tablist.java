package studios.paragonn.system.sistemas.gerais;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import studios.paragonn.system.apis.TablistAPI;
import studios.paragonn.system.configuracoes.Settings;

public class Tablist implements Listener {
	
	@EventHandler
   	public void aoEntrarTablist(PlayerJoinEvent e) {
   		TablistAPI.sendTabList(e.getPlayer(), Settings.Header, Settings.Footer);	   
   	}
	
}