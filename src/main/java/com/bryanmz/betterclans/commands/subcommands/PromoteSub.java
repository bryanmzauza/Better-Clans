package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PromoteSub extends AbstractSub {

    public PromoteSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "promote"; }
    @Override public String permission() { return "betterclans.manage"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan promote <jogador>")); return; }

        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null || me.role() != ClanRole.LEADER) { sender.sendMessage(plugin.messages().get("errors.not-leader")); return; }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        ClanMember tm = plugin.clans().getMember(target.getUniqueId()).orElse(null);
        if (tm == null || !tm.clanId().equals(me.clanId())) {
            sender.sendMessage(plugin.messages().get("errors.target-not-in-clan", "player", args[0]));
            return;
        }
        if (tm.role() != ClanRole.MEMBER) {
            sender.sendMessage(plugin.messages().get("clan.promote.already", "player", args[0]));
            return;
        }
        tm.setRole(ClanRole.OFFICER);
        plugin.clans().saveMember(tm).thenRun(() ->
                p.sendMessage(plugin.messages().get("clan.promote.success", "player", args[0])));
    }
}
