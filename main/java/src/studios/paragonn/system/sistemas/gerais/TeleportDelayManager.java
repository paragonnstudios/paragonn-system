package studios.paragonn.system.sistemas.gerais;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import studios.paragonn.system.Main;
import studios.paragonn.system.configuracoes.Mensagens;

public class TeleportDelayManager implements Listener {

	private static final Map<UUID, Location> ORIGEM = new HashMap<>();
	private static final Map<UUID, BukkitTask> TAREFAS = new HashMap<>();

	// Agenda um teleporte para daqui a 'delaySegundos', cancelando caso o player se mexa antes disso
	public static void iniciar(Player p, long delaySegundos, Runnable aoTeleportar) {
		final UUID uuid = p.getUniqueId();
		ORIGEM.put(uuid, p.getLocation());
		BukkitTask tarefa = new BukkitRunnable() {
			@Override
			public void run() {
				ORIGEM.remove(uuid);
				TAREFAS.remove(uuid);
				aoTeleportar.run();
			}
		}.runTaskLater(Main.get(), 20L * delaySegundos);
		TAREFAS.put(uuid, tarefa);
	}

	private static void cancelar(UUID uuid) {
		BukkitTask tarefa = TAREFAS.remove(uuid);
		if (tarefa != null) {
			tarefa.cancel();
		}
		ORIGEM.remove(uuid);
	}

	@EventHandler
	public void aoSair(PlayerQuitEvent e) {
		cancelar(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void aoMover(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		Location origem = ORIGEM.get(uuid);
		if (origem == null) {
			return;
		}

		// Ignorando eventos que só mudam a direção que o player esta olhando
		Location destino = e.getTo();
		if (destino == null || (origem.getBlockX() == destino.getBlockX()
				&& origem.getBlockY() == destino.getBlockY()
				&& origem.getBlockZ() == destino.getBlockZ())) {
			return;
		}

		cancelar(uuid);
		p.sendMessage(Mensagens.Teleporte_Cancelado_Movimento);
	}

}
