package com.bryanmz.betterclans.clan;

import java.util.UUID;

/**
 * Modelo imutavel de cla persistido. Stats agregados sao atualizados via StatsService.
 */
public record Clan(
        UUID id,
        String tag,
        String name,
        UUID leaderUuid,
        long foundedAt,
        int level,
        long xp,
        String tagColor,
        String motd,
        int kills,
        int deaths,
        int wins
) {
}
