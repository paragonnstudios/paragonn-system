package studios.paragonn.system.addons.corechat;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.database.PlayerStat;

import studios.paragonn.system.Main;
import studios.paragonn.system.configuracoes.Settings;

public class McTopTag implements Listener {

	public static BukkitTask TTask;

	private static String playerTopOne = "";

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void aoEnviarMensagem(AsyncPlayerChatEvent e) {
		if (e.getPlayer().getName().equals(playerTopOne)) {
			e.setFormat(Settings.mcTopTag_Tag + e.getFormat());
		}
	}

	public static void checkMCTop() {
		TTask = new BukkitRunnable() {
			@Override
			public void run() {
				List<PlayerStat> tops = DatabaseManagerFactory.getDatabaseManager().readLeaderboard(null, 1, 1);
				if (!tops.isEmpty()) {
					playerTopOne = tops.get(0).name;
				}
			}
		}.runTaskTimerAsynchronously(Main.get(), 60L, Settings.mcTopTag_Tempo_De_Checagem * 20L);
	}
}
