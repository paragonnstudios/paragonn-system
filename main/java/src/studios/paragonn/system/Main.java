package studios.paragonn.system;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import studios.paragonn.system.addons.Mcmmo;
import studios.paragonn.system.addons.ParagonnCoreChat;
import studios.paragonn.system.addons.Vault;
import studios.paragonn.system.addons.corechat.MagnataTag;
import studios.paragonn.system.addons.corechat.McTopTag;
import studios.paragonn.system.apis.APIS;
import studios.paragonn.system.comandos.ComandoAlerta;
import studios.paragonn.system.comandos.ComandoAlertaOLD;
import studios.paragonn.system.comandos.ComandoBack;
import studios.paragonn.system.comandos.ComandoBigorna;
import studios.paragonn.system.comandos.ComandoChapeu;
import studios.paragonn.system.comandos.ComandoClear;
import studios.paragonn.system.comandos.ComandoClearChat;
import studios.paragonn.system.comandos.ComandoCompactar;
import studios.paragonn.system.comandos.ComandoCompactarOLD;
import studios.paragonn.system.comandos.ComandoCores;
import studios.paragonn.system.comandos.ComandoCraft;
import studios.paragonn.system.comandos.ComandoCrashar;
import studios.paragonn.system.comandos.ComandoCriarkit;
import studios.paragonn.system.comandos.ComandoDarkit;
import studios.paragonn.system.comandos.ComandoDelhome;
import studios.paragonn.system.comandos.ComandoDelkit;
import studios.paragonn.system.comandos.ComandoDelwarp;
import studios.paragonn.system.comandos.ComandoDerreter;
import studios.paragonn.system.comandos.ComandoDivulgar;
import studios.paragonn.system.comandos.ComandoEchest;
import studios.paragonn.system.comandos.ComandoEditaritem;
import studios.paragonn.system.comandos.ComandoEditaritemOLD;
import studios.paragonn.system.comandos.ComandoEditarkit;
import studios.paragonn.system.comandos.ComandoEditarplaca;
import studios.paragonn.system.comandos.ComandoEnchant;
import studios.paragonn.system.comandos.ComandoEstatisticas;
import studios.paragonn.system.comandos.ComandoExecutarSom;
import studios.paragonn.system.comandos.ComandoFeed;
import studios.paragonn.system.comandos.ComandoFly;
import studios.paragonn.system.comandos.ComandoGamemode;
import studios.paragonn.system.comandos.ComandoGod;
import studios.paragonn.system.comandos.ComandoHeal;
import studios.paragonn.system.comandos.ComandoHome;
import studios.paragonn.system.comandos.ComandoHomes;
import studios.paragonn.system.comandos.ComandoInvsee;
import studios.paragonn.system.comandos.ComandoKit;
import studios.paragonn.system.comandos.ComandoKits;
import studios.paragonn.system.comandos.ComandoLixo;
import studios.paragonn.system.comandos.ComandoLuz;
import studios.paragonn.system.comandos.ComandoMundoVip;
import studios.paragonn.system.comandos.ComandoOnline;
import studios.paragonn.system.comandos.ComandoParticular;
import studios.paragonn.system.comandos.ComandoPing;
import studios.paragonn.system.comandos.ComandoPotion;
import studios.paragonn.system.comandos.ComandoPublica;
import studios.paragonn.system.comandos.ComandoRenderizacao;
import studios.paragonn.system.comandos.ComandoReparar;
import studios.paragonn.system.comandos.ComandoSGive;
import studios.paragonn.system.comandos.ComandoSethome;
import studios.paragonn.system.comandos.ComandoSetmundovip;
import studios.paragonn.system.comandos.ComandoSetspawn;
import studios.paragonn.system.comandos.ComandoSetwarp;
import studios.paragonn.system.comandos.ComandoSkull;
import studios.paragonn.system.comandos.ComandoSlime;
import studios.paragonn.system.comandos.ComandoSpawn;
import studios.paragonn.system.comandos.ComandoSpeed;
import studios.paragonn.system.comandos.ComandoSudo;
import studios.paragonn.system.comandos.ComandoSystem;
import studios.paragonn.system.comandos.ComandoTitle;
import studios.paragonn.system.comandos.ComandoTp;
import studios.paragonn.system.comandos.ComandoTpa;
import studios.paragonn.system.comandos.ComandoTpaccept;
import studios.paragonn.system.comandos.ComandoTpall;
import studios.paragonn.system.comandos.ComandoTpcancel;
import studios.paragonn.system.comandos.ComandoTpdeny;
import studios.paragonn.system.comandos.ComandoTphere;
import studios.paragonn.system.comandos.ComandoTptoggle;
import studios.paragonn.system.comandos.ComandoVanish;
import studios.paragonn.system.comandos.ComandoVerinfo;
import studios.paragonn.system.comandos.ComandoVerkit;
import studios.paragonn.system.comandos.ComandoWarp;
import studios.paragonn.system.comandos.ComandoWarpOLD;
import studios.paragonn.system.comandos.ComandoWarps;
import studios.paragonn.system.configuracoes.Locations;
import studios.paragonn.system.configuracoes.Mensagens;
import studios.paragonn.system.configuracoes.Settings;
import studios.paragonn.system.entidades.Command;
import studios.paragonn.system.entidades.Kits;
import studios.paragonn.system.entidades.Warps;
import studios.paragonn.system.enums.JarType;
import studios.paragonn.system.enums.Version;
import studios.paragonn.system.recursos.bloqueadores.BloquearAbrirContainers;
import studios.paragonn.system.recursos.bloqueadores.BloquearCairNoVoid;
import studios.paragonn.system.recursos.bloqueadores.BloquearCama;
import studios.paragonn.system.recursos.bloqueadores.BloquearComandos;
import studios.paragonn.system.recursos.bloqueadores.BloquearCongelarAgua;
import studios.paragonn.system.recursos.bloqueadores.BloquearCrafts;
import studios.paragonn.system.recursos.bloqueadores.BloquearCriarPortal;
import studios.paragonn.system.recursos.bloqueadores.BloquearDerreterGeloENeve;
import studios.paragonn.system.recursos.bloqueadores.BloquearExplodirItens;
import studios.paragonn.system.recursos.bloqueadores.BloquearKickPorDuploLogin;
import studios.paragonn.system.recursos.bloqueadores.BloquearKickPorDuploLoginSuper;
import studios.paragonn.system.recursos.bloqueadores.BloquearMobsDePegaremFogoParaOSol;
import studios.paragonn.system.recursos.bloqueadores.BloquearMobsDePegaremItensDoChao;
import studios.paragonn.system.recursos.bloqueadores.BloquearNameTag;
import studios.paragonn.system.recursos.bloqueadores.BloquearNicksImproprios;
import studios.paragonn.system.recursos.bloqueadores.BloquearPassarDaBorda;
import studios.paragonn.system.recursos.bloqueadores.BloquearPlacas;
import studios.paragonn.system.recursos.bloqueadores.BloquearQuebrarPlantacoesPulando;
import studios.paragonn.system.recursos.bloqueadores.BloquearShiftEmContainers;
import studios.paragonn.system.recursos.bloqueadores.BloquearSubirEmVeiculos;
import studios.paragonn.system.recursos.bloqueadores.BloquearSubirNoTetoNether;
import studios.paragonn.system.recursos.bloqueadores.BloquearTeleportPorPortal;
import studios.paragonn.system.recursos.desativadores.DesativarChuva;
import studios.paragonn.system.recursos.desativadores.DesativarCicloDoDia;
import studios.paragonn.system.recursos.desativadores.DesativarDanoDoBlaze;
import studios.paragonn.system.recursos.desativadores.DesativarDanoDoCacto;
import studios.paragonn.system.recursos.desativadores.DesativarDanoDoEnderDragon;
import studios.paragonn.system.recursos.desativadores.DesativarDanoDoGhast;
import studios.paragonn.system.recursos.desativadores.DesativarDanoDoWither;
import studios.paragonn.system.recursos.desativadores.DesativarFlowDaAguaELava;
import studios.paragonn.system.recursos.desativadores.DesativarFomeNosMundos;
import studios.paragonn.system.recursos.desativadores.DesativarMensagemDeEntrada;
import studios.paragonn.system.recursos.desativadores.DesativarMensagemDeMorte;
import studios.paragonn.system.recursos.desativadores.DesativarMensagemDeSaida;
import studios.paragonn.system.recursos.desativadores.DesativarMobsNaturais;
import studios.paragonn.system.recursos.desativadores.DesativarMobsNaturaisOLD;
import studios.paragonn.system.recursos.desativadores.DesativarPropagacaoDoFogo;
import studios.paragonn.system.recursos.desativadores.DesativarQuedaDaAreia;
import studios.paragonn.system.recursos.desativadores.DesativarQuedaDaBigorna;
import studios.paragonn.system.recursos.desativadores.DesativarQuedaDasFolhas;
import studios.paragonn.system.recursos.gerais.BigornaInfinita;
import studios.paragonn.system.recursos.gerais.BloquearMoneyInvalido;
import studios.paragonn.system.recursos.gerais.ComandosPrimeiroLogin;
import studios.paragonn.system.recursos.gerais.CoresNaBigorna;
import studios.paragonn.system.recursos.gerais.CoresNaPlaca;
import studios.paragonn.system.recursos.gerais.EnderPearlCooldown;
import studios.paragonn.system.recursos.gerais.EntrarNoSpawnAoLogar;
import studios.paragonn.system.recursos.gerais.InvencibilidadeAoTeleportar;
import studios.paragonn.system.recursos.gerais.LimiteDePlayers;
import studios.paragonn.system.recursos.gerais.ManterXpAoMorrer;
import studios.paragonn.system.recursos.gerais.MensagemDeBoasVindas;
import studios.paragonn.system.recursos.gerais.Outros;
import studios.paragonn.system.recursos.gerais.TitleDeBoasVindas;
import studios.paragonn.system.sistemas.comandos.BackListener;
import studios.paragonn.system.sistemas.comandos.EnderChestListener;
import studios.paragonn.system.sistemas.comandos.FlyListener;
import studios.paragonn.system.sistemas.comandos.InvseeListener;
import studios.paragonn.system.sistemas.comandos.KitsListener;
import studios.paragonn.system.sistemas.comandos.VanishListener;
import studios.paragonn.system.sistemas.gerais.AnunciarMorte;
import studios.paragonn.system.sistemas.gerais.AutoAnuncio;
import studios.paragonn.system.sistemas.gerais.CooldownComandos;
import studios.paragonn.system.sistemas.gerais.DelayComandos;
import studios.paragonn.system.sistemas.gerais.DeletarComandos;
import studios.paragonn.system.sistemas.gerais.DroparCabecaAoMorrer;
import studios.paragonn.system.sistemas.gerais.Motd;
import studios.paragonn.system.sistemas.gerais.PlayerData;
import studios.paragonn.system.sistemas.gerais.ScoreBoard;
import studios.paragonn.system.sistemas.gerais.ScoreBoardOLD;
import studios.paragonn.system.sistemas.gerais.StackMobs;
import studios.paragonn.system.sistemas.gerais.Tablist;
import studios.paragonn.system.sistemas.spawners.BloquearTrocarTipoDoSpawnerComOvo;
import studios.paragonn.system.sistemas.spawners.DroparSpawnerAoExplodir;
import studios.paragonn.system.sistemas.spawners.DroparSpawnerAoExplodirOLD;
import studios.paragonn.system.sistemas.spawners.SistemaDeSpawners;
import studios.paragonn.system.sistemas.spawners.SistemaDeSpawnersOLD;
import studios.paragonn.system.sistemas.spawners.SpawnerStackManager;
import studios.paragonn.system.utils.ReflectionUtils;
import studios.paragonn.system.utils.manager.ConfigManager;
import studios.paragonn.system.utils.manager.DataManager;
import studios.paragonn.system.updater.SystemUpdater;
import studios.paragonn.system.updater.UpdaterJoinListener;

