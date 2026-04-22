package com.bryanmz.betterclans.nametag;

import org.bukkit.entity.Player;

/**
 * Provedor de nametag acima da cabeca. Implementacoes: Scoreboard (padrao) ou ProtocolLib.
 */
public interface NametagManager {

    void apply(Player player);

    void clear(Player player);

    void shutdown();
}
