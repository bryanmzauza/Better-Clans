package com.bryanmz.betterclans.events;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Atualiza K/D individual e agregado do cla em mortes PvP.
 */
public final class StatsListener implements Listener {

    private final BetterClansPlugin plugin;

    public StatsListener(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // TODO Fase 2: +1 death no morto, +1 kill no killer, +XP conforme relacao entre clas
    }
}