public class Main extends JavaPlugin {

	private static Main main;
	private static Version version;
	private static JarType jarType;
	public static boolean setupFactions;
	/** Uma vez true, a busca de update no GitHub já foi disparada nesta sessão. */
	private boolean updaterCheckStarted;

	@Override
	public void onEnable() {
		enablePlugin();
		gerarConfigs();
		carregarConfigs();
		registrarEventos();
		registrarComandos();
		cleanupUpdaterArtifactsAfterStartup();
	}

	@Override
	public void onDisable() {
		disablePlugin();
	}

	private void enablePlugin() {
		main = this;
		version = Version.getServerVersion();
		jarType = JarType.getJarType();
		SystemUpdater.resetGithubUpdateCheckLock();
		ReflectionUtils.loadUtils();
		APIS.load();
	}

	private void gerarConfigs() {
		DataManager.createFolder("kits");
		DataManager.createFolder("warps");
		DataManager.createFolder("playerdata");
		ConfigManager.createConfig("config");
		ConfigManager.createConfig("comandos");
		ConfigManager.createConfig("settings");
		ConfigManager.createConfig("mensagens");
		ConfigManager.createConfig("ajuda");
		ConfigManager.createConfig("locations");
	}

	private void carregarConfigs() {
		Kits.loadKits();
		Warps.loadWarps();
		Settings.loadSettings();
		Mensagens.loadMensagens();
		Locations.loadLocations();
		studios.paragonn.system.sistemas.spawners.SpawnerStackManager.load();
	}

