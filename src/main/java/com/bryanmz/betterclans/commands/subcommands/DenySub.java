package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DenySub extends AbstractSub {

    public DenySub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "deny"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan deny <TAG>")); return; }
        Clan clan = plugin.clans().getByTag(args[0]).orElse(null);
        if (clan == null) { sender.sendMessage(plugin.messages().get("errors.clan-not-found", "tag", args[0])); return; }
        if (plugin.clans().invites().consume(p.getUniqueId(), clan.id())) {
            p.sendMessage(plugin.messages().get("clan.invite.denied"));
        } else {
            p.sendMessage(plugin.messages().get("clan.invite.none"));
        }
    }
}
