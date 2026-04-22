package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class DuelAdminCommand implements CommandExecutor, TabCompleter {

    private final BetterClansPlugin plugin;

    public DuelAdminCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("betterclans.admin.duel")) {
            sender.sendMessage(plugin.messages().get("errors.no-permission"));
            return true;
        }
        if (!(sender instanceof Player p)) {
            sender.sendMessage(plugin.messages().get("general.player-only"));
            return true;
        }
        if (args.length < 2 || !args[0].equalsIgnoreCase("setspawn")) {
            sender.sendMessage(Component.text("Uso: /x1admin setspawn <a|b>"));
            return true;
        }
        String slot = args[1].toLowerCase(Locale.ROOT);
        if (!slot.equals("a") && !slot.equals("b")) {
            sender.sendMessage(Component.text("Slot invalido. Use 'a' ou 'b'."));
            return true;
        }
        Location loc = p.getLocation();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("x", loc.getX());
        map.put("y", loc.getY());
        map.put("z", loc.getZ());
        map.put("yaw", (double) loc.getYaw());
        map.put("pitch", (double) loc.getPitch());
        plugin.getConfig().set("duel.arena.spawn-" + slot, map);
        plugin.saveConfig();
        sender.sendMessage(Component.text("Spawn " + slot.toUpperCase(Locale.ROOT) + " do /x1 configurado."));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("setspawn");
        if (args.length == 2 && args[0].equalsIgnoreCase("setspawn")) return List.of("a", "b");
        return List.of();
    }
}