	private void registrarComandos() {
		new Command("back", "system.back", new ComandoBack());
		new Command("chapeu", "system.chapeu", new ComandoChapeu());
		new Command("clear", "system.clear", new ComandoClear());
		new Command("clearchat", "system.clearchat", new ComandoClearChat());
		new Command("cores", "system.cores", new ComandoCores());
		new Command("craft", "system.craft", new ComandoCraft());
		new Command("delhome", "system.delhome", new ComandoDelhome());
		new Command("delwarp", "system.delwarp", new ComandoDelwarp());
		new Command("derreter", "system.derreter", new ComandoDerreter());
		new Command("divulgar", "system.divulgar", new ComandoDivulgar());
		new Command("echest", "system.echest", new ComandoEchest());
		new Command("editarplaca", "system.editarplaca", new ComandoEditarplaca());
		new Command("enchant", "system.enchant", new ComandoEnchant());
		new Command("executarsom", "system.executarsom", new ComandoExecutarSom());
		new Command("feed", "system.feed", new ComandoFeed());
		new Command("fly", "system.fly", new ComandoFly());
		new Command("gamemode", "system.gamemode", new ComandoGamemode());
		new Command("god", "system.god", new ComandoGod());
		new Command("heal", "system.heal", new ComandoHeal());
		new Command("home", "system.home", new ComandoHome());
		new Command("homes", "system.home", new ComandoHomes());
		new Command("invsee", "system.invsee", new ComandoInvsee());
		new Command("lixo", "system.lixo", new ComandoLixo());
		new Command("luz", "system.luz", new ComandoLuz());
		new Command("mundovip", "system.mundovip", new ComandoMundoVip());
		new Command("online", "system.online", new ComandoOnline());
		new Command("particular", "system.particular", new ComandoParticular());
		new Command("ping", "system.ping", new ComandoPing());
		new Command("potion", "system.potion", new ComandoPotion());
		new Command("publica", "system.publica", new ComandoPublica());
		new Command("reparar", "system.reparar", new ComandoReparar());
		new Command("sethome", "system.sethome", new ComandoSethome());
		new Command("setmundovip", "system.setmundovip", new ComandoSetmundovip());
		new Command("setspawn", "system.setspawn", new ComandoSetspawn());
		new Command("setwarp", "system.setwarp", new ComandoSetwarp());
		new Command("slime", "system.slime", new ComandoSlime());
		new Command("spawn", "system.spawn", new ComandoSpawn());
		new Command("speed", "system.speed", new ComandoSpeed());
		new Command("sudo", "system.sudo", new ComandoSudo());
		new Command("system", "system.system", new ComandoSystem());
		new Command("tp", "system.tp", new ComandoTp());
		new Command("tpa", "system.tpa", new ComandoTpa());
		new Command("tpaccept", "system.tpaccept", new ComandoTpaccept());
		new Command("tpall", "system.tpall", new ComandoTpall());
		new Command("tpcancel", "system.tpcancel", new ComandoTpcancel());
		new Command("tpdeny", "system.tpdeny", new ComandoTpdeny());
		new Command("tphere", "system.tphere", new ComandoTphere());
		new Command("tptoggle", "system.tptoggle", new ComandoTptoggle());
		new Command("vanish", "system.vanish", new ComandoVanish());
		new Command("verinfo", "system.verinfo", new ComandoVerinfo());
		new Command("warps", "system.warps", new ComandoWarps());
		
		if (version.value < 17) {	
			new Command("criarkit", "system.criarkit", new ComandoCriarkit());
			new Command("darkit", "system.darkit", new ComandoDarkit());
			new Command("delkit", "system.delkit", new ComandoDelkit());
			new Command("editarkit", "system.editarkit", new ComandoEditarkit());
			new Command("kit", "system.kit", new ComandoKit());
			new Command("kits", "system.kits", new ComandoKits());
			new Command("verkit", "system.verkit", new ComandoVerkit());
			new Command("crashar", "system.crashar", new ComandoCrashar());
		} else {
			notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Comandos /kit, /criarkit etc (nao disponivel nas versoes acima da 1.17)");
			notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Comando /crashar (nao disponivel nas versoes acima da 1.17)");
		}

		if (isOldVersion()) {
			new Command("alerta", "system.alerta", new ComandoAlertaOLD());
			new Command("compactar", "system.compactar", new ComandoCompactarOLD());
			new Command("editaritem", "system.editaritem", new ComandoEditaritemOLD());
			new Command("warp", "system.warp", new ComandoWarpOLD());
			notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Comando /skull (nao disponivel na versao 1.5, 1.6 e 1.7)");
			notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Comando /title (nao disponivel na versao 1.5, 1.6, 1.7)");
		} else {
			new Command("compactar", "system.compactar", new ComandoCompactar());
			new Command("editaritem", "system.editaritem", new ComandoEditaritem());
			new Command("skull", "system.skull", new ComandoSkull());
			new Command("warp", "system.warp", new ComandoWarp());
			new Command("alerta", "system.alerta", new ComandoAlerta());
			new Command("title", "system.title", new ComandoTitle());				
		}
		
		if (!isOldVersion() && !isVeryFuckingNewVersion()) {
			new Command("renderizacao", "system.renderizacao", new ComandoRenderizacao());
		} else {
			notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Comando /renderizacao (nao disponivel nas versoes 1.5, 1,6, 1.7, 1.14, 1.15, 1.16, 1.17 e nas versoes acima da 1.17)");
		}
		
		if (!isVeryOldVersion()) {
			new Command("estatisticas", "system.estatisticas", new ComandoEstatisticas());
		} else {
			notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Comando /estatisticas (nao disponivel nas versoes 1.5 e 1.6)");
		}
		
		if (!isVeryNewVersion()) {
			new Command("sgive", "system.sgive", new ComandoSGive());
		}
		
		if (!isVeryFuckingNewVersion()) {
			new Command("bigorna", "system.bigorna", new ComandoBigorna());
		} else {
			notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Comando /bigorna (nao disponivel nas versoes 1.14, 1.15, 1.16, 1.17 e nas versoes acima da 1.17)");
		}
		
	}

