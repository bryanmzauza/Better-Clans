package com.bryanmz.betterclans.stats;

import com.bryanmz.betterclans.BetterClansPlugin;

import java.util.UUID;

/**
 * Agrega K/D, duelos e vitorias. Flush em batch na Fase 2.
 */
public final class StatsService {

    private final BetterClansPlugin plugin;

    public StatsService(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void recordKill(UUID killer, UUID victim) {
        // TODO Fase 2
    }

    public void recordDuelWin(UUID winner, UUID loser) {
        // TODO Fase 3
    }

    public void flush() {
        // TODO Fase 2
    }
}
