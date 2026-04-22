package com.bryanmz.betterclans.database.dao;

import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Stub MySQL. Implementacao completa na Fase 1.
 */
public final class MySQLClanDAO implements ClanDAO {

    private final DataSource dataSource;

    public MySQLClanDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CompletableFuture<Void> save(Clan clan) {
        // TODO Fase 1: INSERT ... ON DUPLICATE KEY UPDATE em bc_clan
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Optional<Clan>> findById(UUID id) {
        // TODO Fase 1
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Optional<Clan>> findByTag(String tag) {
        // TODO Fase 1
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<List<Clan>> findAll() {
        // TODO Fase 1
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Void> delete(UUID clanId) {
        // TODO Fase 1
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> saveMember(ClanMember member) {
        // TODO Fase 1
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Optional<ClanMember>> findMember(UUID playerUuid) {
        // TODO Fase 1
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<List<ClanMember>> membersOf(UUID clanId) {
        // TODO Fase 1
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Void> removeMember(UUID playerUuid) {
        // TODO Fase 1
        return CompletableFuture.completedFuture(null);
    }
}
