package studios.paragonn.system.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import studios.paragonn.system.Main;
import studios.paragonn.system.apis.ActionBarAPI;

/**
 * Atualização de versão via releases do GitHub (mesma lógica do {@code PUpdater} do paragonn-core).
 */
public class SystemUpdater {

	private static final AtomicBoolean GITHUB_UPDATE_CHECK_RUNNING = new AtomicBoolean(false);

	private static final long DOWNLOAD_HOTBAR_MIN_MS = 5000L;
	private static final long UPDATER_API_LOADING_MS = 4500L;
	private static final long UPDATER_ACTION_BAR_PERIOD_TICKS = 10L;

	/** Permissão para checagem automática e comandos {@code /system updates} e {@code /system atualizar}. */
	public static final String UPDATER_PERMISSION = "system.system";

	public static SystemUpdater UPDATER;
	public boolean canDownload;

	public static void resetGithubUpdateCheckLock() {
		GITHUB_UPDATE_CHECK_RUNNING.set(false);
	}

	private final JavaPlugin plugin;
	private final Logger logger;
	private final String githubOwner;
	private final String githubRepo;
	private final String githubAssetName;
	private final String githubToken;
	private String latestVersion;
	private String downloadUrl;
	private int lastHttpCode = -1;
	private BukkitTask loadingTask;
	/** Definido na primeira consulta a {@code /repos/owner/repo}: publico sem token; privado ou limite com token. */
	private boolean githubApiAuthResolved;
	private boolean githubApiUseAuthorization;

	public SystemUpdater(JavaPlugin plugin, int resourceId) {
		this(plugin);
	}

	public SystemUpdater(JavaPlugin plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		FileConfiguration cfg = loadConfig(plugin);
		this.githubOwner = cfg.getString("updater.github.owner", "");
		this.githubRepo = cfg.getString("updater.github.repo", "");
		this.githubAssetName = cfg.getString("updater.github.asset-name", "paragonn-system.jar");
		this.githubToken = resolveGithubToken(cfg.getString("updater.github.token", ""), this.logger);
	}

	/**
	 * Resolve o PAT: YAML tem prioridade se for um token real; placeholder não substituído ou vazio cai para
	 * {@code PARAGONN_GITHUB_TOKEN}. Prefixo {@code Bearer } duplicado (utilizador colou o header inteiro no YAML) é removido
	 * porque o código já envia {@code Authorization: Bearer ...}.
	 */
	private static String resolveGithubToken(String tokenFromYaml, Logger logger) {
		String fromEnv = System.getenv("PARAGONN_GITHUB_TOKEN");
		String fromCfg = tokenFromYaml == null ? "" : tokenFromYaml.trim();
		if (fromCfg.isEmpty()) {
			return stripBearerPrefix(fromEnv);
		}
		// Build sem token / JAR antigo: literal continua no YAML → GitHub devolve 401 se for enviado como Bearer
		if (fromCfg.contains("githubTokenPackaged")) {
			if (logger != null) {
				logger.log(Level.INFO, "[UPDATER] Token no config.yml ainda e placeholder de build; ignorando e usando PARAGONN_GITHUB_TOKEN se existir.");
			}
			return stripBearerPrefix(fromEnv);
		}
		String normalized = stripBearerPrefix(fromCfg);
		if (normalized == null || normalized.isEmpty()) {
			return stripBearerPrefix(fromEnv);
		}
		return normalized;
	}

	private static String stripBearerPrefix(String token) {
		if (token == null) {
			return null;
		}
		String t = token.trim();
		if (t.regionMatches(true, 0, "bearer ", 0, 7)) {
			t = t.substring(7).trim();
		}
		return t.isEmpty() ? null : t;
	}

	private static FileConfiguration loadConfig(JavaPlugin plugin) {
		return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
	}

	public static String getVersion(int resourceId) {
		if (UPDATER == null) {
			return null;
		}
		return UPDATER.getLatestVersion();
	}

	public String getLatestVersion() {
		return this.latestVersion;
	}

	private void logInfo(String msg) {
		this.logger.log(Level.INFO, "[UPDATER] " + msg);
	}

	private void logWarning(String msg) {
		this.logger.log(Level.WARNING, "[UPDATER] " + msg);
	}

	private void logSevere(String msg) {
		this.logger.log(Level.SEVERE, "[UPDATER] " + msg);
	}

