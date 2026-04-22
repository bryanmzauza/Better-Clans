package com.bryanmz.betterclans.clan;

import java.util.UUID;

public record ClanMember(
        UUID playerUuid,
        UUID clanId,
        ClanRole role,
        long joinedAt,
        int kills,
        int deaths
) {
}
