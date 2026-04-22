package com.bryanmz.betterclans.clan;

import java.util.UUID;

/**
 * Modelo de cla (mutavel para stats agregados; identidade imutavel via id final).
 */
public final class Clan {

    private final UUID id;
    private String tag;
    private String name;
    private UUID leaderUuid;
    private final long foundedAt;
    private int level;
    private long xp;
    private String tagColor;
    private String motd;
    private int kills;
    private int deaths;
    private int wins;

    public Clan(UUID id, String tag, String name, UUID leaderUuid, long foundedAt,
                int level, long xp, String tagColor, String motd,
                int kills, int deaths, int wins) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.leaderUuid = leaderUuid;
        this.foundedAt = foundedAt;
        this.level = level;
        this.xp = xp;
        this.tagColor = tagColor;
        this.motd = motd;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
    }

    public UUID id() { return id; }
    public String tag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String name() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID leaderUuid() { return leaderUuid; }
    public void setLeaderUuid(UUID leaderUuid) { this.leaderUuid = leaderUuid; }
    public long foundedAt() { return foundedAt; }
    public int level() { return level; }
    public void setLevel(int level) { this.level = level; }
    public long xp() { return xp; }
    public void setXp(long xp) { this.xp = xp; }
    public void addXp(long delta) { this.xp += delta; }
    public String tagColor() { return tagColor; }
    public void setTagColor(String tagColor) { this.tagColor = tagColor; }
    public String motd() { return motd; }
    public void setMotd(String motd) { this.motd = motd; }
    public int kills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public void incrementKills() { this.kills++; }
    public int deaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void incrementDeaths() { this.deaths++; }
    public int wins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
    public void incrementWins() { this.wins++; }

    public double kdRatio() {
        return deaths == 0 ? kills : kills / (double) deaths;
    }
}
