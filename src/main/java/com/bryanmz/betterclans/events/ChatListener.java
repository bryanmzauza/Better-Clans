package com.bryanmz.betterclans.events;

import com.bryanmz.betterclans.BetterClansPlugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Formata o chat com a tag do cla quando chat.mode == standalone.
 */
public final class ChatListener implements Listener {

    private final BetterClansPlugin plugin;

    public ChatListener(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        // TODO Fase 1: aplicar formato com tag, cor e role
    }
}
