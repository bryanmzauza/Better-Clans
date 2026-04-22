package com.bryanmz.betterclans.hooks;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.Bukkit;

/**
 * Detecta LuckPerms; permissoes sao resolvidas via Bukkit perms normalmente.
 */
public final class LuckPermsHook {

    private final BetterClansPlugin plugin;
    private boolean present;

    public LuckPermsHook(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void detect() {
        this.present = Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
        if (present) plugin.getLogger().info("LuckPerms detectado.");
    }

    public boolean isPresent() {
        return present;
    }
}
