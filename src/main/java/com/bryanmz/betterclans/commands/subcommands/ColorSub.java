package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRole;
import com.bryanmz.betterclans.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ColorSub extends AbstractSub {

    public ColorSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "color"; }
    @Override public String permission() { return "betterclans.manage"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan color <nome|#hex>")); return; }

        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null || me.role() != ClanRole.LEADER) { sender.sendMessage(plugin.messages().get("errors.not-leader")); return; }

        String color = args[0];
        if (!ColorUtil.isValid(color)) { sender.sendMessage(plugin.messages().get("clan.color.invalid")); return; }

        Clan clan = plugin.clans().getById(me.clanId()).orElseThrow();
        clan.setTagColor(color);
        plugin.clans().saveClan(clan).thenRun(() -> {
            sender.sendMessage(plugin.messages().get("clan.color.set", "color", color));
            // re-aplicar nametag em todos membros online
            for (ClanMember m : plugin.clans().membersOf(clan.id())) {
                Player pp = Bukkit.getPlayer(m.playerUuid());
                if (pp != null) plugin.nametag().apply(pp);
            }
        });
    }
}
