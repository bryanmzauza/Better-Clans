package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.ClanMember;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class KickSub extends AbstractSub {

    public KickSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "kick"; }
    @Override public String permission() { return "betterclans.kick"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan kick <jogador>")); return; }

        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null) { sender.sendMessage(plugin.messages().get("errors.not-in-clan")); return; }
        if (!me.role().canKick()) { sender.sendMessage(plugin.messages().get("errors.not-officer")); return; }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getUniqueId().equals(p.getUniqueId())) { sender.sendMessage(plugin.messages().get("errors.self-action")); return; }
        if (plugin.gladiator().isParticipant(target.getUniqueId())) {
            sender.sendMessage(plugin.messages().get("errors.target-in-gladiator"));
            return;
        }

        ClanMember tm = plugin.clans().getMember(target.getUniqueId()).orElse(null);
        if (tm == null || !tm.clanId().equals(me.clanId())) {
            sender.sendMessage(plugin.messages().get("errors.target-not-in-clan", "player", args[0]));
            return;
        }
        if (!me.role().isAtLeast(tm.role())) {
            sender.sendMessage(plugin.messages().get("errors.no-permission"));
            return;
        }

        plugin.clans().removeMember(target.getUniqueId()).thenRun(() -> {
            p.sendMessage(plugin.messages().get("clan.kick.success", "player", args[0]));
            Player online = target.getPlayer();
            if (online != null) {
                online.sendMessage(plugin.messages().get("clan.kick.notify"));
                plugin.nametag().clear(online);
            }
        });
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) return java.util.Collections.emptyList();
        if (!(sender instanceof Player p)) return completePlayers(args[0]);
        var me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null) return java.util.Collections.emptyList();
        return completeClanMembers(me.clanId(), args[0]);
    }
}