	private void registrarEventos() {
		getServer().getConsoleSender().sendMessage("§a[System] Versao identificada do servidor: " + jarType + " " + version.toString().replace("_", ".").replace("v", ""));

		if (!isRecomendedVersion()) {
			getServer().getConsoleSender().sendMessage("§c[System] Atencao! Voce esta utilizando uma versao do Minecraft que nao suporta todos os recursos do System!");
			getServer().getConsoleSender().sendMessage("§c[System] Lista dos recursos dos System que nao serao habilitados:");
		}
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		FileConfiguration commands = ConfigManager.getConfig("comandos");
				
		if (Settings.Anunciar_Morte) {
			pm.registerEvents(new AnunciarMorte(), this);
		}

		if (Settings.Ativar_Cores_Na_Bigorna) {
			pm.registerEvents(new CoresNaBigorna(), this);
		}

		if (Settings.Ativar_Cores_Na_Placa) {
			pm.registerEvents(new CoresNaPlaca(), this);
		}

		if (Settings.Auto_Anuncio) {
			if (Settings.Lista_De_Anuncios != null && Settings.Lista_De_Anuncios.size() > 0) {
				AutoAnuncio.runMensagens();	
			}
		}

		if (Settings.Bigorna_Infinita) {
			if (!isVeryFuckingNewVersion()) {
				pm.registerEvents(new BigornaInfinita(), this);								
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Bigorna Infinita (nao disponivel nas versoes 1.13, 1.14, 1.15, 1.16, 1.17 e nas versoes acima da 1.17)");
			}
		}

		if (Settings.Bloquear_Abrir_Containers_Ativar) {
			if (Settings.Bloquear_Abrir_Containers_Containers != null && Settings.Bloquear_Abrir_Containers_Containers.size() > 0) {
				pm.registerEvents(new BloquearAbrirContainers(), this);	
			}
		}

		if (Settings.Bloquear_Cair_No_Void) {
			pm.registerEvents(new BloquearCairNoVoid(), this);
		}

		if (Settings.Bloquear_Cama) {
			pm.registerEvents(new BloquearCama(), this);
		}

		if (Settings.Bloquear_Comandos) {
			if (Settings.Lista_Dos_Comandos_Bloqueados != null && Settings.Lista_Dos_Comandos_Bloqueados.size() > 0) {
				pm.registerEvents(new BloquearComandos(), this);
			}
		}

		if (Settings.Bloquear_Congelar_Agua) {
			pm.registerEvents(new BloquearCongelarAgua(), this);
		}

		if (Settings.Bloquear_Crafts) {
			if (Settings.Lista_Dos_Crafts_Bloqueados != null && Settings.Lista_Dos_Crafts_Bloqueados.size() > 0) {
				pm.registerEvents(new BloquearCrafts(), this);	
			}
		}

		if (Settings.Bloquear_Criar_Portal) {
			pm.registerEvents(new BloquearCriarPortal(), this);
		}

		if (Settings.Bloquear_Derreter_Gelo_E_Neve) {
			pm.registerEvents(new BloquearDerreterGeloENeve(), this);
		}
		
		if (Settings.Bloquear_Explodir_Itens) {
			pm.registerEvents(new BloquearExplodirItens(), this);
		}
		
		if (Settings.Bloquear_Kick_Por_Duplo_Login_Super) {
			pm.registerEvents(new BloquearKickPorDuploLoginSuper(), this);
		} else if (Settings.Bloquear_Kick_Por_Duplo_Login) {
			if (jarType == JarType.BUKKIT) {
				pm.registerEvents(new BloquearKickPorDuploLoginSuper(), this);
			} else {
				pm.registerEvents(new BloquearKickPorDuploLogin(), this);
			}
		}

		if (Settings.Bloquear_NameTag) {
			if (!isOldVersion()) {
				pm.registerEvents(new BloquearNameTag(), this);
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Bloquear NameTag (nao disponivel nas versoes 1.5, 1.6 e 1.7)");
			}
		}

		if (Settings.Bloquear_Nicks_Improprios) {
			if (Settings.Nicks_Bloqueados != null && Settings.Nicks_Bloqueados.size() > 0) {
				pm.registerEvents(new BloquearNicksImproprios(), this);				
			}
		}

		if (Settings.Bloquear_Mobs_De_Pegarem_Fogo_Para_O_Sol) {
			pm.registerEvents(new BloquearMobsDePegaremFogoParaOSol(), this);
		}
		
		if (Settings.Bloquear_Mobs_De_Pegarem_Itens_Do_Chao) {
			pm.registerEvents(new BloquearMobsDePegaremItensDoChao(), this);
		}
		
		if (Settings.Bloquear_Money_Invalido) {
			pm.registerEvents(new BloquearMoneyInvalido(), this);
		}

		if (Settings.Bloquear_Palavras_Em_Placas_Ativar) {
			pm.registerEvents(new BloquearPlacas(), this);
		}

		if (Settings.Bloquear_Passar_Da_Borda) {
			if (!isOldVersion()) {
				pm.registerEvents(new BloquearPassarDaBorda(), this);
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Bloquear Passar da Borda (nao disponivel nas versoes 1.5, 1.6 e 1.7)");
			}
		}

		if (Settings.Bloquear_Quebrar_Plantacoes_Pulando) {
			pm.registerEvents(new BloquearQuebrarPlantacoesPulando(), this);
		}
		
		if (Settings.Bloquear_Shift_Em_Containers_Ativar) {
			if (Settings.Bloquear_Shift_Em_Containers_Containers != null && Settings.Bloquear_Shift_Em_Containers_Containers.size() > 0) {
				pm.registerEvents(new BloquearShiftEmContainers(), this);	
			}
		}

		if (Settings.Bloquear_Subir_Em_Veiculos) {
			pm.registerEvents(new BloquearSubirEmVeiculos(), this);
		}

		if (Settings.Bloquear_Subir_No_Teto_Nether) {
			pm.registerEvents(new BloquearSubirNoTetoNether(), this);
		}

		if (Settings.Bloquear_Teleport_Por_Portal_Ativar) {
			pm.registerEvents(new BloquearTeleportPorPortal(), this);
		}
		
		if (Settings.Comandos_Com_Cooldown) {
			if (Settings.Lista_Dos_Comandos_Com_Cooldown != null && Settings.Lista_Dos_Comandos_Com_Cooldown.size() > 0) {
				pm.registerEvents(new CooldownComandos(), this);	
			}
		}
		
		if (Settings.Comandos_Com_Delay) {
			if (Settings.Lista_Dos_Comandos_Com_Delay != null && Settings.Lista_Dos_Comandos_Com_Delay.size() > 0) {
				pm.registerEvents(new DelayComandos(), this);	
			}
		}
		
		if (Settings.Deletar_Comandos) {
			if (Settings.Lista_Dos_Comandos_Deletados != null && Settings.Lista_Dos_Comandos_Deletados.size() > 0) {
				DeletarComandos.deleteCommands();
			}
		}
		
		if (Settings.Desativar_Chuva) {
			pm.registerEvents(new DesativarChuva(), this);
		}

		if (Settings.Desativar_Ciclo_Do_Dia) {
			if (version == Version.v1_5) {
				DesativarCicloDoDia.stopDaylightCycleOLD();
			} else {
				DesativarCicloDoDia.stopDaylightCycle();
			}
		}

		if (Settings.Desativar_Dano_Do_Blaze) {
			if (!isVeryOldVersion()) {
				pm.registerEvents(new DesativarDanoDoBlaze(), this);
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Desativar Dano do Blazer (nao disponivel nas versoes 1.5 e 1.6)");
			}
		}
		
		if (Settings.Desativar_Dano_Do_Cacto) {
			pm.registerEvents(new DesativarDanoDoCacto(), this);
		}
		
		if (Settings.Desativar_Dano_Do_EnderDragon) {
			pm.registerEvents(new DesativarDanoDoEnderDragon(), this);
		}

		if (Settings.Desativar_Dano_Do_Ghast) {
			if (!isVeryOldVersion()) {
				pm.registerEvents(new DesativarDanoDoGhast(), this);
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Desativar Dano do Ghast (nao disponivel nas versoes 1.5 e 1.6)");
			}
		}
		
		if (Settings.Desativar_Dano_Do_Wither) {
			pm.registerEvents(new DesativarDanoDoWither(), this);
		}

		if (Settings.Desativar_Flow_Da_Agua_E_Lava) {
			pm.registerEvents(new DesativarFlowDaAguaELava(), this);
		}

		if (Settings.Desativar_Fome_Nos_Mundos) {
			pm.registerEvents(new DesativarFomeNosMundos(), this);
		}

		if (Settings.Desativar_Mensagem_De_Entrada) {
			pm.registerEvents(new DesativarMensagemDeEntrada(), this);
		}

		if (Settings.Desativar_Mensagem_De_Morte) {
			pm.registerEvents(new DesativarMensagemDeMorte(), this);
		}

		if (Settings.Desativar_Mensagem_De_Saida) {
			pm.registerEvents(new DesativarMensagemDeSaida(), this);
		}

		if (Settings.Desativar_Mobs_Naturais) {
			if (isOldVersion()) {
				pm.registerEvents(new DesativarMobsNaturaisOLD(), this);				
			} else {
				pm.registerEvents(new DesativarMobsNaturais(), this);
			}
		}

		if (Settings.Desativar_Propagacao_Do_Fogo) {
			pm.registerEvents(new DesativarPropagacaoDoFogo(), this);
		}

		if (Settings.Desativar_Queda_Da_Areia) {
			pm.registerEvents(new DesativarQuedaDaAreia(), this);
		}

		if (Settings.Desativar_Queda_Da_Bigorna) {
			pm.registerEvents(new DesativarQuedaDaBigorna(), this);
		}
		
		if (Settings.Desativar_Queda_Das_Folhas) {
			pm.registerEvents(new DesativarQuedaDasFolhas(), this);
		}

		if (Settings.Dropar_Cabeca_Ao_Morrer) {
			pm.registerEvents(new DroparCabecaAoMorrer(), this);
		}

		if (Settings.EnderPearl_Cooldown_Ativar) {
			if (!isVeryOldVersion()) {
				pm.registerEvents(new EnderPearlCooldown(), this);
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("EnderPearl Cooldown (nao disponivel nas versoes 1.5 e 1.6)");
			}
		}

		if (Settings.Entrar_No_Spawn_Ao_Logar) {
			pm.registerEvents(new EntrarNoSpawnAoLogar(), Main.this);
		}

		if (Settings.Executar_Comandos_No_Primeiro_Login) {
			if (Settings.Lista_Dos_Comandos_Executados_No_Primeiro_Login != null && Settings.Lista_Dos_Comandos_Executados_No_Primeiro_Login.size() > 0) {
				pm.registerEvents(new ComandosPrimeiroLogin(), Main.this);	
			}
		}
		
		if (Settings.Invencibilidade_Ao_Teleportar) {
			pm.registerEvents(new InvencibilidadeAoTeleportar(), this);
		}

		if (Settings.Limitador_De_Players) {
			if (!isOldVersion()) {
				pm.registerEvents(new LimiteDePlayers(), this);
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Limitador de Players (nao disponivel nas versoes 1.5, 1.6 e 1.7)");
			}
		}

		if (Settings.Mensagem_De_Boas_Vindas_Ativar) {
			pm.registerEvents(new MensagemDeBoasVindas(), this);
		}

		if (Settings.Motd_Ativar) {
			pm.registerEvents(new Motd(), this);
		}

		if (Settings.ScoreBoard_Ativar) {
			if (isOldVersion()) {
				pm.registerEvents(new ScoreBoardOLD(), this);
				ScoreBoardOLD.loadScoreBoard();
			} else {
				pm.registerEvents(new ScoreBoard(), this);
				ScoreBoard.loadScoreBoard();
			}
		}

		if (Settings.Sistema_De_Fly_Para_Players) {
			if (commands.getBoolean("comandos.fly.ativar-comando")) {
				pm.registerEvents(new FlyListener(), this);
			}
		}
		
		if (Settings.Sistema_De_Stack_Mobs) {
			pm.registerEvents(new StackMobs(), this);
		}

		if (Settings.Sistema_De_Spawners) {
			if (commands.getBoolean("comandos.sgive.ativar-comando")) {
				if (!isVeryNewVersion()) {
					
					if (Settings.Bloquear_Trocar_Tipo_Do_Spawner_Com_Ovo) {
						pm.registerEvents(new BloquearTrocarTipoDoSpawnerComOvo(), this);
					}
					
					pm.registerEvents(new SpawnerStackManager(), this);
					
					if (isOldVersion()) {
						pm.registerEvents(new SistemaDeSpawnersOLD(), this);
						if (Settings.Dropar_Spawner_Ao_Explodir) {
							pm.registerEvents(new DroparSpawnerAoExplodirOLD(), this);
						}
					} else {
						pm.registerEvents(new SistemaDeSpawners(), this);
						if (Settings.Dropar_Spawner_Ao_Explodir) {
							pm.registerEvents(new DroparSpawnerAoExplodir(), this);
						}
					}
				} else {
					notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Sistema de Spawners (nao disponivel nas versoes 1.13, 1.14, 1.15, 1.16, 1.17 e nas versoes acima da 1.17)");
				}
			}
		}

		if (Settings.Title_De_Boas_Vindas_Ativar) {
			if (!isOldVersion()) {
				pm.registerEvents(new TitleDeBoasVindas(), this);
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Title de Boas Vindas (nao disponivel nas versoes 1.5, 1.6, 1.7)");
			}
		}

		if (Settings.Ativar_Tablist) {
			if (!isOldVersion()) {
				pm.registerEvents(new Tablist(), this);
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Tablist (nao disponivel nas versoes 1.5, 1.6, 1.7)");
			}
		}

		if (Settings.AtivarAddons_Core) {
			if (pm.getPlugin("paragonn-core") == null) {
				getServer().getConsoleSender().sendMessage("§c[System] Paragonn Core nao encontrado, desativando addon de chat do core!");
			} else {
				pm.registerEvents(new ParagonnCoreChat(), this);
			}
		}

		if (Settings.AtivarAddons_McMMO) {
			if (!isOldVersion()) {
				if (pm.getPlugin("mcMMO") == null) {
					getServer().getConsoleSender().sendMessage("§c[System] McMMO nao encontrado, desativando addons!");
				} else {
					pm.registerEvents(new Mcmmo(), this);
					if (Settings.AtivarAddons_Core && pm.getPlugin("paragonn-core") != null) {
						pm.registerEvents(new McTopTag(), this);
						McTopTag.checkMCTop();
					}
				}
			}
		}

		if (Settings.AtivarAddons_MassiveFactions) {
			if (pm.getPlugin("MassiveCore") == null || pm.getPlugin("Factions") == null) {
				getServer().getConsoleSender().sendMessage("§c[System] Factions nao encontrado, desativando addons!");
			} else {
				setupFactions = true;
			}
		}
		
		if (Settings.AtivarAddons_Vault) {
			if (pm.getPlugin("Vault") == null) {
				getServer().getConsoleSender().sendMessage("§c[System] Vault nao encontrado, desativando addons!");
			} else {
				if (Vault.setupEconomy()) {
					if (Settings.AtivarAddons_Core && pm.getPlugin("paragonn-core") != null) {
						pm.registerEvents(new MagnataTag(), this);
						MagnataTag.checkMagnata();
					}
				} else {
					getServer().getConsoleSender().sendMessage("§c[System] Nenhum plugin valido de economia encontrado!");
				}
			}
		}

		if (commands.getBoolean("comandos.vanish.ativar-comando")) {
			pm.registerEvents(new VanishListener(), this);
		}
		
		if (commands.getBoolean("comandos.echest.ativar-comando")) {
			pm.registerEvents(new EnderChestListener(), this);
		}
		
		if (commands.getBoolean("comandos.invsee.ativar-comando")) {
			pm.registerEvents(new InvseeListener(), this);
		}
		
		if (commands.getBoolean("comandos.kit.ativar-comando")) {
			if (version.value < 17) {
				pm.registerEvents(new KitsListener(), this);				
			} else {
				notificarQueEsteRecursoNaoEstaDisponivelNestaVersao("Sistema de Kits (nao disponivel nas versoes acima da 1.17)");
			}
		}
		
		if (commands.getBoolean("comandos.back.ativar-comando")) {
			pm.registerEvents(new BackListener(), this);
		}

		pm.registerEvents(new PlayerData(), this);
		pm.registerEvents(new ManterXpAoMorrer(), this);
		pm.registerEvents(new Outros(), this);

		File updaterConfigFile = new File(getDataFolder(), "config.yml");
		if (updaterConfigFile.exists()) {
			FileConfiguration updaterCfg = YamlConfiguration.loadConfiguration(updaterConfigFile);
			if (updaterCfg.getBoolean("updater.check-on-first-admin-join", true)) {
				pm.registerEvents(new UpdaterJoinListener(), this);
			}
		}
	}

