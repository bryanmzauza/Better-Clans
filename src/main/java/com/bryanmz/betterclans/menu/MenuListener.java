package com.bryanmz.betterclans.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registra inventarios de menu abertos por UUID e intercepta cliques.
 */
public final class MenuListener implements Listener {

    private static final Map<UUID, JavaMenuBase> OPEN = new ConcurrentHashMap<>();

    static void register(UUID player, JavaMenuBase menu) {
        OPEN.put(player, menu);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;
        JavaMenuBase menu = OPEN.get(p.getUniqueId());
        if (menu == null) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;
        event.setCancelled(true);
        // Agenda para proximo tick para evitar erros ao reabrir o inventario
        final int slot = event.getSlot();
        Bukkit.getScheduler().runTask(com.bryanmz.betterclans.BetterClansPlugin.getInstance(),
                () -> menu.handleClick(p, slot));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player p) OPEN.remove(p.getUniqueId());
    }
}
