package com.bryanmz.betterclans.duel;

import java.util.UUID;

public record DuelSession(
        UUID challenger,
        UUID challenged,
        double bet,
        long createdAt
) {
}
