package studios.paragonn.system.addons.corechat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import studios.paragonn.system.Main;
import studios.paragonn.system.addons.Vault;
import studios.paragonn.system.configuracoes.Settings;

public class MagnataTag implements Listener {

	public static BukkitTask MTask;

	private static String playerMagnata = "";
	private static double balanceMagnata = 0;

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void aoEnviarMensagem(AsyncPlayerChatEvent e) {
		if (e.getPlayer().getName().equals(playerMagnata)) {
			e.setFormat(Settings.magnataTag_Tag + e.getFormat());
		}
	}

	public static void checkMagnata() {
		MTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (OfflinePlayer off : Bukkit.getOfflinePlayers()) {
					double balance = Vault.getBalance(off);
					if (balance > balanceMagnata) {
						playerMagnata = off.getName();
						balanceMagnata = balance;
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.get(), 60L, Settings.magnataTag_Tempo_De_Checagem * 20L);
	}
}
