package com.bryanmz.betterclans.events;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRelation;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Formata o chat global conforme clan.tag do remetente, e roteia canais clan/ally quando ativos.
 */
public final class ChatListener implements Listener {

    private final BetterClansPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ChatListener(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player p = event.getPlayer();
        ClanMember member = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        Clan clan = member == null ? null : plugin.clans().getById(member.clanId()).orElse(null);
        String plain = PlainTextComponentSerializer.plainText().serialize(event.message());

        // Canais alternativos via toggle
        String mode = plugin.clans().chatMode(p.getUniqueId());
        if (clan != null && ("clan".equals(mode) || "ally".equals(mode))) {
            event.setCancelled(true);
            if ("clan".equals(mode)) {
                broadcastClan(clan, p, plain);
            } else {
                broadcastAlly(clan, p, plain);
            }
            return;
        }

        // Modo placeholder-only: nao formata
        if (!"standalone".equalsIgnoreCase(plugin.getConfig().getString("chat.mode", "standalone"))) {
            return;
        }

        String format = plugin.getConfig().getString("chat.format",
                "<gray>[</gray><clan_color><clan_tag></clan_color><gray>]</gray> <player_name><gray>:</gray> <message>");
        TagResolver resolver = TagResolver.resolver(
                Placeholder.unparsed("clan_tag", clan == null ? "" : clan.tag()),
                Placeholder.unparsed("clan_color", clan == null ? "white" : clan.tagColor()),
                Placeholder.unparsed("player_name", p.getName()),
                Placeholder.unparsed("message", plain)
        );
        Component rendered = mm.deserialize(format, resolver);
        event.renderer((source, display, msg, viewer) -> rendered);
    }

    private void broadcastClan(Clan clan, Player from, String message) {
        String prefix = plugin.getConfig().getString("chat.clan-chat-prefix", "<dark_green>[Cla]</dark_green>");
        Component rendered = mm.deserialize(prefix + " <white><player></white><gray>:</gray> <msg>",
                Placeholder.unparsed("player", from.getName()),
                Placeholder.unparsed("msg", message));
        for (ClanMember m : plugin.clans().membersOf(clan.id())) {
            Player online = Bukkit.getPlayer(m.playerUuid());
            if (online != null) online.sendMessage(rendered);
        }
    }

    private void broadcastAlly(Clan clan, Player from, String message) {
        String prefix = plugin.getConfig().getString("chat.ally-chat-prefix", "<aqua>[Alianca]</aqua>");
        Component rendered = mm.deserialize(prefix + " <white><tag> <player></white><gray>:</gray> <msg>",
                Placeholder.unparsed("tag", clan.tag()),
                Placeholder.unparsed("player", from.getName()),
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
    }
}
