package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AcceptSub extends AbstractSub {

    public AcceptSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "accept"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan accept <TAG>")); return; }

        Clan clan = plugin.clans().getByTag(args[0]).orElse(null);
        if (clan == null) { sender.sendMessage(plugin.messages().get("errors.clan-not-found", "tag", args[0])); return; }

        if (plugin.clans().getMember(p.getUniqueId()).isPresent()) {
            sender.sendMessage(plugin.messages().get("errors.already-in-clan"));
            return;
        }

        if (!plugin.clans().invites().consume(p.getUniqueId(), clan.id())) {
            sender.sendMessage(plugin.messages().get("clan.invite.none"));
            return;
        }

        int max = plugin.getConfig().getInt("clan.max-members", 30);
        if (plugin.clans().membersOf(clan.id()).size() >= max) {
            sender.sendMessage(plugin.messages().get("errors.full", "max", String.valueOf(max)));
            return;
        }

        plugin.clans().addMember(clan, p.getUniqueId(), ClanRole.MEMBER).thenRun(() -> {
            p.sendMessage(plugin.messages().get("clan.invite.accepted", "tag", clan.tag()));
            plugin.nametag().apply(p);
        });
    }
}