	/**
	 * Dispara no máximo uma vez por ativação do plugin a verificação de updates no GitHub,
	 * na primeira vez que um jogador com {@link SystemUpdater#UPDATER_PERMISSION} entra.
	 */
	public void scheduleUpdaterWhenFirstAdminJoins() {
		if (!this.isEnabled() || this.updaterCheckStarted) {
			return;
		}
		this.updaterCheckStarted = true;
		Bukkit.getScheduler().runTask(this, () -> {
			if (this.isEnabled()) {
				new SystemUpdater(this, 0).run();
			}
		});
	}

	/**
	 * Remove pasta legada {@code plugins/paragonn-system/update} e jars deste plugin na pasta global de update do Spigot.
	 */
	private void cleanupUpdaterArtifactsAfterStartup() {
		File legacyUpdateDir = new File(getDataFolder(), "update");
		if (legacyUpdateDir.exists()) {
			deleteRecursivelyQuiet(legacyUpdateDir);
		}

		File spigotUpdateDir = Bukkit.getUpdateFolderFile();
		if (!spigotUpdateDir.exists() || !spigotUpdateDir.isDirectory()) {
			return;
		}

		File pluginUpdateJar = new File(spigotUpdateDir, getPluginJarFilename());
		File pluginPartialJar = new File(spigotUpdateDir, getPluginJarFilename() + ".part");
		if (pluginUpdateJar.exists()) {
			//noinspection ResultOfMethodCallIgnored
			pluginUpdateJar.delete();
		}
		if (pluginPartialJar.exists()) {
			//noinspection ResultOfMethodCallIgnored
			pluginPartialJar.delete();
		}

		File[] remaining = spigotUpdateDir.listFiles();
		if (remaining != null && remaining.length == 0) {
			//noinspection ResultOfMethodCallIgnored
			spigotUpdateDir.delete();
		}
	}

