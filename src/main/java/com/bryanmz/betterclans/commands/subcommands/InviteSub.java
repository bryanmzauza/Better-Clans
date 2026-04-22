package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class InviteSub extends AbstractSub {

    public InviteSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "invite"; }
    @Override public String permission() { return "betterclans.invite"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan invite <jogador>")); return; }

        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null) { sender.sendMessage(plugin.messages().get("errors.not-in-clan")); return; }
        if (!me.role().canInvite()) { sender.sendMessage(plugin.messages().get("errors.not-officer")); return; }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { sender.sendMessage(plugin.messages().get("errors.player-offline", "player", args[0])); return; }
        if (target.getUniqueId().equals(p.getUniqueId())) { sender.sendMessage(plugin.messages().get("errors.self-action")); return; }
        if (plugin.clans().getMember(target.getUniqueId()).isPresent()) {
            sender.sendMessage(plugin.messages().get("errors.target-already-in-clan", "player", target.getName()));
            return;
        }

        Clan clan = plugin.clans().getById(me.clanId()).orElseThrow();
        int max = plugin.getConfig().getInt("clan.max-members", 30);
        if (plugin.clans().membersOf(clan.id()).size() >= max) {
            sender.sendMessage(plugin.messages().get("errors.full", "max", String.valueOf(max)));
            return;
        }

        plugin.clans().invites().invite(target.getUniqueId(), clan.id());
        p.sendMessage(plugin.messages().get("clan.invite.sent", "player", target.getName()));
        target.sendMessage(plugin.messages().get("clan.invite.received", "player", p.getName(), "tag", clan.tag()));
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? completePlayers(args[0]) : java.util.Collections.emptyList();
    }
}
