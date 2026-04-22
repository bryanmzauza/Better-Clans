package com.bryanmz.betterclans.duel;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerencia desafios pendentes e duelos em andamento. Implementacao na Fase 3.
 */
public final class DuelManager {

    private final BetterClansPlugin plugin;
    private final ConcurrentHashMap<UUID, DuelSession> pending = new ConcurrentHashMap<>();

    public DuelManager(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void challenge(Player challenger, Player challenged, double bet) {
        // TODO Fase 3
    }

    public void accept(Player challenged) {
        // TODO Fase 3
    }

    public void deny(Player challenged) {
        // TODO Fase 3
    }

    public void shutdown() {
        pending.clear();
    }
}
