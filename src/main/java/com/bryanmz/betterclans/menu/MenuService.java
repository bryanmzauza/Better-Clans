package com.bryanmz.betterclans.menu;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.entity.Player;

/**
 * Servico central de menus. Decide entre formulario Bedrock (Floodgate/Cumulus)
 * e inventario Java com base no tipo do jogador.
 */
public final class MenuService {

    private final BetterClansPlugin plugin;
    private final boolean floodgateAvailable;

    public MenuService(BetterClansPlugin plugin) {
        this.plugin = plugin;
        boolean fg;
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            fg = plugin.getServer().getPluginManager().getPlugin("floodgate") != null;
        } catch (ClassNotFoundException e) {
            fg = false;
        }
        this.floodgateAvailable = fg;
    }

    public boolean isBedrock(Player player) {
        if (!floodgateAvailable) return false;
        try {
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        } catch (Throwable t) {
            return false;
        }
    }

    public void openMain(Player player) {
        if (isBedrock(player)) {
            try { BedrockMenus.openMain(plugin, player); return; }
            catch (Throwable t) { plugin.getLogger().warning("Fallback Java menu: " + t.getMessage()); }
        }
        new JavaClanMenu(plugin).open(player);
    }

    public void openClanList(Player player) {
        if (isBedrock(player)) {
            try { BedrockMenus.openClanList(plugin, player); return; }
            catch (Throwable t) { plugin.getLogger().warning("Fallback Java menu: " + t.getMessage()); }
        }
        new JavaClanListMenu(plugin).open(player);
    }

    public void openTop(Player player) {
        if (isBedrock(player)) {
            try { BedrockMenus.openTop(plugin, player); return; }
            catch (Throwable t) { plugin.getLogger().warning("Fallback Java menu: " + t.getMessage()); }
        }
        new JavaTopMenu(plugin).open(player);
    }
}
