package com.bryanmz.betterclans.clan;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Convites pendentes in-memory. Expiram apos N minutos (nao persistem entre restarts).
 */
public final class InviteRegistry {

    private static final long EXPIRY_MS = 5 * 60 * 1000L;

    private final ConcurrentHashMap<String, Long> invites = new ConcurrentHashMap<>(); // "playerUuid|clanId" -> createdAt

    public void invite(UUID player, UUID clanId) {
        invites.put(key(player, clanId), System.currentTimeMillis());
    }

    public boolean consume(UUID player, UUID clanId) {
        Long at = invites.remove(key(player, clanId));
        return at != null && System.currentTimeMillis() - at <= EXPIRY_MS;
    }

    public boolean has(UUID player, UUID clanId) {
        Long at = invites.get(key(player, clanId));
        return at != null && System.currentTimeMillis() - at <= EXPIRY_MS;
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        invites.entrySet().removeIf(e -> now - e.getValue() > EXPIRY_MS);
    }

    private String key(UUID a, UUID b) { return a + "|" + b; }
}
