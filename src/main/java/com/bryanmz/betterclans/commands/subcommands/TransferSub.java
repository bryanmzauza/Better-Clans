package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TransferSub extends AbstractSub {

    public TransferSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "transfer"; }
    @Override public String permission() { return "betterclans.manage"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan transfer <jogador>")); return; }

        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null || me.role() != ClanRole.LEADER) { sender.sendMessage(plugin.messages().get("errors.not-leader")); return; }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getUniqueId().equals(p.getUniqueId())) { sender.sendMessage(plugin.messages().get("errors.self-action")); return; }

        ClanMember tm = plugin.clans().getMember(target.getUniqueId()).orElse(null);
        if (tm == null || !tm.clanId().equals(me.clanId())) {
            sender.sendMessage(plugin.messages().get("errors.target-not-in-clan", "player", args[0]));
            return;
        }

        Clan clan = plugin.clans().getById(me.clanId()).orElseThrow();
        me.setRole(ClanRole.OFFICER);
        tm.setRole(ClanRole.LEADER);
        clan.setLeaderUuid(tm.playerUuid());
        plugin.clans().saveMember(me);
        plugin.clans().saveMember(tm);
        plugin.clans().saveClan(clan).thenRun(() ->
                p.sendMessage(plugin.messages().get("clan.transfer.success", "player", args[0])));
    }
}
