package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.commands.SubCommand;
import org.bukkit.command.CommandSender;

/**
 * Base de todos os subs de /clan: resposta padrao "em breve" ate que a logica seja implementada.
 */
public abstract class AbstractSub implements SubCommand {

    protected final BetterClansPlugin plugin;

    protected AbstractSub(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(plugin.messages().get("general.not-implemented"));
    }
}
