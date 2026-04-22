package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRelation;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RivalSub extends AbstractSub {

    public RivalSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "rival"; }
    @Override public String permission() { return "betterclans.diplomacy"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan rival <TAG>")); return; }

        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null || me.role() != ClanRole.LEADER) { sender.sendMessage(plugin.messages().get("errors.not-leader")); return; }

        Clan other = plugin.clans().getByTag(args[0]).orElse(null);
        if (other == null) { sender.sendMessage(plugin.messages().get("errors.clan-not-found", "tag", args[0])); return; }
        Clan mine = plugin.clans().getById(me.clanId()).orElseThrow();
        if (mine.id().equals(other.id())) { sender.sendMessage(plugin.messages().get("clan.rival.self")); return; }

        ClanRelation current = plugin.clans().relationBetween(mine.id(), other.id());
        if (current == ClanRelation.RIVAL) {
            plugin.clans().setRelation(mine.id(), other.id(), ClanRelation.NEUTRAL).thenRun(() ->
                    sender.sendMessage(plugin.messages().get("clan.rival.removed", "tag", other.tag())));
        } else {
            plugin.clans().setRelation(mine.id(), other.id(), ClanRelation.RIVAL).thenRun(() ->
                    sender.sendMessage(plugin.messages().get("clan.rival.set", "tag", other.tag())));
        }
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? completeTags(args[0]) : java.util.Collections.emptyList();
    }
}
