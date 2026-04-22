package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /clanchat <msg> - envia mensagem direta ao canal do cla (sem toggle persistente).
 */
public final class ClanChatCommand implements CommandExecutor {

    private final BetterClansPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ClanChatCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return true; }
        if (args.length == 0) {
            // toggle
            String cur = plugin.clans().chatMode(p.getUniqueId());
            if ("clan".equals(cur)) {
                plugin.clans().setChatMode(p.getUniqueId(), null);
                p.sendMessage(plugin.messages().get("chat.toggled-off"));
            } else {
                plugin.clans().setChatMode(p.getUniqueId(), "clan");
                p.sendMessage(plugin.messages().get("chat.toggled-on", "mode", "clan"));
            }
            return true;
        }
        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null) { sender.sendMessage(plugin.messages().get("errors.not-in-clan")); return true; }
        Clan clan = plugin.clans().getById(me.clanId()).orElseThrow();
        String message = String.join(" ", args);
        String prefix = plugin.getConfig().getString("chat.clan-chat-prefix", "<dark_green>[Cla]</dark_green>");
        Component rendered = mm.deserialize(prefix + " <white><player></white><gray>:</gray> <msg>",
                Placeholder.unparsed("player", p.getName()),
                Placeholder.unparsed("msg", message));
        for (ClanMember m : plugin.clans().membersOf(clan.id())) {
            Player online = Bukkit.getPlayer(m.playerUuid());
            if (online != null) online.sendMessage(rendered);
        }
        return true;
    }
}
