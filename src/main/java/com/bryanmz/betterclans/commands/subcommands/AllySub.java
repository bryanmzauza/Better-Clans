package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRelation;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AllySub extends AbstractSub {

    public AllySub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "ally"; }
    @Override public String permission() { return "betterclans.diplomacy"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 1) { sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan ally <TAG>")); return; }

        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null || me.role() != ClanRole.LEADER) { sender.sendMessage(plugin.messages().get("errors.not-leader")); return; }

        Clan other = plugin.clans().getByTag(args[0]).orElse(null);
        if (other == null) { sender.sendMessage(plugin.messages().get("errors.clan-not-found", "tag", args[0])); return; }
        Clan mine = plugin.clans().getById(me.clanId()).orElseThrow();
        if (mine.id().equals(other.id())) { sender.sendMessage(plugin.messages().get("clan.ally.self")); return; }

        ClanRelation current = plugin.clans().relationBetween(mine.id(), other.id());
        if (current == ClanRelation.ALLY) {
            // toggle: remover alianca
            plugin.clans().setRelation(mine.id(), other.id(), ClanRelation.NEUTRAL).thenRun(() ->
                    sender.sendMessage(plugin.messages().get("clan.ally.removed", "tag", other.tag())));
            return;
        }

        // aceite da proposta enviada pelo outro lado?
        if (plugin.clans().allyProposals().consume(mine.id(), other.id())) {
            plugin.clans().setRelation(mine.id(), other.id(), ClanRelation.ALLY).thenRun(() -> {
                sender.sendMessage(plugin.messages().get("clan.ally.success", "tag", other.tag()));
                Player otherLeader = plugin.getServer().getPlayer(other.leaderUuid());
                if (otherLeader != null) otherLeader.sendMessage(plugin.messages().get("clan.ally.success", "tag", mine.tag()));
            });
            return;
        }

        // proposta nova
        plugin.clans().allyProposals().propose(mine.id(), other.id());
        sender.sendMessage(plugin.messages().get("clan.ally.proposed", "tag", other.tag()));
        Player otherLeader = plugin.getServer().getPlayer(other.leaderUuid());
        if (otherLeader != null) otherLeader.sendMessage(plugin.messages().get("clan.ally.received", "tag", mine.tag()));
    }
}
