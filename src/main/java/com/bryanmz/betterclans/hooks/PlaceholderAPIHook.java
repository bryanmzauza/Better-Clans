package com.bryanmz.betterclans.hooks;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Expansion PlaceholderAPI: expõe %betterclans_*% para outros plugins.
 */
public final class PlaceholderAPIHook extends PlaceholderExpansion {

    private final BetterClansPlugin plugin;

    public PlaceholderAPIHook(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public static boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @Override public @NotNull String getIdentifier() { return "betterclans"; }
    @Override public @NotNull String getAuthor() { return "BryanMZ"; }
    @Override public @NotNull String getVersion() { return plugin.getPluginMeta().getVersion(); }
    @Override public boolean persist() { return true; }

    @Override
    public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";
        Optional<ClanMember> member = plugin.clans().getMember(player.getUniqueId());
        Optional<Clan> clan = member.flatMap(m -> plugin.clans().getById(m.clanId()));

        return switch (params.toLowerCase()) {
            case "tag" -> clan.map(Clan::tag).orElse("");
            case "name" -> clan.map(Clan::name).orElse("");
            case "color" -> clan.map(Clan::tagColor).orElse("white");
            case "level" -> clan.map(c -> String.valueOf(c.level())).orElse("0");
            case "role" -> member.map(m -> m.role().name()).orElse("");
            case "player_kd" -> member.map(m -> {
                int d = Math.max(1, m.deaths());
                return String.format("%.2f", m.kills() / (double) d);
            }).orElse("0.00");
            default -> null;
        };
    }
}
