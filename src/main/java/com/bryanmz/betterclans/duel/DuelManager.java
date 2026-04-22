package com.bryanmz.betterclans.duel;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerencia desafios /x1: pendentes, aceitos e em andamento.
 */
public final class DuelManager {

    private final BetterClansPlugin plugin;

    /** Challenges pendentes: target -> session. */
    private final ConcurrentHashMap<UUID, DuelSession> pending = new ConcurrentHashMap<>();

    /** Duelos ativos: qualquer lado -> session. */
    private final ConcurrentHashMap<UUID, DuelSession> active = new ConcurrentHashMap<>();

    /** Cooldown por par ordenado "minUuid|maxUuid" -> expiresAt. */
    private final Map<String, Long> cooldowns = new HashMap<>();

    public DuelManager(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isInDuel(UUID player) {
        return active.containsKey(player);
    }

    public DuelSession activeSession(UUID player) {
        return active.get(player);
    }

    public void challenge(Player challenger, Player challenged, double bet) {
        if (challenger.equals(challenged)) {
            challenger.sendMessage(plugin.messages().get("duel.challenge.self"));
            return;
        }
        long cd = cooldownRemaining(challenger.getUniqueId(), challenged.getUniqueId());
        if (cd > 0) {
            challenger.sendMessage(plugin.messages().get("duel.cooldown", "time", String.valueOf(cd / 1000)));
            return;
        }
        if (bet > 0) {
            double min = plugin.getConfig().getDouble("duel.min-bet", 100);
            double max = plugin.getConfig().getDouble("duel.max-bet", 1_000_000);
            if (bet < min) { challenger.sendMessage(plugin.messages().get("duel.bet.too-low", "min", String.valueOf((long) min))); return; }
            if (bet > max) { challenger.sendMessage(plugin.messages().get("duel.bet.too-high", "max", String.valueOf((long) max))); return; }
            if (!plugin.vault().hasEconomy() || !plugin.vault().has(challenger, bet)) {
                challenger.sendMessage(plugin.messages().get("duel.bet.insufficient", "amount", String.valueOf((long) bet)));
                return;
            }
        }

        DuelSession session = new DuelSession(challenger.getUniqueId(), challenged.getUniqueId(), bet);
        pending.put(challenged.getUniqueId(), session);

        String betSuffix = bet > 0 ? " (aposta $" + (long) bet + ")" : "";
        challenger.sendMessage(plugin.messages().get("duel.challenge.sent", "player", challenged.getName()));
        challenged.sendMessage(plugin.messages().get("duel.challenge.received",
                "player", challenger.getName(), "bet_suffix", betSuffix));

        new BukkitRunnable() {
            @Override public void run() {
                DuelSession existing = pending.get(challenged.getUniqueId());
                if (existing == session && existing.state() == DuelSession.State.PENDING) {
                    pending.remove(challenged.getUniqueId());
                    Player a = plugin.getServer().getPlayer(session.challenger());
                    Player b = plugin.getServer().getPlayer(session.challenged());
                    if (a != null) a.sendMessage(plugin.messages().get("duel.challenge.expired"));
                    if (b != null) b.sendMessage(plugin.messages().get("duel.challenge.expired"));
                }
            }
        }.runTaskLater(plugin, 20L * 30);
    }

    public void accept(Player challenged) {
        DuelSession session = pending.remove(challenged.getUniqueId());
        if (session == null) { challenged.sendMessage(plugin.messages().get("duel.challenge.none")); return; }
        Player challenger = plugin.getServer().getPlayer(session.challenger());
        if (challenger == null) { challenged.sendMessage(plugin.messages().get("errors.player-offline", "player", "?")); return; }

        // apostas: retira de ambos agora
        if (session.bet() > 0 && plugin.vault().hasEconomy()) {
            if (!plugin.vault().withdraw(challenger, session.bet())) { challenged.sendMessage(plugin.messages().get("duel.bet.insufficient", "amount", String.valueOf((long) session.bet()))); return; }
            if (!plugin.vault().withdraw(challenged, session.bet())) {
                plugin.vault().deposit(challenger, session.bet()); // rollback
                challenged.sendMessage(plugin.messages().get("duel.bet.insufficient", "amount", String.valueOf((long) session.bet())));
                return;
            }
        }

        String mode = plugin.getConfig().getString("duel.mode", "arena");
        if ("arena".equalsIgnoreCase(mode)) {
            Location a = readLoc(plugin.getConfig().getConfigurationSection("duel.arena.spawn-a"));
            Location b = readLoc(plugin.getConfig().getConfigurationSection("duel.arena.spawn-b"));
            if (a == null || b == null) {
                challenged.sendMessage(plugin.messages().get("duel.arena-not-set"));
                challenger.sendMessage(plugin.messages().get("duel.arena-not-set"));
                if (session.bet() > 0 && plugin.vault().hasEconomy()) {
                    plugin.vault().deposit(challenger, session.bet());
                    plugin.vault().deposit(challenged, session.bet());
                }
                return;
            }
            session.setChallengerOrigin(challenger.getLocation());
            session.setChallengedOrigin(challenged.getLocation());
            challenger.teleport(a);
            challenged.teleport(b);
        } else {
            session.setChallengerOrigin(challenger.getLocation());
            session.setChallengedOrigin(challenged.getLocation());
        }

        active.put(session.challenger(), session);
        active.put(session.challenged(), session);
        session.setState(DuelSession.State.COUNTDOWN);

        challenger.sendMessage(plugin.messages().get("duel.challenge.accepted"));
        challenged.sendMessage(plugin.messages().get("duel.challenge.accepted"));

        int countdown = plugin.getConfig().getInt("duel.countdown-seconds", 5);
        new BukkitRunnable() {
            int remaining = countdown;
            @Override public void run() {
                if (remaining <= 0) {
                    session.setState(DuelSession.State.ACTIVE);
                    challenger.sendMessage(plugin.messages().get("duel.start"));
                    challenged.sendMessage(plugin.messages().get("duel.start"));
                    cancel();
                    return;
                }
                challenger.sendMessage(plugin.messages().get("duel.countdown", "seconds", String.valueOf(remaining)));
                challenged.sendMessage(plugin.messages().get("duel.countdown", "seconds", String.valueOf(remaining)));
                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void deny(Player challenged) {
        DuelSession session = pending.remove(challenged.getUniqueId());
        if (session == null) { challenged.sendMessage(plugin.messages().get("duel.challenge.none")); return; }
        Player challenger = plugin.getServer().getPlayer(session.challenger());
        if (challenger != null) challenger.sendMessage(plugin.messages().get("duel.challenge.denied"));
        challenged.sendMessage(plugin.messages().get("duel.challenge.denied"));
    }

    /** Chamado pelo StatsListener quando um participante morre. */
    public void onDeath(UUID deadPlayer) {
        DuelSession session = active.remove(deadPlayer);
        if (session == null) return;
        UUID winner = deadPlayer.equals(session.challenger()) ? session.challenged() : session.challenger();
        active.remove(winner);
        session.setState(DuelSession.State.ENDED);

        Player w = plugin.getServer().getPlayer(winner);
        Player l = plugin.getServer().getPlayer(deadPlayer);

        if (session.bet() > 0 && plugin.vault().hasEconomy() && w != null) {
            plugin.vault().deposit(w, session.bet() * 2);
        }

        // XP
        long xpPerDuel = plugin.getConfig().getLong("xp.per-duel-win", 3);
        plugin.clans().getClanOf(winner).ifPresent(c -> { c.addXp(xpPerDuel); c.incrementWins(); plugin.clans().saveClan(c); });

        // Log
        plugin.database().dao().logDuel(winner, deadPlayer, "x1");

        // Cooldown
        setCooldown(session.challenger(), session.challenged());

        // Return teleports
        if (w != null && session.challengerOrigin() != null && session.challengedOrigin() != null) {
            Location back = winner.equals(session.challenger()) ? session.challengerOrigin() : session.challengedOrigin();
            w.teleport(back);
            if (w != null) w.sendMessage(plugin.messages().get("duel.win", "player", w.getName()));
        }
        if (l != null) {
            Location back = deadPlayer.equals(session.challenger()) ? session.challengerOrigin() : session.challengedOrigin();
            if (back != null) l.teleport(back);
        }
    }

    public void shutdown() {
        pending.clear();
        active.clear();
    }

    private long cooldownRemaining(UUID a, UUID b) {
        String key = pairKey(a, b);
        Long exp = cooldowns.get(key);
        if (exp == null) return 0;
        long left = exp - System.currentTimeMillis();
        if (left <= 0) { cooldowns.remove(key); return 0; }
        return left;
    }

    private void setCooldown(UUID a, UUID b) {
        long seconds = plugin.getConfig().getLong("duel.cooldown-seconds", 180);
        cooldowns.put(pairKey(a, b), System.currentTimeMillis() + seconds * 1000);
    }

    private String pairKey(UUID a, UUID b) {
        return a.compareTo(b) <= 0 ? a + "|" + b : b + "|" + a;
    }

    private Location readLoc(ConfigurationSection section) {
        if (section == null) return null;
        String world = section.getString("world");
        if (world == null) return null;
        var w = plugin.getServer().getWorld(world);
        if (w == null) return null;
        return new Location(w, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                (float) section.getDouble("yaw", 0), (float) section.getDouble("pitch", 0));
    }
}
