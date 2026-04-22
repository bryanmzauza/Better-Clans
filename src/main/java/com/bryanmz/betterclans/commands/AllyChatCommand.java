package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /ac - atalho para chat de alianca.
 */
public final class AllyChatCommand implements CommandExecutor {

    private final BetterClansPlugin plugin;

    public AllyChatCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(plugin.messages().get("general.not-implemented"));
        return true;
    }
}
