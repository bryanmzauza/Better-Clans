package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /cc - atalho para chat de cla. Redireciona para a logica de ChatSub na Fase 1.
 */
public final class ClanChatCommand implements CommandExecutor {

    private final BetterClansPlugin plugin;

    public ClanChatCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(plugin.messages().get("general.not-implemented"));
        return true;
    }
}
