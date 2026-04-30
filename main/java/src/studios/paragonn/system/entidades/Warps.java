package studios.paragonn.system.entidades;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;

import studios.paragonn.system.utils.manager.DataManager;

public abstract class Warps {

	private static HashMap<String, Warp> WARPS = new HashMap<>();
	
	public static Warp get(String warp) {
		return WARPS.get(warp);
	}
	
	public static Collection<Warp> getAll() {
		return WARPS.values();
	}
	
	public static void create(String nome, Warp warp) {
		WARPS.put(nome, warp);
	}
	
	public static void delete(String nome) {
		File file = DataManager.getFile(nome, "warps");
		DataManager.deleteFile(file);
		WARPS.remove(nome);
	}
	
	public static boolean contains(String nome) {
		return WARPS.containsKey(nome);
	}
	
	public static void loadWarps() {
		WARPS.clear();
		File folder = DataManager.getFolder("warps");
		File[] file = folder.listFiles();
		if(file != null) {
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null && file[i].isFile())  {
				FileConfiguration configWarp = DataManager.getConfiguration(file[i]);
				String nome = file[i].getName().replace(".yml", "");
				
				boolean updated = false;
				if (!configWarp.contains("EnviarTitle")) {
					configWarp.set("EnviarTitle", false);
					updated = true;
				}
				if (!configWarp.contains("EnviarSubTitle")) {
					configWarp.set("EnviarSubTitle", false);
					updated = true;
				}
				if (!configWarp.contains("EnviarMensagemPlayerTeleportado")) {
					configWarp.set("EnviarMensagemPlayerTeleportado", false);
					updated = true;
				}
				if (updated) {
					try {
						configWarp.save(file[i]);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				String loc = configWarp.getString("Localizacao");
				String perm = configWarp.getString("Permissao", "");
				String semPerm = configWarp.getString("MensagemSemPermissao", "");
				int delay = configWarp.getInt("Delay");
				boolean delayVip = configWarp.getBoolean("DelayParaVips");
				boolean mensagem = configWarp.getBoolean("EnviarMensagem", true);
				String inicio = configWarp.getString("MensagemInicio", "");
				String fim = configWarp.getString("MensagemFinal", "");
				boolean enviar = configWarp.getBoolean("EnviarTitle");
				boolean enviarSub = configWarp.getBoolean("EnviarSubTitle");
				boolean enviarMsgTel = configWarp.getBoolean("EnviarMensagemPlayerTeleportado");
				String title = configWarp.getString("Title", "");
				String subTitle = configWarp.getString("SubTitle", "");
				String teleportado = configWarp.getString("MensagemPlayerTeleportado", "");
				String teleportadoStaff = configWarp.getString("MensagemPlayerTeleportadoStaff", "");
				Warp warp = new Warp(nome, loc, perm, semPerm, delay, delayVip, mensagem, inicio, fim, enviar, title, enviarSub, subTitle, enviarMsgTel, teleportado, teleportadoStaff);
				WARPS.put(nome, warp);
				}
			}
		}
	}
}
