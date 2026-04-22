package com.bryanmz.betterclans.database.dao;

import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRelation;
import com.bryanmz.betterclans.clan.ClanRole;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Unica implementacao SQL para MySQL e SQLite (as queries usam subset compativel).
 */
public final class SqlClanDAO {

    private final DataSource ds;
    private final Executor executor;

    public SqlClanDAO(DataSource ds, Executor executor) {
        this.ds = ds;
        this.executor = executor;
    }

    // -------------------- Clan --------------------

    public CompletableFuture<Void> upsertClan(Clan clan) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO bc_clan(id, tag, name, leader_uuid, founded_at, level, xp, tag_color, motd, kills, deaths, wins) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            String upd = "UPDATE bc_clan SET tag=?, name=?, leader_uuid=?, level=?, xp=?, tag_color=?, motd=?, kills=?, deaths=?, wins=? WHERE id=?";
            try (Connection c = ds.getConnection()) {
                try (PreparedStatement up = c.prepareStatement(upd)) {
                    up.setString(1, clan.tag());
                    up.setString(2, clan.name());
                    up.setString(3, clan.leaderUuid().toString());
                    up.setInt(4, clan.level());
                    up.setLong(5, clan.xp());
                    up.setString(6, clan.tagColor());
                    up.setString(7, clan.motd());
                    up.setInt(8, clan.kills());
                    up.setInt(9, clan.deaths());
                    up.setInt(10, clan.wins());
                    up.setString(11, clan.id().toString());
                    if (up.executeUpdate() > 0) return;
                }
                try (PreparedStatement ins = c.prepareStatement(sql)) {
                    ins.setString(1, clan.id().toString());
                    ins.setString(2, clan.tag());
                    ins.setString(3, clan.name());
                    ins.setString(4, clan.leaderUuid().toString());
                    ins.setLong(5, clan.foundedAt());
                    ins.setInt(6, clan.level());
                    ins.setLong(7, clan.xp());
                    ins.setString(8, clan.tagColor());
                    ins.setString(9, clan.motd());
                    ins.setInt(10, clan.kills());
                    ins.setInt(11, clan.deaths());
                    ins.setInt(12, clan.wins());
                    ins.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao salvar cla", e);
            }
        }, executor);
    }

    public CompletableFuture<List<Clan>> findAllClans() {
        return CompletableFuture.supplyAsync(() -> {
            List<Clan> out = new ArrayList<>();
            try (Connection c = ds.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT * FROM bc_clan");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(readClan(rs));
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao carregar clas", e);
            }
            return out;
        }, executor);
    }

    public CompletableFuture<Void> deleteClan(UUID id) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = ds.getConnection();
                 PreparedStatement ps = c.prepareStatement("DELETE FROM bc_clan WHERE id=?")) {
                ps.setString(1, id.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao apagar cla", e);
            }
        }, executor);
    }

    private Clan readClan(ResultSet rs) throws SQLException {
        return new Clan(
                UUID.fromString(rs.getString("id")),
                rs.getString("tag"),
                rs.getString("name"),
                UUID.fromString(rs.getString("leader_uuid")),
                rs.getLong("founded_at"),
                rs.getInt("level"),
                rs.getLong("xp"),
                rs.getString("tag_color"),
                rs.getString("motd"),
                rs.getInt("kills"),
                rs.getInt("deaths"),
                rs.getInt("wins")
        );
    }

    // -------------------- Members --------------------

    public CompletableFuture<Void> upsertMember(ClanMember m) {
        return CompletableFuture.runAsync(() -> {
            String upd = "UPDATE bc_member SET clan_id=?, role=?, kills=?, deaths=? WHERE player_uuid=?";
            String ins = "INSERT INTO bc_member(player_uuid, clan_id, role, joined_at, kills, deaths) VALUES(?,?,?,?,?,?)";
            try (Connection c = ds.getConnection()) {
                try (PreparedStatement up = c.prepareStatement(upd)) {
                    up.setString(1, m.clanId().toString());
                    up.setString(2, m.role().name());
                    up.setInt(3, m.kills());
                    up.setInt(4, m.deaths());
                    up.setString(5, m.playerUuid().toString());
                    if (up.executeUpdate() > 0) return;
                }
                try (PreparedStatement p = c.prepareStatement(ins)) {
                    p.setString(1, m.playerUuid().toString());
                    p.setString(2, m.clanId().toString());
                    p.setString(3, m.role().name());
                    p.setLong(4, m.joinedAt());
                    p.setInt(5, m.kills());
                    p.setInt(6, m.deaths());
                    p.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao salvar membro", e);
            }
        }, executor);
    }

    public CompletableFuture<List<ClanMember>> findAllMembers() {
        return CompletableFuture.supplyAsync(() -> {
            List<ClanMember> out = new ArrayList<>();
            try (Connection c = ds.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT * FROM bc_member");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new ClanMember(
                            UUID.fromString(rs.getString("player_uuid")),
                            UUID.fromString(rs.getString("clan_id")),
                            ClanRole.valueOf(rs.getString("role")),
                            rs.getLong("joined_at"),
                            rs.getInt("kills"),
                            rs.getInt("deaths")));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao carregar membros", e);
            }
            return out;
        }, executor);
    }

    public CompletableFuture<Void> removeMember(UUID playerUuid) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = ds.getConnection();
                 PreparedStatement ps = c.prepareStatement("DELETE FROM bc_member WHERE player_uuid=?")) {
                ps.setString(1, playerUuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao remover membro", e);
            }
        }, executor);
    }

    // -------------------- Relations --------------------

    public CompletableFuture<Void> setRelation(UUID a, UUID b, ClanRelation type) {
        return CompletableFuture.runAsync(() -> {
            UUID[] ord = order(a, b);
            try (Connection c = ds.getConnection()) {
                if (type == ClanRelation.NEUTRAL) {
                    try (PreparedStatement p = c.prepareStatement("DELETE FROM bc_relation WHERE clan_a_id=? AND clan_b_id=?")) {
                        p.setString(1, ord[0].toString());
                        p.setString(2, ord[1].toString());
                        p.executeUpdate();
                    }
                    return;
                }
                try (PreparedStatement up = c.prepareStatement("UPDATE bc_relation SET type=? WHERE clan_a_id=? AND clan_b_id=?")) {
                    up.setString(1, type.name());
                    up.setString(2, ord[0].toString());
                    up.setString(3, ord[1].toString());
                    if (up.executeUpdate() > 0) return;
                }
                try (PreparedStatement ins = c.prepareStatement("INSERT INTO bc_relation(clan_a_id, clan_b_id, type, established_at) VALUES(?,?,?,?)")) {
                    ins.setString(1, ord[0].toString());
                    ins.setString(2, ord[1].toString());
                    ins.setString(3, type.name());
                    ins.setLong(4, System.currentTimeMillis());
                    ins.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao salvar relacao", e);
            }
        }, executor);
    }

    public CompletableFuture<Map<String, ClanRelation>> loadAllRelations() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, ClanRelation> out = new java.util.HashMap<>();
            try (Connection c = ds.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT clan_a_id, clan_b_id, type FROM bc_relation");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.put(rs.getString(1) + "|" + rs.getString(2), ClanRelation.valueOf(rs.getString(3)));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao carregar relacoes", e);
            }
            return out;
        }, executor);
    }

    // -------------------- Invites --------------------

    public CompletableFuture<Void> saveInvite(UUID player, UUID clan, UUID invitedBy) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = ds.getConnection();
                 PreparedStatement p = c.prepareStatement(
                         "INSERT INTO bc_invite(player_uuid, clan_id, invited_by, invited_at) VALUES(?,?,?,?)")) {
                p.setString(1, player.toString());
                p.setString(2, clan.toString());
                p.setString(3, invitedBy.toString());
                p.setLong(4, System.currentTimeMillis());
                p.executeUpdate();
            } catch (SQLException e) {
                // ignore conflict on duplicate
            }
        }, executor);
    }

    public CompletableFuture<Void> deleteInvite(UUID player, UUID clan) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = ds.getConnection();
                 PreparedStatement p = c.prepareStatement("DELETE FROM bc_invite WHERE player_uuid=? AND clan_id=?")) {
                p.setString(1, player.toString());
                p.setString(2, clan.toString());
                p.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    // -------------------- Duel log --------------------

    public CompletableFuture<Void> logDuel(UUID winner, UUID loser, String type) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = ds.getConnection();
                 PreparedStatement p = c.prepareStatement(
                         "INSERT INTO bc_duel_log(winner_uuid, loser_uuid, fought_at, duel_type) VALUES(?,?,?,?)")) {
                p.setString(1, winner.toString());
                p.setString(2, loser.toString());
                p.setLong(3, System.currentTimeMillis());
                p.setString(4, type);
                p.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    public CompletableFuture<int[]> duelStats(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            int[] out = new int[2]; // wins, losses
            try (Connection c = ds.getConnection()) {
                try (PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM bc_duel_log WHERE winner_uuid=?")) {
                    p.setString(1, player.toString());
                    try (ResultSet rs = p.executeQuery()) { if (rs.next()) out[0] = rs.getInt(1); }
                }
                try (PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM bc_duel_log WHERE loser_uuid=?")) {
                    p.setString(1, player.toString());
                    try (ResultSet rs = p.executeQuery()) { if (rs.next()) out[1] = rs.getInt(1); }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return out;
        }, executor);
    }

    // -------------------- Gladiator history --------------------

    public CompletableFuture<Void> logGladiator(long weekStart, UUID winner, UUID runnerUp, UUID third, int participants) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = ds.getConnection();
                 PreparedStatement p = c.prepareStatement(
                         "INSERT INTO bc_gladiator_history(week_start, winner_clan_id, runner_up_id, third_place_id, participants) VALUES(?,?,?,?,?)")) {
                p.setLong(1, weekStart);
                p.setString(2, winner == null ? null : winner.toString());
                p.setString(3, runnerUp == null ? null : runnerUp.toString());
                p.setString(4, third == null ? null : third.toString());
                p.setInt(5, participants);
                p.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    public CompletableFuture<List<String>> recentGladiatorWinners(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> out = new ArrayList<>();
            try (Connection c = ds.getConnection();
                 PreparedStatement p = c.prepareStatement("SELECT week_start, winner_clan_id FROM bc_gladiator_history ORDER BY week_start DESC LIMIT ?")) {
                p.setInt(1, limit);
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        out.add(rs.getLong(1) + "|" + rs.getString(2));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return out;
        }, executor);
    }

    // -------------------- helpers --------------------

    public Optional<ClanMember> findMemberSync(UUID playerUuid) {
        try (Connection c = ds.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT * FROM bc_member WHERE player_uuid=?")) {
            p.setString(1, playerUuid.toString());
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ClanMember(
                            UUID.fromString(rs.getString("player_uuid")),
                            UUID.fromString(rs.getString("clan_id")),
                            ClanRole.valueOf(rs.getString("role")),
                            rs.getLong("joined_at"),
                            rs.getInt("kills"),
                            rs.getInt("deaths")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private UUID[] order(UUID a, UUID b) {
        return a.compareTo(b) <= 0 ? new UUID[]{a, b} : new UUID[]{b, a};
    }
}
