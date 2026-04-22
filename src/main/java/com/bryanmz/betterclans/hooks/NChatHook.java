package com.bryanmz.betterclans.hooks;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.Bukkit;

/**
 * Stub para integracao futura com nChat/Carbon/VentureChat (placeholder-only mode).
 */
public final class NChatHook {

    private final BetterClansPlugin plugin;

    public NChatHook(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean detect() {
        boolean has = Bukkit.getPluginManager().isPluginEnabled("nChat")
                || Bukkit.getPluginManager().isPluginEnabled("Carbon")
                || Bukkit.getPluginManager().isPluginEnabled("VentureChat");
        if (has) {
            plugin.getLogger().info("Plugin de chat externo detectado. Use chat.mode: placeholder-only para integracao limpa.");
        }
        return has;
    }
}
