package com.bryanmz.betterclans.clan;

import java.util.UUID;

public final class ClanMember {

    private final UUID playerUuid;
    private final UUID clanId;
    private ClanRole role;
    private final long joinedAt;
    private int kills;
    private int deaths;

    public ClanMember(UUID playerUuid, UUID clanId, ClanRole role, long joinedAt, int kills, int deaths) {
        this.playerUuid = playerUuid;
        this.clanId = clanId;
        this.role = role;
        this.joinedAt = joinedAt;
        this.kills = kills;
        this.deaths = deaths;
    }

    public UUID playerUuid() { return playerUuid; }
    public UUID clanId() { return clanId; }
    public ClanRole role() { return role; }
    public void setRole(ClanRole role) { this.role = role; }
    public long joinedAt() { return joinedAt; }
    public int kills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public void incrementKills() { this.kills++; }
    public int deaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void incrementDeaths() { this.deaths++; }
}
