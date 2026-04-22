package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DisbandSub extends AbstractSub {

    private final Map<UUID, Long> pending = new HashMap<>();
    private static final long WINDOW_MS = 30_000L;

    public DisbandSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "disband"; }
    @Override public String permission() { return "betterclans.disband"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null || me.role() != ClanRole.LEADER) { sender.sendMessage(plugin.messages().get("errors.not-leader")); return; }

        boolean confirm = args.length > 0 && args[0].equalsIgnoreCase("confirm");
        Long prev = pending.get(p.getUniqueId());
        if (!confirm || prev == null || System.currentTimeMillis() - prev > WINDOW_MS) {
            pending.put(p.getUniqueId(), System.currentTimeMillis());
            sender.sendMessage(plugin.messages().get("clan.disband.confirm"));
            return;
        }

        pending.remove(p.getUniqueId());
        UUID clanId = me.clanId();
        Clan clan = plugin.clans().getById(clanId).orElse(null);

        // snapshot members para nametag clear
        var members = plugin.clans().membersOf(clanId).stream().map(ClanMember::playerUuid).toList();
        plugin.clans().disband(clanId).thenRun(() -> {
            sender.sendMessage(plugin.messages().get("clan.disband.success"));
            for (UUID id : members) {
                Player online = Bukkit.getPlayer(id);
                if (online != null) plugin.nametag().clear(online);
            }
        });
    }
}
