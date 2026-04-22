package com.bryanmz.betterclans.clan;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Propostas de alianca pendentes (precisa aceite do outro lider).
 */
public final class AllyProposalRegistry {

    private static final long EXPIRY_MS = 5 * 60 * 1000L;

    private final ConcurrentHashMap<String, Long> proposals = new ConcurrentHashMap<>();

    public void propose(UUID fromClan, UUID toClan) {
        proposals.put(fromClan + "|" + toClan, System.currentTimeMillis());
    }

    public boolean consume(UUID toClan, UUID fromClan) {
        Long at = proposals.remove(fromClan + "|" + toClan);
        return at != null && System.currentTimeMillis() - at <= EXPIRY_MS;
    }
}
