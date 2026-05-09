package studios.paragonn.system.addons;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import studios.paragonn.system.configuracoes.Settings;

/**
 * Integra destaque e cores automáticas no chat processado pelo Paragonn Core
 * (antes do {@code AsyncPlayerChatEvent} em MONITOR no core).
 */
public class ParagonnCoreChat implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void aoEnviarMensagem(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();

		if (p.hasPermission("system.chat.destaque")) {
			e.setFormat(" §l§f §a \n" + e.getFormat() + "\n §l§f §a ");
		}

		String colorId = getChatColorIdByPerm(p);
		if (colorId != null && Settings.CorAutomatica != null && Settings.CorAutomatica.containsKey(colorId)) {
			String color = Settings.CorAutomatica.get(colorId);
			e.setMessage(color + e.getMessage());
		}
	}

	private String getChatColorIdByPerm(Player p) {
		if (Settings.CorAutomatica == null) {
			return null;
		}
		for (String perm : Settings.CorAutomatica.keySet()) {
			if (p.hasPermission("system.chat.cor." + perm)) {
				return perm;
			}
		}
		for (PermissionAttachmentInfo perm : p.getEffectivePermissions()) {
			if (perm.getPermission().startsWith("system.chat.cor.")) {
				return perm.getPermission().replace("system.chat.cor.", "");
			}
		}
		return null;
	}
}
