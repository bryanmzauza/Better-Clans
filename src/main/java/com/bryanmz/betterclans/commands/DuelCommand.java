package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DuelCommand implements CommandExecutor, TabCompleter {

    private final BetterClansPlugin plugin;

    public DuelCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return true; }
        if (args.length < 1) {
            sender.sendMessage(plugin.messages().get("general.usage", "usage", "/x1 <jogador|accept|deny|stats>"));
            return true;
        }
        String head = args[0].toLowerCase(Locale.ROOT);
        switch (head) {
            case "accept" -> plugin.duels().accept(p);
            case "deny", "denied" -> plugin.duels().deny(p);
            case "stats" -> showStats(p);
            default -> {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) { sender.sendMessage(plugin.messages().get("errors.player-offline", "player", args[0])); return true; }
                double bet = 0;
                if (args.length >= 2) {
                    try { bet = Double.parseDouble(args[1]); } catch (NumberFormatException e) {
                        sender.sendMessage(plugin.messages().get("errors.invalid-number"));
                        return true;
                    }
                }
                plugin.duels().challenge(p, target, bet);
            }
        }
        return true;
    }

    private void showStats(Player p) {
        plugin.database().dao().duelStats(p.getUniqueId()).thenAccept(stats -> {
            p.sendMessage(Component.text("Duelos vencidos: " + stats[0] + " | perdidos: " + stats[1]));
        });
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> out = new ArrayList<>();
            for (String fixed : new String[] { "accept", "deny", "stats" }) {
                if (fixed.startsWith(prefix)) out.add(fixed);
            }
            for (var online : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player self && self.getUniqueId().equals(online.getUniqueId())) continue;
                if (online.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) out.add(online.getName());
            }
            return out;
        }
        return List.of();
    }
}
