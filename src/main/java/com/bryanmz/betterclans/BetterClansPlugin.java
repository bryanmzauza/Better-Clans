package com.bryanmz.betterclans;

import com.bryanmz.betterclans.clan.ClanManager;
import com.bryanmz.betterclans.commands.AllyChatCommand;
import com.bryanmz.betterclans.commands.ClanChatCommand;
import com.bryanmz.betterclans.commands.ClanCommand;
import com.bryanmz.betterclans.commands.DuelCommand;
import com.bryanmz.betterclans.commands.DuelAdminCommand;
import com.bryanmz.betterclans.commands.GladiatorCommand;
import com.bryanmz.betterclans.database.DatabaseManager;
import com.bryanmz.betterclans.duel.DuelManager;
import com.bryanmz.betterclans.events.ChatListener;
import com.bryanmz.betterclans.events.PvPListener;
import com.bryanmz.betterclans.events.StatsListener;
import com.bryanmz.betterclans.gladiator.GladiatorEvent;
import com.bryanmz.betterclans.gladiator.GladiatorScheduler;
import com.bryanmz.betterclans.hooks.LuckPermsHook;
import com.bryanmz.betterclans.hooks.NChatHook;
import com.bryanmz.betterclans.hooks.PlaceholderAPIHook;
import com.bryanmz.betterclans.hooks.VaultHook;
import com.bryanmz.betterclans.menu.MenuListener;
import com.bryanmz.betterclans.menu.MenuService;
import com.bryanmz.betterclans.nametag.NametagManager;
import com.bryanmz.betterclans.nametag.ProtocolLibAdapter;
import com.bryanmz.betterclans.nametag.ScoreboardNametagProvider;
import com.bryanmz.betterclans.stats.StatsService;
import com.bryanmz.betterclans.util.Messages;
import com.bryanmz.betterclans.util.TagValidator;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Objects;

public final class BetterClansPlugin extends JavaPlugin {

    private static BetterClansPlugin instance;

    private Messages messages;
    private TagValidator tagValidator;
    private DatabaseManager database;
    private ClanManager clans;
    private StatsService stats;
    private DuelManager duels;
    private GladiatorEvent gladiator;
    private GladiatorScheduler gladiatorScheduler;
    private NametagManager nametag;

    private VaultHook vault;
    private NChatHook nChat;
    private LuckPermsHook luckPerms;
    private MenuService menus;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        // i18n
        this.messages = new Messages(this);
        messages.load(getConfig().getString("language", "pt_BR"));

        // Tag validator
        this.tagValidator = new TagValidator(new HashSet<>(
                getConfig().getStringList("clan.tag.reserved")));

        // Banco
        this.database = new DatabaseManager(this);
        try {
            database.init();
        } catch (Exception ex) {
            getLogger().severe("Falha ao inicializar banco de dados: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Dominio
        this.clans = new ClanManager(this, database.clans());
        clans.loadAll();

        // Servicos
        this.stats = new StatsService(this);
        this.duels = new DuelManager(this);
        this.gladiator = new GladiatorEvent(this);
        this.gladiatorScheduler = new GladiatorScheduler(this);
        if (getConfig().getBoolean("gladiator.enabled", true)) {
            gladiatorScheduler.start();
        }

        // Nametag
        this.nametag = resolveNametagProvider();

        // Hooks
        this.vault = new VaultHook(this);
        vault.setup();

        this.nChat = new NChatHook(this);
        nChat.detect();

        this.luckPerms = new LuckPermsHook(this);
        luckPerms.detect();

        if (PlaceholderAPIHook.isAvailable()) {
            new PlaceholderAPIHook(this).register();
            getLogger().info("Expansion PlaceholderAPI registrada.");
        }

        // Comandos
        bind("clan", new ClanCommand(this));
        bind("clanchat", new ClanChatCommand(this));
        bind("allychat", new AllyChatCommand(this));
        bind("x1", new DuelCommand(this));
        bind("x1admin", new DuelAdminCommand(this));
        bind("gladiator", new GladiatorCommand(this));

        // Tab completers
        setTabCompleter("clan");
        setTabCompleter("x1");
        setTabCompleter("x1admin");
        setTabCompleter("gladiator");

        // Listeners
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PvPListener(this), this);
        getServer().getPluginManager().registerEvents(new StatsListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        // Menus (Java + Bedrock via Floodgate)
        this.menus = new MenuService(this);
        if (getServer().getPluginManager().getPlugin("floodgate") != null) {
            getLogger().info("Floodgate detectado - menus Bedrock habilitados.");
        }

        getLogger().info("BetterClans habilitado (v" + getPluginMeta().getVersion() + ").");
    }

    @Override
    public void onDisable() {
        if (gladiatorScheduler != null) gladiatorScheduler.shutdown();
        if (duels != null) duels.shutdown();
        if (stats != null) stats.flush();
        if (clans != null) clans.flush();
        if (nametag != null) nametag.shutdown();
        if (database != null) database.shutdown();
        instance = null;
    }

    private NametagManager resolveNametagProvider() {
        String provider = getConfig().getString("nametag.provider", "scoreboard");
        if ("protocollib".equalsIgnoreCase(provider) && ProtocolLibAdapter.isAvailable()) {
            return new ProtocolLibAdapter(this);
        }
        return new ScoreboardNametagProvider(this);
    }

    private void bind(String name, org.bukkit.command.CommandExecutor executor) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) {
            getLogger().warning("Comando nao registrado no plugin.yml: " + name);
            return;
        }
        cmd.setExecutor(executor);
    }

    private void setTabCompleter(String name) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) return;
        if (cmd.getExecutor() instanceof org.bukkit.command.TabCompleter tc) {
            cmd.setTabCompleter(tc);
        }
    }

    // -------- getters ----------

    public static BetterClansPlugin getInstance() {
        return Objects.requireNonNull(instance, "BetterClansPlugin nao esta habilitado");
    }

    public Messages messages() { return messages; }
    public TagValidator tagValidator() { return tagValidator; }
    public DatabaseManager database() { return database; }
    public ClanManager clans() { return clans; }
    public StatsService stats() { return stats; }
    public DuelManager duels() { return duels; }
    public GladiatorEvent gladiator() { return gladiator; }
    public NametagManager nametag() { return nametag; }
    public VaultHook vault() { return vault; }
    public LuckPermsHook luckPerms() { return luckPerms; }
    public MenuService menus() { return menus; }
}
