package com.bryanmz.betterclans.database.dao;

import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * DAO async para cla e membros. Implementacoes: MySQL / SQLite.
 */
public interface ClanDAO {

    CompletableFuture<Void> save(Clan clan);

    CompletableFuture<Optional<Clan>> findById(UUID id);

    CompletableFuture<Optional<Clan>> findByTag(String tag);

    CompletableFuture<List<Clan>> findAll();

    CompletableFuture<Void> delete(UUID clanId);

    CompletableFuture<Void> saveMember(ClanMember member);

    CompletableFuture<Optional<ClanMember>> findMember(UUID playerUuid);

    CompletableFuture<List<ClanMember>> membersOf(UUID clanId);

    CompletableFuture<Void> removeMember(UUID playerUuid);
}
