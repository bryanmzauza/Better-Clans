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

/**
 * Agenda o match semanal do Gladiador (sab 20h America/Sao_Paulo por default).
 */
public final class GladiatorScheduler {

    private final BetterClansPlugin plugin;
    private BukkitTask task;

    public GladiatorScheduler(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        ConfigurationSection schedule = plugin.getConfig().getConfigurationSection("gladiator.schedule");
        if (schedule == null) return;
        // Sinaliza apenas; tick fino de delay e reagendamento ficam para Fase 4.
        long delayTicks = computeInitialDelayTicks(schedule);
        this.task = plugin.getServer().getScheduler().runTaskLater(plugin, this::fire, delayTicks);
    }

    public void shutdown() {
        if (task != null) task.cancel();
    }

    private void fire() {
        // TODO Fase 4: invocar start do GladiatorEvent
        start(); // reagenda proxima semana
    }

    private long computeInitialDelayTicks(ConfigurationSection schedule) {
        DayOfWeek day = DayOfWeek.valueOf(schedule.getString("day-of-week", "SATURDAY"));
        int hour = schedule.getInt("hour", 20);
        int minute = schedule.getInt("minute", 0);
        ZoneId zone = ZoneId.of(schedule.getString("timezone", "America/Sao_Paulo"));

        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime next = now.with(TemporalAdjusters.nextOrSame(day)).with(LocalTime.of(hour, minute));
        if (!next.isAfter(now)) next = next.plusWeeks(1);

        long seconds = Duration.between(now, next).toSeconds();
        return Math.max(20L, seconds * 20L); // ticks
    }
}
