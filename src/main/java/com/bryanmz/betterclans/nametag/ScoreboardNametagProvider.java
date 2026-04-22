package com.bryanmz.betterclans.nametag;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.entity.Player;

/**
 * Implementacao padrao via Scoreboard Team prefix.
 */
public final class ScoreboardNametagProvider implements NametagManager {

    private final BetterClansPlugin plugin;

    public ScoreboardNametagProvider(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void apply(Player player) {
        // TODO Fase 1: criar/atualizar Team "bc_<TAG>" com prefix [TAG] e adicionar o player
    }

    @Override
    public void clear(Player player) {
        // TODO Fase 1: remover de qualquer team bc_*
    }

    @Override
    public void shutdown() {
        // TODO Fase 1: remover todos os teams bc_* criados
    }
}
