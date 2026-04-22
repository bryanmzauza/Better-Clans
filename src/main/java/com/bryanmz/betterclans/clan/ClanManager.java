package com.bryanmz.betterclans.clan;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.database.dao.SqlClanDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache em memoria + fachada para o DAO (write-through).
 */
public final class ClanManager {

    private final BetterClansPlugin plugin;
    private final SqlClanDAO dao;

    private final ConcurrentHashMap<UUID, Clan> byId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> byTag = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ClanMember> memberByPlayer = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<UUID>> membersByClan = new ConcurrentHashMap<>();

    /** Relacoes indexadas por "uuidA|uuidB" onde A < B. */
    private final ConcurrentHashMap<String, ClanRelation> relations = new ConcurrentHashMap<>();

    private final InviteRegistry invites = new InviteRegistry();
    private final AllyProposalRegistry allyProposals = new AllyProposalRegistry();

    /** Modos de chat ativos: player -> "clan" ou "ally". */
    private final ConcurrentHashMap<UUID, String> chatMode = new ConcurrentHashMap<>();

    public ClanManager(BetterClansPlugin plugin, SqlClanDAO dao) {
        this.plugin = plugin;
        this.dao = dao;
    }

    public InviteRegistry invites() { return invites; }
    public AllyProposalRegistry allyProposals() { return allyProposals; }

    public String chatMode(UUID player) { return chatMode.get(player); }
    public void setChatMode(UUID player, String mode) {
        if (mode == null) chatMode.remove(player); else chatMode.put(player, mode);
    }

    public void loadAll() {
        dao.findAllClans().thenAccept(list -> {
            list.forEach(c -> {
                byId.put(c.id(), c);
                byTag.put(c.tag().toUpperCase(Locale.ROOT), c.id());
            });
            plugin.getLogger().info("Carregados " + list.size() + " clas.");
        }).join();

        dao.findAllMembers().thenAccept(list -> {
            list.forEach(m -> {
                memberByPlayer.put(m.playerUuid(), m);
                membersByClan.computeIfAbsent(m.clanId(), k -> new ArrayList<>()).add(m.playerUuid());
            });
            plugin.getLogger().info("Carregados " + list.size() + " membros.");
        }).join();

        dao.loadAllRelations().thenAccept(map -> {
            for (Map.Entry<String, ClanRelation> e : map.entrySet()) relations.put(e.getKey(), e.getValue());
            plugin.getLogger().info("Carregadas " + map.size() + " relacoes.");
        }).join();
    }

    public void flush() {
        byId.values().forEach(c -> dao.upsertClan(c).join());
        memberByPlayer.values().forEach(m -> dao.upsertMember(m).join());
    }

    // -------- queries --------

    public Collection<Clan> all() { return byId.values(); }

    public Optional<Clan> getByTag(String tag) {
        if (tag == null) return Optional.empty();
        UUID id = byTag.get(tag.toUpperCase(Locale.ROOT));
        return id == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }

    public Optional<Clan> getById(UUID id) { return Optional.ofNullable(byId.get(id)); }

    public Optional<ClanMember> getMember(UUID playerUuid) {
        return Optional.ofNullable(memberByPlayer.get(playerUuid));
    }

    public Optional<Clan> getClanOf(UUID playerUuid) {
        return getMember(playerUuid).flatMap(m -> getById(m.clanId()));
    }

    public List<ClanMember> membersOf(UUID clanId) {
        List<UUID> ids = membersByClan.getOrDefault(clanId, List.of());
        List<ClanMember> out = new ArrayList<>(ids.size());
        for (UUID id : ids) {
            ClanMember m = memberByPlayer.get(id);
            if (m != null) out.add(m);
        }
        return out;
    }

    public ClanRelation relationBetween(UUID a, UUID b) {
        if (a.equals(b)) return ClanRelation.NEUTRAL;
        String key = a.compareTo(b) <= 0 ? a + "|" + b : b + "|" + a;
        return relations.getOrDefault(key, ClanRelation.NEUTRAL);
    }

    // -------- commands --------

    public boolean tagExists(String tag) {
        return byTag.containsKey(tag.toUpperCase(Locale.ROOT));
    }

    public boolean nameExists(String name) {
        return byId.values().stream().anyMatch(c -> c.name().equalsIgnoreCase(name));
    }

    public CompletableFuture<Clan> create(String tag, String name, UUID leader) {
        String up = tag.toUpperCase(Locale.ROOT);
        Clan clan = new Clan(UUID.randomUUID(), up, name, leader, System.currentTimeMillis(),
                1, 0L, "white", null, 0, 0, 0);
        ClanMember leaderMember = new ClanMember(leader, clan.id(), ClanRole.LEADER,
                System.currentTimeMillis(), 0, 0);

        byId.put(clan.id(), clan);
        byTag.put(up, clan.id());
        memberByPlayer.put(leader, leaderMember);
        membersByClan.computeIfAbsent(clan.id(), k -> new ArrayList<>()).add(leader);

        return dao.upsertClan(clan)
                .thenCompose(v -> dao.upsertMember(leaderMember))
                .thenApply(v -> clan);
    }

    public CompletableFuture<Void> disband(UUID clanId) {
        Clan c = byId.remove(clanId);
        if (c != null) byTag.remove(c.tag().toUpperCase(Locale.ROOT));
        List<UUID> members = membersByClan.remove(clanId);
        if (members != null) members.forEach(memberByPlayer::remove);
        // remove relations envolvendo o cla
        relations.keySet().removeIf(k -> k.contains(clanId.toString()));
        return dao.deleteClan(clanId);
    }

    public CompletableFuture<Void> addMember(Clan clan, UUID playerUuid, ClanRole role) {
        ClanMember m = new ClanMember(playerUuid, clan.id(), role, System.currentTimeMillis(), 0, 0);
        memberByPlayer.put(playerUuid, m);
        membersByClan.computeIfAbsent(clan.id(), k -> new ArrayList<>()).add(playerUuid);
        return dao.upsertMember(m);
    }

    public CompletableFuture<Void> removeMember(UUID playerUuid) {
        ClanMember m = memberByPlayer.remove(playerUuid);
        if (m != null) {
            List<UUID> list = membersByClan.get(m.clanId());
            if (list != null) list.remove(playerUuid);
        }
        return dao.removeMember(playerUuid);
    }

    public CompletableFuture<Void> saveClan(Clan clan) {
        return dao.upsertClan(clan);
    }

    public CompletableFuture<Void> saveMember(ClanMember member) {
        return dao.upsertMember(member);
    }

    public CompletableFuture<Void> setRelation(UUID a, UUID b, ClanRelation type) {
        String key = a.compareTo(b) <= 0 ? a + "|" + b : b + "|" + a;
        if (type == ClanRelation.NEUTRAL) relations.remove(key);
        else relations.put(key, type);
        return dao.setRelation(a, b, type);
    }

    public SqlClanDAO dao() { return dao; }
}
