package studios.paragonn.system.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import studios.paragonn.system.apis.OnlinePlayersAPI;
import studios.paragonn.system.configuracoes.Mensagens;

public class ComandoOnline implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
		s.sendMessage(Mensagens.Players_Online.replace("%online%", String.valueOf(OnlinePlayersAPI.getOnlinePlayers().length)));
		return true;
	}
}