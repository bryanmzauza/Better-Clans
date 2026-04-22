package com.bryanmz.betterclans.events;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRelation;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public final class PvPListener implements Listener {

    private final BetterClansPlugin plugin;

    public PvPListener(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Player victim = event.getEntity() instanceof Player pv ? pv : null;
        if (victim == null) return;
        Player attacker = resolveAttacker(event);
        if (attacker == null || attacker.equals(victim)) return;

        ClanMember a = plugin.clans().getMember(attacker.getUniqueId()).orElse(null);
        ClanMember v = plugin.clans().getMember(victim.getUniqueId()).orElse(null);
        if (a == null || v == null) return;

        if (a.clanId().equals(v.clanId())) {
            if (!plugin.getConfig().getBoolean("clan.friendly-fire", false)) {
                event.setCancelled(true);
            }
            return;
        }

        if (plugin.clans().relationBetween(a.clanId(), v.clanId()) == ClanRelation.ALLY
                && !plugin.getConfig().getBoolean("clan.ally-damage", false)) {
            event.setCancelled(true);
        }
    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player p) return p;
        if (event.getDamager() instanceof Projectile proj) {
            ProjectileSource src = proj.getShooter();
            if (src instanceof Player p) return p;
        }
        return null;
    }
}
