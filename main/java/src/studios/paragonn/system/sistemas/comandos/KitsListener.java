package studios.paragonn.system.sistemas.comandos;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import studios.paragonn.system.Main;
import studios.paragonn.system.configuracoes.Mensagens;
import studios.paragonn.system.entidades.Kit;
import studios.paragonn.system.entidades.Kits;
import studios.paragonn.system.utils.GuiHolder;
import studios.paragonn.system.utils.manager.DataManager;
import studios.paragonn.system.utils.serializer.Serializer;
import studios.paragonn.system.utils.serializer.SerializerNEW;
import studios.paragonn.system.utils.serializer.SerializerVeryNEW;
import studios.paragonn.system.utils.serializer.SerializerOLD;

public class KitsListener implements Listener {

	@EventHandler
	public void InventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getHolder() instanceof GuiHolder) {
			
			Player p = (Player) e.getPlayer();
			GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
			int guiID = holder.getId();
			
			if (guiID == 997) {
				String kit = (String) holder.getProperty("kit");
				if (p.hasPermission("system.criarkit")) {
					createKit(e.getInventory(), p, kit);
					return;
				}
			}
		
			else if (guiID == 995) {
				String kit = (String) holder.getProperty("kit");
				if (p.hasPermission("system.editarkit")) {
					editKit(e.getInventory(), p, kit);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void InvetoryClick(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof GuiHolder) {
			int guiID = ((GuiHolder) e.getInventory().getHolder()).getId();
			if (guiID == -990) {
				e.setResult(Result.DENY);
				e.setCancelled(true);
				
				if (e.getCurrentItem() != null && e.getCurrentItem().getType() == org.bukkit.Material.ARROW) {
					if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("§cVoltar")) {
						Player p = (Player) e.getWhoClicked();
						p.closeInventory();
						String menuName = studios.paragonn.system.configuracoes.Settings.Comando_Do_Botao_Voltar;
						String menuFileName = menuName.endsWith(".yml") ? menuName : menuName + ".yml";
						
						// 1. Tenta via API Moderna (v4+)
						try {
							Class<?> ccAPI = Class.forName("me.filoghost.chestcommands.api.ChestCommandsAPI");
							java.lang.reflect.Method openMenu = ccAPI.getMethod("openMenu", Player.class, String.class);
							try {
								openMenu.invoke(null, p, menuFileName);
								return;
							} catch (Throwable ignored_) {
								openMenu.invoke(null, p, menuName);
								return;
							}
						} catch (Throwable ignored) {}
						
						// 2. Tenta via API Antiga (v4+)
						try {
							Class<?> ccAPI = Class.forName("com.gmail.filoghost.chestcommands.api.ChestCommandsAPI");
							java.lang.reflect.Method openMenu = ccAPI.getMethod("openMenu", Player.class, String.class);
							try {
								openMenu.invoke(null, p, menuFileName);
								return;
							} catch (Throwable ignored_) {
								openMenu.invoke(null, p, menuName);
								return;
							}
						} catch (Throwable ignored) {}
						
						// 3. Tenta via Reflexão Interna (v3.x)
						try {
							Class<?> ccClass = Class.forName("com.gmail.filoghost.chestcommands.ChestCommands");
							Object ccInstance = ccClass.getMethod("getInstance").invoke(null);
							java.lang.reflect.Method getMap = ccClass.getMethod("getFileNameToMenuMap");
							java.util.Map<?, ?> menuMap = (java.util.Map<?, ?>) getMap.invoke(ccInstance);
							Object menu = menuMap.get(menuFileName);
							if (menu == null) menu = menuMap.get(menuName);
							if (menu != null) {
								java.lang.reflect.Method open = menu.getClass().getMethod("open", Player.class);
								open.invoke(menu, p);
								return;
							}
						} catch (Throwable ignored) {}

						// 4. Fallback para comando configurado
						p.performCommand(menuName);
					}
				}
			}
		}
	}

	// Método para criar o kit
	private void createKit(Inventory inv, Player p, String id) {
		String permissao = "system.kit." + id;
		String itens = serializeItens(inv.getContents());
		Kit kit = new Kit(id, permissao, "§rKit '" + id + "' sem nome! Use /editarkit!", 5, Mensagens.Kit_Sem_Permissao, itens);
		File file = DataManager.getFile(id, "kits");
		FileConfiguration config = DataManager.getConfiguration(file);
		DataManager.createFile(file);
		config.set("Permissao", permissao);
		config.set("Nome", "§rKit '" + id + "' sem nome! Use /editarkit!");
		config.set("Delay", 5);
		config.set("MensagemDeErro", Mensagens.Kit_Sem_Permissao);
		config.set("Itens", itens);
		try {
			Kits.create(id, kit);
			config.save(file);
			p.sendMessage(Mensagens.Kit_Criado.replace("%kit-id%", id));
		} catch (IOException ex) {
			Bukkit.getConsoleSender().sendMessage(Mensagens.Falha_Ao_Salvar.replace("%arquivo%", file.getName()));
		}
	}
	
	// Método para ediar o kit
	private void editKit(Inventory inv, Player p, String id) {
		String itens = serializeItens(inv.getContents());
		Kit kit = Kits.get(id);
		File file = DataManager.getFile(id, "kits");
		FileConfiguration config = DataManager.getConfiguration(file);
		kit.setItens(inv.getContents());
		config.set("Itens", itens);
		try {
			config.save(file);
			p.sendMessage(Mensagens.Kit_Editado.replace("%kit-id%", id).replace("%kit-nome%", kit.getNome()));
		} catch (IOException ex) {
			Bukkit.getConsoleSender().sendMessage(Mensagens.Falha_Ao_Salvar.replace("%arquivo%", file.getName()));
		}
	}
	
	// Método para serializar os itens de acordo com a versão
	private String serializeItens(ItemStack[] itens) {
		if (Main.isOldVersion()) 
		{
			return SerializerOLD.serializeListItemStack(itens);
		}
		else if (Main.isMotherFuckerVersion()) 
		{
			return SerializerVeryNEW.serializeListItemStack(itens);
		}
		else if (Main.isNewVersion()) 
		{
			return SerializerNEW.serializeListItemStack(itens);
		}
		else 
		{
			return Serializer.serializeListItemStack(itens);
		}
	}
	
}