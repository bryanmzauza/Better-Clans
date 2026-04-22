package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class InfoSub extends AbstractSub {

    public InfoSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "info"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Clan clan;
        if (args.length >= 1) {
            clan = plugin.clans().getByTag(args[0]).orElse(null);
            if (clan == null) { sender.sendMessage(plugin.messages().get("errors.clan-not-found", "tag", args[0])); return; }
        } else {
            if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan info <TAG>")); return; }
            clan = plugin.clans().getClanOf(p.getUniqueId()).orElse(null);
            if (clan == null) { sender.sendMessage(plugin.messages().get("errors.not-in-clan")); return; }
        }

        List<ClanMember> members = plugin.clans().membersOf(clan.id());
        String leaderName = Bukkit.getOfflinePlayer(clan.leaderUuid()).getName();
        String names = members.stream()
                .map(m -> Bukkit.getOfflinePlayer(m.playerUuid()).getName())
                .filter(n -> n != null)
                .collect(Collectors.joining(", "));

        sender.sendMessage(plugin.messages().raw("clan.info.header", "tag", clan.tag(), "name", clan.name()));
        sender.sendMessage(plugin.messages().raw("clan.info.leader", "leader", leaderName == null ? "?" : leaderName));
        sender.sendMessage(plugin.messages().raw("clan.info.level", "level", String.valueOf(clan.level()), "xp", String.valueOf(clan.xp())));
        sender.sendMessage(plugin.messages().raw("clan.info.members", "count", String.valueOf(members.size()), "list", names));
        sender.sendMessage(plugin.messages().raw("clan.info.kd",
                "kills", String.valueOf(clan.kills()),
                "deaths", String.valueOf(clan.deaths()),
                "ratio", String.format("%.2f", clan.kdRatio())));
        sender.sendMessage(plugin.messages().raw("clan.info.wins", "wins", String.valueOf(clan.wins())));
        if (clan.motd() != null && !clan.motd().isBlank()) {
            sender.sendMessage(plugin.messages().raw("clan.info.motd", "motd", clan.motd()));
        }
    }

    @Override
    public java.util.List<String> tabComplete(org.bukkit.command.CommandSender sender, String[] args) {
        return args.length == 1 ? completeTags(args[0]) : java.util.Collections.emptyList();
    }
}