	private HttpsURLConnection openGithubConnection(String url) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("Accept", "application/vnd.github+json");
		connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
		connection.setRequestProperty("User-Agent", "paragonn-system-updater");
		if (this.githubApiUseAuthorization && this.githubToken != null && !this.githubToken.trim().isEmpty()) {
			connection.setRequestProperty("Authorization", "Bearer " + this.githubToken.trim());
		}
		return connection;
	}

	private void ensureGithubApiAuthMode(String owner, String repo) {
		synchronized (this) {
			if (this.githubApiAuthResolved) {
				return;
			}
			try {
				this.githubApiUseAuthorization = computeGithubApiUseAuthorization(owner, repo, this.githubToken);
			} catch (IOException ex) {
				logWarning("Nao foi possivel consultar visibilidade do repo (" + ex.getMessage() + "); usando token se existir.");
				this.githubApiUseAuthorization = this.githubToken != null && !this.githubToken.trim().isEmpty();
			}
			this.githubApiAuthResolved = true;
			logInfo("Pedidos a API GitHub " + (this.githubApiUseAuthorization ? "com Authorization (repo privado ou limite)." : "sem Authorization (repo publico)."));
		}
	}

	private static boolean computeGithubApiUseAuthorization(String owner, String repo, String token) throws IOException {
		String meta = "https://api.github.com/repos/" + owner + "/" + repo;
		boolean hasTok = token != null && !token.trim().isEmpty();
		int anon = httpRepoMetaStatus(meta, false, null);
		if (anon == 200) {
			JSONObject jo = httpRepoMetaJson(meta, false, null);
			if (jo != null && Boolean.TRUE.equals(jo.get("private"))) {
				return hasTok;
			}
			return false;
		}
		if (anon == 404 && hasTok) {
			int auth = httpRepoMetaStatus(meta, true, token.trim());
			return auth == 200;
		}
		if (anon == 403 && hasTok) {
			return true;
		}
		return hasTok;
	}

	private static int httpRepoMetaStatus(String url, boolean withAuth, String tok) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/vnd.github+json");
		connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
		connection.setRequestProperty("User-Agent", "paragonn-system-updater");
		if (withAuth && tok != null && !tok.trim().isEmpty()) {
			connection.setRequestProperty("Authorization", "Bearer " + tok.trim());
		}
		int code = connection.getResponseCode();
		discardHttpBody(connection);
		return code;
	}

	private static JSONObject httpRepoMetaJson(String url, boolean withAuth, String tok) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/vnd.github+json");
		connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
		connection.setRequestProperty("User-Agent", "paragonn-system-updater");
		if (withAuth && tok != null && !tok.trim().isEmpty()) {
			connection.setRequestProperty("Authorization", "Bearer " + tok.trim());
		}
		int code = connection.getResponseCode();
		if (code < 200 || code >= 300) {
			discardHttpBody(connection);
			return null;
		}
		try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
			Object parsed = new JSONParser().parse(reader);
			if (parsed instanceof JSONObject) {
				return (JSONObject) parsed;
			}
		} catch (org.json.simple.parser.ParseException ex) {
			return null;
		}
		return null;
	}

	private static void discardHttpBody(HttpsURLConnection connection) throws IOException {
		InputStream err = connection.getErrorStream();
		if (err != null) {
			try {
				byte[] buf = new byte[8192];
				while (err.read(buf) != -1) {
				}
			} finally {
				err.close();
			}
			return;
		}
		InputStream in = connection.getInputStream();
		try {
			byte[] buf = new byte[8192];
			while (in.read(buf) != -1) {
			}
		} finally {
			in.close();
		}
	}

	private JSONObject requestJson(String url) throws IOException {
		HttpsURLConnection connection = openGithubConnection(url);
		this.lastHttpCode = connection.getResponseCode();
		if (this.lastHttpCode < 200 || this.lastHttpCode >= 300) {
			InputStream err = connection.getErrorStream();
			if (err != null) {
				try {
					err.close();
				} catch (IOException ignored) {
				}
			}
			throw new IOException("HTTP " + this.lastHttpCode);
		}

		try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
			Object parsed = new JSONParser().parse(reader);
			if (!(parsed instanceof JSONObject)) {
				throw new IOException("Resposta GitHub invalida (nao e JSON objeto).");
			}
			return (JSONObject) parsed;
		} catch (org.json.simple.parser.ParseException ex) {
			throw new IOException("Resposta GitHub invalida: " + ex.getMessage(), ex);
		}
	}

	private JSONArray requestJsonArray(String url) throws IOException {
		HttpsURLConnection connection = openGithubConnection(url);
		this.lastHttpCode = connection.getResponseCode();
		if (this.lastHttpCode < 200 || this.lastHttpCode >= 300) {
			InputStream err = connection.getErrorStream();
			if (err != null) {
				try {
					err.close();
				} catch (IOException ignored) {
				}
			}
			throw new IOException("HTTP " + this.lastHttpCode);
		}

		try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
			Object parsed = new JSONParser().parse(reader);
			if (!(parsed instanceof JSONArray)) {
				throw new IOException("Resposta GitHub invalida (nao e JSON array).");
			}
			return (JSONArray) parsed;
		} catch (org.json.simple.parser.ParseException ex) {
			throw new IOException("Resposta GitHub invalida: " + ex.getMessage(), ex);
		}
	}

	private void loadLatestRelease() throws IOException {
		this.latestVersion = null;
		this.downloadUrl = null;
		this.lastHttpCode = -1;

		if (this.githubOwner == null || this.githubOwner.trim().isEmpty() || this.githubRepo == null || this.githubRepo.trim().isEmpty()) {
			return;
		}

		String owner = this.githubOwner.trim();
		String repo = this.githubRepo.trim();
		ensureGithubApiAuthMode(owner, repo);
		String listUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases?per_page=50";

		JSONArray releases;
		try {
			releases = requestJsonArray(listUrl);
		} catch (IOException ex) {
			logInfo("Lista de releases falhou (" + ex.getMessage() + "); tentando /releases/latest.");
			loadLatestReleaseFromLatestEndpoint(owner, repo);
			return;
		}

		JSONObject bestRelease = null;
		String bestTag = null;
		for (Object element : releases) {
			if (!(element instanceof JSONObject)) {
				continue;
			}
			JSONObject rel = (JSONObject) element;
			Object draftObj = rel.get("draft");
			if (Boolean.TRUE.equals(draftObj)) {
				continue;
			}
			Object preObj = rel.get("prerelease");
			if (Boolean.TRUE.equals(preObj)) {
				continue;
			}
			Object tagObj = rel.get("tag_name");
			if (tagObj == null) {
				continue;
			}
			String tag = tagObj.toString().trim();
			JSONArray assets = (JSONArray) rel.get("assets");
			if (!releaseContainsAsset(assets, this.githubAssetName)) {
				continue;
			}
			if (bestTag == null || compareSemverVersions(tag, bestTag) > 0) {
				bestTag = tag;
				bestRelease = rel;
			}
		}

		if (bestRelease == null || bestTag == null) {
			logInfo("Nenhuma release estavel com asset \"" + this.githubAssetName + "\" na lista; tentando /releases/latest.");
			loadLatestReleaseFromLatestEndpoint(owner, repo);
			return;
		}

		this.latestVersion = bestTag;
		this.downloadUrl = findAssetDownloadUrl((JSONArray) bestRelease.get("assets"), this.githubAssetName);
		logInfo("Release remota escolhida por semver: " + bestTag + " (asset " + this.githubAssetName + ").");
	}

	private static boolean releaseContainsAsset(JSONArray assets, String assetName) {
		if (assets == null || assetName == null) {
			return false;
		}
		for (Object assetObject : assets) {
			if (!(assetObject instanceof JSONObject)) {
				continue;
			}
			JSONObject asset = (JSONObject) assetObject;
			Object nameObj = asset.get("name");
			if (nameObj != null && nameObj.toString().equalsIgnoreCase(assetName)) {
				return true;
			}
		}
		return false;
	}

	private static String findAssetDownloadUrl(JSONArray assets, String assetName) {
		if (assets == null) {
			return null;
		}
		for (Object assetObject : assets) {
			if (!(assetObject instanceof JSONObject)) {
				continue;
			}
			JSONObject asset = (JSONObject) assetObject;
			Object nameObj = asset.get("name");
			if (nameObj != null && nameObj.toString().equalsIgnoreCase(assetName)) {
				Object assetApiUrl = asset.get("url");
				return assetApiUrl != null ? assetApiUrl.toString() : null;
			}
		}
		return null;
	}

	private void loadLatestReleaseFromLatestEndpoint(String owner, String repo) throws IOException {
		JSONObject release = requestJson("https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest");
		Object tagName = release.get("tag_name");
		if (tagName != null) {
			this.latestVersion = tagName.toString().trim();
		}
		this.downloadUrl = findAssetDownloadUrl((JSONArray) release.get("assets"), this.githubAssetName);
	}

	private static String normalizeReleaseVersion(String raw) {
		if (raw == null) {
			return "";
		}
		String s = raw.trim();
		if (s.length() >= 1 && (s.charAt(0) == 'v' || s.charAt(0) == 'V')) {
			s = s.substring(1).trim();
		}
		int plus = s.indexOf('+');
		if (plus >= 0) {
			s = s.substring(0, plus).trim();
		}
		return s;
	}

	private static int parseVersionSegment(String segment) {
		if (segment == null || segment.isEmpty()) {
			return 0;
		}
		int i = 0;
		while (i < segment.length() && Character.isDigit(segment.charAt(i))) {
			i++;
		}
		if (i == 0) {
			return 0;
		}
		try {
			return Integer.parseInt(segment.substring(0, i));
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	private static int compareSemverVersions(String a, String b) {
		String na = normalizeReleaseVersion(a);
		String nb = normalizeReleaseVersion(b);
		if (na.isEmpty() || nb.isEmpty()) {
			return na.compareTo(nb);
		}
		String[] pa = na.split("\\.");
		String[] pb = nb.split("\\.");
		int n = Math.max(pa.length, pb.length);
		for (int i = 0; i < n; i++) {
			int va = parseVersionSegment(i < pa.length ? pa[i] : "0");
			int vb = parseVersionSegment(i < pb.length ? pb[i] : "0");
			if (va != vb) {
				return Integer.compare(va, vb);
			}
		}
		return 0;
	}

	private static String displayJarBaseName(String jarFileName) {
		if (jarFileName == null || jarFileName.isEmpty()) {
			return "plugin";
		}
		String s = jarFileName.replace("..", ".").trim();
		if (s.toLowerCase(Locale.ROOT).endsWith(".jar")) {
			s = s.substring(0, s.length() - 4);
		}
		return s.isEmpty() ? "plugin" : s;
	}

	private static String repeatString(String unit, int count) {
		if (count <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(unit.length() * count);
		for (int i = 0; i < count; i++) {
			sb.append(unit);
		}
		return sb.toString();
	}

	private static String progressBarSegment(int percentage) {
		int filled = Math.min(25, Math.max(0, percentage / 4));
		return "§7[§a" + repeatString("|", filled) + "§8" + repeatString("|", 25 - filled) + "§7]";
	}

	private void pushUpdaterLoadingToAdmins(int percentage) {
		int pct = Math.max(0, Math.min(99, percentage));
		String msg = "§fConsultando GitHub API §a" + pct + "% " + progressBarSegment(pct);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission(UPDATER_PERMISSION)) {
				ActionBarAPI.sendActionBar(p, msg);
			}
		}
	}

	private void clearUpdaterLoadingActionBar() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission(UPDATER_PERMISSION)) {
				ActionBarAPI.sendActionBar(p, "");
			}
		}
	}

	private void notifyAdminsSystemUpdateAvailable() {
		String ver = this.latestVersion != null ? this.latestVersion : "?";
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.hasPermission(UPDATER_PERMISSION)) {
				continue;
			}
			TextComponent component = new TextComponent("");
			for (BaseComponent part : TextComponent.fromLegacyText(
					"\n §6§lATUALIZAÇÃO \n \n §7Foi encontrado um update novo do §6Paragonn System §f(" + ver + ")§7, para atualizar basta clicar ")) {
				component.addExtra(part);
			}
			TextComponent click = new TextComponent("AQUI");
			click.setColor(ChatColor.GREEN);
			click.setBold(true);
			click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/system atualizar"));
			click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					TextComponent.fromLegacyText("§7Clique aqui para atualizar o paragonn-system.")));
			component.addExtra(click);
			for (BaseComponent part : TextComponent.fromLegacyText("§7.\n ")) {
				component.addExtra(part);
			}
			player.spigot().sendMessage(component);
			playSound(player, true);
		}
	}

	private void notifyAdminsSystemUpToDate(String localVersion, String remoteTag) {
		String loc = localVersion != null ? localVersion : "?";
		String rem = remoteTag != null ? remoteTag : "?";
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.hasPermission(UPDATER_PERMISSION)) {
				continue;
			}
			player.sendMessage("§a§lparagonn-system §r§7» Versão em dia com o GitHub.");
			player.sendMessage("§7Servidor: §f" + loc + " §7· Release: §f" + rem);
			playSound(player, false);
		}
	}

	private static void playSound(Player player, boolean positive) {
		Sound s = positive ? firstAvailableSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP")
				: firstAvailableSound("BLOCK_NOTE_BLOCK_PLING", "BLOCK_NOTE_PLING", "NOTE_PLING");
		if (s != null) {
			player.playSound(player.getLocation(), s, 1.0F, positive ? 1.0F : 1.65F);
		}
	}

	private static Sound firstAvailableSound(String... names) {
		for (String n : names) {
			try {
				return Sound.valueOf(n);
			} catch (IllegalArgumentException ignored) {
			}
		}
		return null;
	}

	private void startLoadingAnimation(final long startedAt) {
		stopLoadingAnimation();
		this.loadingTask = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
			long elapsed = System.currentTimeMillis() - startedAt;
			int pct = (int) Math.min(99L, elapsed * 100L / Math.max(1L, UPDATER_API_LOADING_MS));
			pushUpdaterLoadingToAdmins(pct);
		}, 2L, UPDATER_ACTION_BAR_PERIOD_TICKS);
	}

	private void stopLoadingAnimation() {
		if (this.loadingTask != null) {
			this.loadingTask.cancel();
			this.loadingTask = null;
		}
		clearUpdaterLoadingActionBar();
	}

	private void finishRun(PluginDescriptionFile description, Exception asyncError) {
		try {
			stopLoadingAnimation();

			if (this.githubOwner == null || this.githubOwner.trim().isEmpty() || this.githubRepo == null || this.githubRepo.trim().isEmpty()) {
				logInfo("Updater ignorado: defina updater.github.owner e updater.github.repo no config.yml.");
				return;
			}

			if (asyncError != null) {
				if (this.lastHttpCode == 404) {
					logInfo("Nenhuma release publicada no GitHub ainda (HTTP 404). Publique uma release para habilitar updates.");
					return;
				}
				if (this.lastHttpCode == 401 || this.lastHttpCode == 403) {
					logSevere("GitHub recusou acesso (HTTP " + this.lastHttpCode + "). Verifique token ou permissoes do repositorio.");
					return;
				}
				logSevere("Nao foi possivel buscar a ultima release no GitHub: " + asyncError.getMessage());
				return;
			}

			String current = description.getVersion();
			if (this.latestVersion == null) {
				logSevere("Nao foi possivel buscar updates (resposta sem tag_name).");
			} else {
				String localNorm = normalizeReleaseVersion(current);
				String remoteNorm = normalizeReleaseVersion(this.latestVersion);
				int cmp = compareSemverVersions(current, this.latestVersion);

				if (cmp >= 0 || localNorm.equalsIgnoreCase(remoteNorm)) {
					UPDATER = null;
					this.canDownload = false;
					logInfo("Plugin se encontra em sua ultima versao (local " + current + ", remoto " + this.latestVersion + ").");
					notifyAdminsSystemUpToDate(current, this.latestVersion);
				} else {
					logWarning("Encontramos um update. Utilize /system atualizar para prosseguir.");
					UPDATER = this;
					this.canDownload = true;
					notifyAdminsSystemUpdateAvailable();
				}
			}
		} finally {
			GITHUB_UPDATE_CHECK_RUNNING.set(false);
		}
	}

	public boolean tryBeginGithubUpdateCheck() {
		if (!Bukkit.isPrimaryThread()) {
			return false;
		}
		if (this.githubOwner == null || this.githubOwner.trim().isEmpty() || this.githubRepo == null || this.githubRepo.trim().isEmpty()) {
			logInfo("Updater ignorado: defina updater.github.owner e updater.github.repo no config.yml.");
			return false;
		}
		if (!GITHUB_UPDATE_CHECK_RUNNING.compareAndSet(false, true)) {
			logInfo("Verificacao de updates no GitHub ja em andamento.");
			return false;
		}

		try {
			PluginDescriptionFile description = this.plugin.getDescription();
			logInfo("Verificacao de updates no GitHub em segundo plano...");
			final long spinnerStartedAt = System.currentTimeMillis();
			startLoadingAnimation(spinnerStartedAt);

			Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
				Exception error = null;
				try {
					loadLatestRelease();
				} catch (Exception ex) {
					error = ex;
				}
				long elapsed = System.currentTimeMillis() - spinnerStartedAt;
				long remaining = UPDATER_API_LOADING_MS - elapsed;
				if (remaining > 0L) {
					try {
						Thread.sleep(remaining);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
				final Exception captured = error;
				Bukkit.getScheduler().runTask(this.plugin, () -> finishRun(description, captured));
			});
			return true;
		} catch (Throwable t) {
			GITHUB_UPDATE_CHECK_RUNNING.set(false);
			throw t;
		}
	}

	public void run() {
		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(this.plugin, this::run);
			return;
		}
		tryBeginGithubUpdateCheck();
	}

	public void downloadUpdate(Player player) {
		player.sendMessage("§aBaixando atualizacao...");

		if (this.downloadUrl == null || this.downloadUrl.trim().isEmpty()) {
			player.sendMessage("§cNao encontramos o asset " + this.githubAssetName + " na release mais recente.");
			return;
		}

		final UUID playerId = player.getUniqueId();
		final String jarOnDisk = Main.get().getPluginJarFilename();
		final String jarLabel = displayJarBaseName(jarOnDisk);
		final File updateRoot = Bukkit.getUpdateFolderFile();
		final File destFile = new File(updateRoot, jarOnDisk);
		final File partFile = new File(updateRoot, jarOnDisk + ".part");
		final long startedAt = System.currentTimeMillis();
		final AtomicInteger maxBytes = new AtomicInteger(-1);
		final AtomicInteger transferred = new AtomicInteger(0);
		final AtomicBoolean downloadFinished = new AtomicBoolean(false);
		final AtomicReference<Exception> downloadError = new AtomicReference<>(null);

		final BukkitTask[] hotbarTask = new BukkitTask[1];
		hotbarTask[0] = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
			Player p = Bukkit.getPlayer(playerId);
			if (p == null || !p.isOnline()) {
				hotbarTask[0].cancel();
				return;
			}

			Exception err = downloadError.get();
			if (err != null) {
				hotbarTask[0].cancel();
				ActionBarAPI.sendActionBar(p, "§cNao foi possivel baixar a atualizacao: " + err.getMessage());
				return;
			}

			long elapsed = System.currentTimeMillis() - startedAt;
			boolean done = downloadFinished.get();
			int max = maxBytes.get();
			int tr = transferred.get();

			if (done && elapsed >= DOWNLOAD_HOTBAR_MIN_MS) {
				hotbarTask[0].cancel();
				p.sendMessage("\n§aDownload concluido. Use /stop e suba o servidor para aplicar o jar.\n");
				return;
			}

			int percentage;
			if (max > 0) {
				percentage = (int) Math.min(99L, tr * 100L / max);
				if (done) {
					percentage = 99;
				}
			} else {
				percentage = (int) Math.min(95L, elapsed * 95L / DOWNLOAD_HOTBAR_MIN_MS);
				if (done) {
					percentage = 99;
				}
			}

			ActionBarAPI.sendActionBar(p, "§fBaixando §e" + jarLabel + " " + progressBarSegment(percentage));
		}, 0L, 2L);

		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
			try {
				if (!updateRoot.exists() && !updateRoot.mkdirs()) {
					throw new IOException("Nao foi possivel criar a pasta de updates: " + updateRoot.getAbsolutePath());
				}
				if (partFile.exists() && !partFile.delete()) {
					throw new IOException("Nao foi possivel limpar arquivo temporario: " + partFile.getAbsolutePath());
				}
				HttpsURLConnection connection = (HttpsURLConnection) new URL(this.downloadUrl).openConnection();
				connection.setRequestProperty("Accept", "application/octet-stream");
				connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
				connection.setRequestProperty("User-Agent", "paragonn-system-updater");
				if (githubApiUseAuthorization && githubToken != null && !githubToken.trim().isEmpty()) {
					connection.setRequestProperty("Authorization", "Bearer " + githubToken.trim());
				}

				int max = connection.getContentLength();
				maxBytes.set(max);

				try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
						BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(partFile), 8192)) {
					byte[] buf = new byte[8192];
					int read;
					while ((read = in.read(buf)) != -1) {
						bout.write(buf, 0, read);
						transferred.addAndGet(read);
					}
				}
				Files.move(partFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Bukkit.getScheduler().runTask(this.plugin, () ->
						logInfo("Download concluido; aplicacao na proxima subida do servidor (pasta update do Spigot)."));
			} catch (Exception ex) {
				downloadError.set(ex);
				if (partFile.exists()) {
					//noinspection ResultOfMethodCallIgnored
					partFile.delete();
				}
			} finally {
				downloadFinished.set(true);
			}
		});
	}
}