	private static void deleteRecursivelyQuiet(File root) {
		if (root == null || !root.exists()) {
			return;
		}
		if (root.isDirectory()) {
			File[] children = root.listFiles();
			if (children != null) {
				for (File c : children) {
					deleteRecursivelyQuiet(c);
				}
			}
		}
		//noinspection ResultOfMethodCallIgnored
		root.delete();
	}
	
	private void notificarQueEsteRecursoNaoEstaDisponivelNestaVersao(String mensagem) {
		getServer().getConsoleSender().sendMessage("§c[System] " + mensagem);
	}

	private void disablePlugin() {
		try {
			this.updaterCheckStarted = false;
			SystemUpdater.UPDATER = null;
			SystemUpdater.resetGithubUpdateCheckLock();

			studios.paragonn.system.sistemas.spawners.SpawnerStackManager.save();
			HandlerList.unregisterAll(this);
			Bukkit.getScheduler().cancelTasks(this);
	
			if (Settings.AtivarAddons_McMMO && McTopTag.TTask != null) {
				McTopTag.TTask.cancel();
			}
			
			if (Settings.AtivarAddons_Vault && MagnataTag.MTask != null) {
				MagnataTag.MTask.cancel();
			}
	
			if (Settings.Auto_Anuncio && AutoAnuncio.XTask != null) {
				AutoAnuncio.XTask.cancel();
			}
		} catch (Throwable e) {}
	}
	
