package com.bryanmz.betterclans.nametag;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Locale;

public final class ScoreboardNametagProvider implements NametagManager {

    private final BetterClansPlugin plugin;

    public ScoreboardNametagProvider(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void apply(Player player) {
        if (!plugin.getConfig().getBoolean("nametag.enabled", true)) return;
        ClanMember member = plugin.clans().getMember(player.getUniqueId()).orElse(null);
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();

        // Remove de qualquer team bc_* anterior
        removeFromBcTeams(sb, player);

        if (member == null) {
            updateTablist(player, null);
            return;
        }
        Clan clan = plugin.clans().getById(member.clanId()).orElse(null);
        if (clan == null) return;

        String teamName = "bc_" + clan.tag().toLowerCase(Locale.ROOT);
        Team team = sb.getTeam(teamName);
        if (team == null) team = sb.registerNewTeam(teamName);
        TextColor color = com.bryanmz.betterclans.util.ColorUtil.parse(clan.tagColor());
        // Prefix: apenas a tag colorida; espaco separador fica branco para o nome ficar branco.
        team.prefix(Component.text("[" + clan.tag() + "]").color(color)
                .append(Component.text(" ").color(NamedTextColor.WHITE)));
        team.color(NamedTextColor.WHITE);
        team.addEntity(player);

        updateTablist(player, clan);
    }

    private void updateTablist(Player player, Clan clan) {
        if (!plugin.getConfig().getBoolean("tablist.enabled", true)) return;
        if (clan == null) {
            player.playerListName(Component.text(player.getName(), NamedTextColor.WHITE));
            return;
        }
        TextColor color = com.bryanmz.betterclans.util.ColorUtil.parse(clan.tagColor());
        Component line = Component.text("[" + clan.tag() + "]").color(color)
                .append(Component.text(" " + player.getName(), NamedTextColor.WHITE));
        player.playerListName(line);
    }

    @Override
    public void clear(Player player) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        removeFromBcTeams(sb, player);
        updateTablist(player, null);
    }

    @Override
    public void shutdown() {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team t : sb.getTeams()) {
            if (t.getName().startsWith("bc_")) t.unregister();
        }
    }

    private void removeFromBcTeams(Scoreboard sb, Player player) {
        for (Team t : sb.getTeams()) {
            if (t.getName().startsWith("bc_") && t.hasEntity(player)) {
                t.removeEntity(player);
            }
        }
    }
}
