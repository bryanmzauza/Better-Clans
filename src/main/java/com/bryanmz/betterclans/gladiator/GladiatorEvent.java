package com.bryanmz.betterclans.gladiator;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class GladiatorEvent {

    public enum State { IDLE, SIGNUP_OPEN, PRE_MATCH, RUNNING, ENDED }

    private final BetterClansPlugin plugin;
    private final Set<UUID> registeredClans = new HashSet<>();
    private final Set<UUID> aliveParticipants = new HashSet<>();

    /** player -> clan id (snapshot no inicio do match). */
    private final Map<UUID, UUID> clanByPlayer = new HashMap<>();

    /** Ordem de eliminacao dos clas: primeiro elemento eliminado primeiro. */
    private final List<UUID> eliminationOrder = new ArrayList<>();

    /** Origin teleport por participante. */
    private final Map<UUID, Location> origins = new HashMap<>();

    private State state = State.IDLE;

    public GladiatorEvent(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public State state() { return state; }
    public Set<UUID> registeredClans() { return registeredClans; }
    public Set<UUID> aliveParticipants() { return aliveParticipants; }

    public void openSignups() {
        reset();
        state = State.SIGNUP_OPEN;
        Bukkit.broadcast(plugin.messages().get("gladiator.signup.opened"));
    }

    public void closeSignups() {
        if (state == State.SIGNUP_OPEN) {
            Bukkit.broadcast(plugin.messages().get("gladiator.signup.closed"));
        }
    }

    public boolean registerClan(UUID clanId) {
        if (state != State.SIGNUP_OPEN) return false;
        int max = plugin.getConfig().getInt("gladiator.max-clans", 16);
        if (registeredClans.size() >= max) return false;
        return registeredClans.add(clanId);
    }

    public boolean unregisterClan(UUID clanId) {
        return registeredClans.remove(clanId);
    }

    public void startMatch() {
        if (registeredClans.size() < plugin.getConfig().getInt("gladiator.min-clans", 2)) {
            Bukkit.broadcast(plugin.messages().get("gladiator.announce.no-clans"));
            reset();
            return;
        }

        state = State.PRE_MATCH;
        List<UUID> spawnIndexes = new ArrayList<>(registeredClans);
        Collections.shuffle(spawnIndexes);

        ConfigurationSection spawns = plugin.getConfig().getConfigurationSection("gladiator.spawns");
        List<Location> locs = loadSpawns();
        if (locs.isEmpty()) {
            Bukkit.broadcast(plugin.messages().get("gladiator.no-spawns"));
            reset();
            return;
        }

        List<ItemStack> kit = buildKit();
        int idx = 0;
        for (UUID clanId : spawnIndexes) {
            Location spawn = locs.get(idx % locs.size());
            idx++;
            for (ClanMember m : plugin.clans().membersOf(clanId)) {
                Player p = Bukkit.getPlayer(m.playerUuid());
                if (p == null) continue;
                origins.put(p.getUniqueId(), p.getLocation());
                clanByPlayer.put(p.getUniqueId(), clanId);
                aliveParticipants.add(p.getUniqueId());
                p.teleport(spawn);
                if (!kit.isEmpty()) {
                    p.getInventory().clear();
                    for (ItemStack it : kit) p.getInventory().addItem(it.clone());
                }
                int immunity = plugin.getConfig().getInt("gladiator.pre-match-immunity-seconds", 10);
                if (immunity > 0) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, immunity * 20, 4, true, false));
                }
            }
        }

        Bukkit.broadcast(plugin.messages().get("gladiator.announce.start"));
        state = State.RUNNING;

        int timeoutMin = plugin.getConfig().getInt("gladiator.match-timeout-minutes", 20);
        Bukkit.getScheduler().runTaskLater(plugin, this::forceEnd, timeoutMin * 60L * 20L);
    }

    public void onParticipantDeath(UUID player) {
        if (state != State.RUNNING) return;
        if (!aliveParticipants.remove(player)) return;

        UUID clanId = clanByPlayer.get(player);
        if (clanId == null) return;
        boolean clanHasAlive = aliveParticipants.stream().anyMatch(uuid -> clanId.equals(clanByPlayer.get(uuid)));
        if (!clanHasAlive) {
            eliminationOrder.add(clanId);
            Set<UUID> remainingClans = new HashSet<>();
            for (UUID alive : aliveParticipants) remainingClans.add(clanByPlayer.get(alive));
            if (remainingClans.size() <= 1) {
                UUID winnerClan = remainingClans.stream().findFirst().orElse(null);
                endMatch(winnerClan);
            }
        }
    }

    public void onParticipantQuit(UUID player) {
        if (state == State.RUNNING && aliveParticipants.contains(player)) {
            Player p = Bukkit.getPlayer(player);
            if (p != null) p.setHealth(0);
            onParticipantDeath(player);
        }
    }

    private void endMatch(UUID winnerClan) {
        state = State.ENDED;
        Clan clan = winnerClan == null ? null : plugin.clans().getById(winnerClan).orElse(null);
        if (clan != null) {
            Bukkit.broadcast(plugin.messages().get("gladiator.announce.winner", "tag", clan.tag()));
            clan.incrementWins();
            applyRewards(clan, 1);
            plugin.clans().saveClan(clan);
        }

        // Segundo / terceiro
        UUID second = eliminationOrder.isEmpty() ? null : eliminationOrder.get(eliminationOrder.size() - 1);
        UUID third = eliminationOrder.size() >= 2 ? eliminationOrder.get(eliminationOrder.size() - 2) : null;
        if (second != null) plugin.clans().getById(second).ifPresent(c -> { applyRewards(c, 2); plugin.clans().saveClan(c); });
        if (third != null) plugin.clans().getById(third).ifPresent(c -> { applyRewards(c, 3); plugin.clans().saveClan(c); });

        // Historico
        int participants = clanByPlayer.size();
        long weekStart = System.currentTimeMillis();
        plugin.database().dao().logGladiator(weekStart, winnerClan, second, third, participants);

        // Teleporta todos de volta
        Location ret = loadLocation(plugin.getConfig().getConfigurationSection("gladiator.return-spawn"));
        for (UUID id : clanByPlayer.keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p == null) continue;
            Location dest = ret != null ? ret : origins.get(id);
            if (dest != null) p.teleport(dest);
        }

        new BukkitRunnable() {
            @Override public void run() { reset(); }
        }.runTaskLater(plugin, 200L);
    }

    private void forceEnd() {
        if (state != State.RUNNING) return;
        Set<UUID> clans = new HashSet<>();
        for (UUID alive : aliveParticipants) clans.add(clanByPlayer.get(alive));
        UUID winner = clans.size() == 1 ? clans.iterator().next() : null;
        endMatch(winner);
    }

    private void applyRewards(Clan clan, int position) {
        String path = switch (position) { case 1 -> "first"; case 2 -> "second"; default -> "third"; };
        long xp = plugin.getConfig().getLong("gladiator.rewards." + path + ".xp", 0);
        double money = plugin.getConfig().getDouble("gladiator.rewards." + path + ".money", 0);
        if (xp > 0) clan.addXp(xp);
        if (money > 0 && plugin.vault().hasEconomy()) {
            // Entrega ao lider
            plugin.vault().deposit(Bukkit.getOfflinePlayer(clan.leaderUuid()), money);
        }
    }

    public void reset() {
        registeredClans.clear();
        aliveParticipants.clear();
        clanByPlayer.clear();
        origins.clear();
        eliminationOrder.clear();
        state = State.IDLE;
    }

    public List<Location> loadSpawns() {
        List<Location> out = new ArrayList<>();
        List<Map<?, ?>> raw = plugin.getConfig().getMapList("gladiator.spawns");
        for (Map<?, ?> m : raw) {
            String world = (String) m.get("world");
            if (world == null) continue;
            var w = Bukkit.getWorld(world);
            if (w == null) continue;
            double x = ((Number) m.get("x")).doubleValue();
            double y = ((Number) m.get("y")).doubleValue();
            double z = ((Number) m.get("z")).doubleValue();
            out.add(new Location(w, x, y, z));
        }
        return out;
    }

    private Location loadLocation(ConfigurationSection section) {
        if (section == null) return null;
        String world = section.getString("world");
        if (world == null) return null;
        var w = Bukkit.getWorld(world);
        if (w == null) return null;
        return new Location(w, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                (float) section.getDouble("yaw", 0), (float) section.getDouble("pitch", 0));
    }

    private List<ItemStack> buildKit() {
        List<ItemStack> out = new ArrayList<>();
        if (!plugin.getConfig().getBoolean("gladiator.kit.enabled", true)) return out;
        for (String entry : plugin.getConfig().getStringList("gladiator.kit.items")) {
            String[] parts = entry.split(":");
            Material mat = Material.matchMaterial(parts[0]);
            if (mat == null) continue;
            int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            out.add(new ItemStack(mat, amount));
        }
        return out;
    }

    public boolean isParticipant(UUID player) {
        return aliveParticipants.contains(player) || clanByPlayer.containsKey(player);
    }
}