	public static boolean isRecomendedVersion() {
		if (version == Version.v1_8) 
			return true;
		if (version == Version.v1_9)
			return true;
		if (version == Version.v1_10) 
			return true;
		if (version == Version.v1_11) 
			return true;
		if (version == Version.v1_12) 
			return true;
		return false;
	}

	public static boolean isOldVersion() {
		if (version == Version.v1_7)
			return true;
		if (version == Version.v1_6)
			return true;
		if (version == Version.v1_5)
			return true;
		return false;
	}
	
	public static boolean isVeryOldVersion() {
		if (version == Version.v1_6)
			return true;
		if (version == Version.v1_5)
			return true;
		return false;
	}

	public static boolean isNewVersion() {
		if (version == Version.v1_21)
			return true;
		if (version == Version.v1_20)
			return true;
		if (version == Version.v1_19)
			return true;
		if (version == Version.v1_18)
			return true;
		if (version == Version.v1_17)
			return true;
		if (version == Version.v1_16_5)
			return true;
		if (version == Version.v1_16_4)
			return true;
		if (version == Version.v1_16_3)
			return true;
		if (version == Version.v1_16_2)
			return true;
		if (version == Version.v1_16)
			return true;
		if (version == Version.v1_15)
			return true;
		if (version == Version.v1_14)
			return true;
		if (version == Version.v1_13)
			return true;
		if (version == Version.v1_12)
			return true;
		if (version == Version.v1_11)
			return true;
		return false;
	}
	
