package com.bryanmz.betterclans.events;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRelation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class StatsListener implements Listener {

    private final BetterClansPlugin plugin;

    public StatsListener(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.nametag().apply(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.clans().setChatMode(event.getPlayer().getUniqueId(), null);
        plugin.gladiator().onParticipantQuit(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Gladiator lifecycle
        plugin.gladiator().onParticipantDeath(victim.getUniqueId());
        // Duel lifecycle
        if (plugin.duels().isInDuel(victim.getUniqueId())) {
            plugin.duels().onDeath(victim.getUniqueId());
            return; // nao contabiliza nas stats normais
        }

        ClanMember vm = plugin.clans().getMember(victim.getUniqueId()).orElse(null);
        if (vm != null) {
            vm.incrementDeaths();
            Clan vc = plugin.clans().getById(vm.clanId()).orElse(null);
            if (vc != null) { vc.incrementDeaths(); plugin.clans().saveClan(vc); }
            plugin.clans().saveMember(vm);
        }

        if (killer == null || killer.equals(victim)) return;
        ClanMember km = plugin.clans().getMember(killer.getUniqueId()).orElse(null);
        if (km != null) {
            km.incrementKills();
            Clan kc = plugin.clans().getById(km.clanId()).orElse(null);
            if (kc != null) {
                kc.incrementKills();
                long xp;
                if (vm != null && plugin.clans().relationBetween(km.clanId(), vm.clanId()) == ClanRelation.RIVAL) {
                    xp = plugin.getConfig().getLong("xp.per-kill-rival", 10);
                } else {
                    xp = plugin.getConfig().getLong("xp.per-kill-neutral", 5);
                }
                kc.addXp(xp);
                plugin.clans().saveClan(kc);
            }
            plugin.clans().saveMember(km);
        }
    }
}
