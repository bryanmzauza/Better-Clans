package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRelation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AllyChatCommand implements CommandExecutor {

    private final BetterClansPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AllyChatCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return true; }
        if (args.length == 0) {
            String cur = plugin.clans().chatMode(p.getUniqueId());
            if ("ally".equals(cur)) {
                plugin.clans().setChatMode(p.getUniqueId(), null);
                p.sendMessage(plugin.messages().get("chat.toggled-off"));
            } else {
                plugin.clans().setChatMode(p.getUniqueId(), "ally");
                p.sendMessage(plugin.messages().get("chat.toggled-on", "mode", "ally"));
            }
            return true;
        }
        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null) { sender.sendMessage(plugin.messages().get("errors.not-in-clan")); return true; }
        Clan clan = plugin.clans().getById(me.clanId()).orElseThrow();

        String message = String.join(" ", args);
        String prefix = plugin.getConfig().getString("chat.ally-chat-prefix", "<aqua>[Alianca]</aqua>");
        Component rendered = mm.deserialize(prefix + " <white><tag> <player></white><gray>:</gray> <msg>",
                Placeholder.unparsed("tag", clan.tag()),
                Placeholder.unparsed("player", p.getName()),
                Placeholder.unparsed("msg", message));

        Set<UUID> targets = new HashSet<>();
        for (Clan other : plugin.clans().all()) {
            if (plugin.clans().relationBetween(clan.id(), other.id()) == ClanRelation.ALLY) {
                for (ClanMember m : plugin.clans().membersOf(other.id())) targets.add(m.playerUuid());
            }
        }
        for (ClanMember m : plugin.clans().membersOf(clan.id())) targets.add(m.playerUuid());

        for (UUID id : targets) {
            Player online = Bukkit.getPlayer(id);
            if (online != null) online.sendMessage(rendered);
        }
        return true;
    }
}
