package com.bryanmz.betterclans.clan;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.database.dao.ClanDAO;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache em memoria + fachada para o DAO. Implementacao completa na Fase 1.
 */
public final class ClanManager {

    private final BetterClansPlugin plugin;
    private final ClanDAO dao;

    private final ConcurrentHashMap<UUID, Clan> byId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> byTag = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ClanMember> memberByPlayer = new ConcurrentHashMap<>();

    public ClanManager(BetterClansPlugin plugin, ClanDAO dao) {
        this.plugin = plugin;
        this.dao = dao;
    }

    public void loadAll() {
        // TODO Fase 1: carregar todos os clas via dao.findAll() e popular caches
    }

    public void flush() {
        // TODO Fase 1: persistir stats pendentes em batch
    }

    public Optional<Clan> getByTag(String tag) {
        UUID id = byTag.get(tag.toUpperCase());
        return id == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }

    public Optional<Clan> getById(UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<ClanMember> getMember(UUID playerUuid) {
        return Optional.ofNullable(memberByPlayer.get(playerUuid));
    }

    public Optional<Clan> getClanOf(UUID playerUuid) {
        return getMember(playerUuid).flatMap(m -> getById(m.clanId()));
    }

    public CompletableFuture<Clan> create(String tag, String name, UUID leader) {
        // TODO Fase 1: validar, persistir e atualizar caches
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Em breve"));
    }

    public CompletableFuture<Void> disband(UUID clanId) {
        // TODO Fase 1
        return CompletableFuture.completedFuture(null);
    }
}