	public static boolean isVeryNewVersion() {
		if (version == Version.v1_21)
			return true;
		if (version == Version.v1_20)
			return true;
		if (version == Version.v1_19)
			return true;
		if (version == Version.v1_18)
			return true;
		if (version == Version.v1_17)
			return true;
		if (version == Version.v1_16_5)
			return true;
		if (version == Version.v1_16_4)
			return true;
		if (version == Version.v1_16_3)
			return true;
		if (version == Version.v1_16_2)
			return true;
		if (version == Version.v1_16)
			return true;
		if (version == Version.v1_15)
			return true;
		if (version == Version.v1_14)
			return true;
		if (version == Version.v1_13)
			return true;
		return false;
	}
	
	public static boolean isVeryFuckingNewVersion() {
		if (version == Version.v1_21)
			return true;
		if (version == Version.v1_20)
			return true;
		if (version == Version.v1_19)
			return true;
		if (version == Version.v1_18)
			return true;
		if (version == Version.v1_17)
			return true;
		if (version == Version.v1_16_5)
			return true;
		if (version == Version.v1_16_4)
			return true;
		if (version == Version.v1_16_3)
			return true;
		if (version == Version.v1_16_2)
			return true;
		if (version == Version.v1_16)
			return true;
		if (version == Version.v1_15)
			return true;
		if (version == Version.v1_14)
			return true;
		return false;
	}
	
	public static boolean isMotherFuckerVersion() {
		if (version == Version.v1_21)
			return true;
		if (version == Version.v1_20)
			return true;
		if (version == Version.v1_19)
			return true;
		if (version == Version.v1_18)
			return true;
		if (version == Version.v1_17)
			return true;
		if (version == Version.v1_16_5)
			return true;
		if (version == Version.v1_16_4)
			return true;
		if (version == Version.v1_16_3)
			return true;
		if (version == Version.v1_16_2)
			return true;
		return false;
	}

	public static JarType getTypeJar() {
		return jarType;
	}

	public static Version getVersion() {
		return version;
	}

	public static Main get() {
		return main;
	}

	/** Nome do jar na pasta {@code plugins/} (pasta {@code update} do Spigot usa o mesmo nome). */
	public String getPluginJarFilename() {
		return getFile().getName();
	}

}