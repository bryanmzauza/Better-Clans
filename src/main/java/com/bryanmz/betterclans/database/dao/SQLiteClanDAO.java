package com.bryanmz.betterclans.database.dao;

import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Stub SQLite. Implementacao completa na Fase 1.
 */
public final class SQLiteClanDAO implements ClanDAO {

    private final DataSource dataSource;

    public SQLiteClanDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override public CompletableFuture<Void> save(Clan clan) { return CompletableFuture.completedFuture(null); }
    @Override public CompletableFuture<Optional<Clan>> findById(UUID id) { return CompletableFuture.completedFuture(Optional.empty()); }
    @Override public CompletableFuture<Optional<Clan>> findByTag(String tag) { return CompletableFuture.completedFuture(Optional.empty()); }
    @Override public CompletableFuture<List<Clan>> findAll() { return CompletableFuture.completedFuture(List.of()); }
    @Override public CompletableFuture<Void> delete(UUID clanId) { return CompletableFuture.completedFuture(null); }
    @Override public CompletableFuture<Void> saveMember(ClanMember member) { return CompletableFuture.completedFuture(null); }
    @Override public CompletableFuture<Optional<ClanMember>> findMember(UUID playerUuid) { return CompletableFuture.completedFuture(Optional.empty()); }
    @Override public CompletableFuture<List<ClanMember>> membersOf(UUID clanId) { return CompletableFuture.completedFuture(List.of()); }
    @Override public CompletableFuture<Void> removeMember(UUID playerUuid) { return CompletableFuture.completedFuture(null); }
}
