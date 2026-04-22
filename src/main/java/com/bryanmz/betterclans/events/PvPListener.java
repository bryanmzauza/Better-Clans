package com.bryanmz.betterclans.events;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Bloqueia dano entre aliados / mesmo cla conforme config.
 */
public final class PvPListener implements Listener {

    private final BetterClansPlugin plugin;

    public PvPListener(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        // TODO Fase 2: cancelar se mesmo cla e !friendly-fire, ou se aliado e !ally-damage
    }
}
