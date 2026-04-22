package com.bryanmz.betterclans.stats;

import com.bryanmz.betterclans.BetterClansPlugin;

/**
 * No momento, stats sao atualizadas direto no StatsListener via ClanManager.
 * Essa classe reserva espaco para cache/batching futuro.
 */
public final class StatsService {

    private final BetterClansPlugin plugin;

    public StatsService(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void flush() {
        plugin.clans().flush();
    }
}
