package com.bryanmz.betterclans.gladiator;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public final class GladiatorScheduler {

    private final BetterClansPlugin plugin;
    private BukkitTask signupTask;
    private BukkitTask matchTask;

    public GladiatorScheduler(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        scheduleSignup();
        scheduleMatch();
    }

    public void shutdown() {
        if (signupTask != null) signupTask.cancel();
        if (matchTask != null) matchTask.cancel();
    }

    private void scheduleSignup() {
        ConfigurationSection cfg = plugin.getConfig().getConfigurationSection("gladiator.signup-opens");
        if (cfg == null) return;
        long ticks = computeDelayTicks(cfg);
        this.signupTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.gladiator().openSignups();
            scheduleSignup();
        }, ticks);
    }

    private void scheduleMatch() {
        ConfigurationSection cfg = plugin.getConfig().getConfigurationSection("gladiator.schedule");
        if (cfg == null) return;
        long ticks = computeDelayTicks(cfg);
        this.matchTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.gladiator().closeSignups();
            plugin.gladiator().startMatch();
            scheduleMatch();
        }, ticks);
    }

    private long computeDelayTicks(ConfigurationSection schedule) {
        DayOfWeek day = DayOfWeek.valueOf(schedule.getString("day-of-week", "SATURDAY"));
        int hour = schedule.getInt("hour", 20);
        int minute = schedule.getInt("minute", 0);
        ZoneId zone = ZoneId.of(plugin.getConfig().getString("gladiator.schedule.timezone", "America/Sao_Paulo"));

        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime next = now.with(TemporalAdjusters.nextOrSame(day)).with(LocalTime.of(hour, minute)).withSecond(0).withNano(0);
        if (!next.isAfter(now)) next = next.plusWeeks(1);

        long seconds = Duration.between(now, next).toSeconds();
        return Math.max(20L, seconds * 20L);
    }
}
