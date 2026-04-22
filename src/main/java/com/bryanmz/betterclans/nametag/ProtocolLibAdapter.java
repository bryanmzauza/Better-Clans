package com.bryanmz.betterclans.nametag;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Adapter opcional via ProtocolLib para customizacao avancada (suffix com nivel, etc.).
 * Selecionado quando config "nametag.provider" = "protocollib" e o plugin esta presente.
 */
public final class ProtocolLibAdapter implements NametagManager {

    private final BetterClansPlugin plugin;

    public ProtocolLibAdapter(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public static boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

    @Override
    public void apply(Player player) {
        // TODO Fase 2: enviar pacotes de team via ProtocolLib
    }

    @Override
    public void clear(Player player) {
        // TODO Fase 2
    }

    @Override
    public void shutdown() {
        // TODO Fase 2
    }
}
