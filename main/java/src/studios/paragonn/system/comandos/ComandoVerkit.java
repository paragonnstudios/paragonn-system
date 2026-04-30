package studios.paragonn.system.comandos;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import studios.paragonn.system.configuracoes.Mensagens;
import studios.paragonn.system.configuracoes.Settings;
import studios.paragonn.system.entidades.Kit;
import studios.paragonn.system.entidades.Kits;
import studios.paragonn.system.utils.GuiHolder;

public class ComandoVerkit implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {

		// Verificando se o player digitou o número de argumentos correto
		if (args.length != 1) {
			s.sendMessage(Mensagens.VerKit_Comando_Incorreto);
			return true;
		}

		// Pegando o argumento e verificando se o kit existe
		String nome = args[0].toLowerCase();
		if (!Kits.contains(nome)) {
			s.sendMessage(Mensagens.Kit_Nao_Existe.replace("%kit-id%", nome));
			if (Settings.Listar_Kits_Caso_Nao_Exista) {
				if (!s.hasPermission("system.kit.all") && !s.isOp()) {
					ComandoKits.ListKits(s);
				} else {
					ComandoKits.ListKitsForStaff(s);
				}	
			}
			return true;
		}

		// Verificando se o sender não é o console
		if (!(s instanceof Player)) {
			s.sendMessage(Mensagens.Console_Nao_Pode);
			return true;
		}
		
		// Pegando o kit e a config do Kit
		Kit kit = Kits.get(nome);

		// Pegando o player e abrindo um inventarios com os itens, o resto é feito pela classe KitsListener
		Player p = (Player) s;
		Inventory inv = Bukkit.createInventory(new GuiHolder(-990), 36, "Visualizando Kit");
		for (ItemStack item : kit.getItens()) {
			if (item != null) inv.addItem(item);
		}
		
		ItemStack voltar = new ItemStack(Material.ARROW);
		org.bukkit.inventory.meta.ItemMeta meta = voltar.getItemMeta();
		meta.setDisplayName("§cVoltar");
		voltar.setItemMeta(meta);
		
		int size = inv.getSize();
		int lastRowStart = size - 9;
		int middleSlot = lastRowStart + 4;
		if (inv.getItem(middleSlot) != null && inv.getItem(middleSlot).getType() != Material.AIR) {
			inv.setItem(middleSlot - 1, voltar);
		} else {
			inv.setItem(middleSlot, voltar);
		}
		
		p.openInventory(inv);
		return true;
	}
}