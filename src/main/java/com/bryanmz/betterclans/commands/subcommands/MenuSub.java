package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MenuSub extends AbstractSub {

    public MenuSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "menu"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        plugin.menus().openMain(p);
    }
}
