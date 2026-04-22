package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /x1 - desafios avulsos. Implementacao completa na Fase 3.
 */
public final class DuelCommand implements CommandExecutor {

    private final BetterClansPlugin plugin;

    public DuelCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(plugin.messages().get("general.not-implemented"));
        return true;
    }
}
