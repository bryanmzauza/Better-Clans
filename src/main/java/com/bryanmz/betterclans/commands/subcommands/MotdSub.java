package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MotdSub extends AbstractSub {

    public MotdSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "motd"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null) { sender.sendMessage(plugin.messages().get("errors.not-in-clan")); return; }
        Clan clan = plugin.clans().getById(me.clanId()).orElseThrow();

        if (args.length == 0) {
            String motd = clan.motd();
            p.sendMessage(plugin.messages().raw("clan.info.motd", "motd", motd == null ? "-" : motd));
            return;
        }

        if (!me.role().canManage() && me.role() != ClanRole.OFFICER) {
            sender.sendMessage(plugin.messages().get("errors.not-officer"));
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            clan.setMotd(null);
            plugin.clans().saveClan(clan).thenRun(() -> sender.sendMessage(plugin.messages().get("clan.motd.cleared")));
            return;
        }

        String motd = String.join(" ", args);
        clan.setMotd(motd);
        plugin.clans().saveClan(clan).thenRun(() -> sender.sendMessage(plugin.messages().get("clan.motd.set")));
    }
}
