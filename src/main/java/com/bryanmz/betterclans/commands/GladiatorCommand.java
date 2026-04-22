package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRole;
import com.bryanmz.betterclans.gladiator.GladiatorEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class GladiatorCommand implements CommandExecutor, TabCompleter {

    private final BetterClansPlugin plugin;

    public GladiatorCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Uso: /gladiator <join|leave|info|history|start|setspawn|setlobby|setreturn>"));
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "join" -> join(sender);
            case "leave" -> leave(sender);
            case "info" -> info(sender);
            case "history" -> history(sender);
            case "start" -> startAdmin(sender);
            case "setspawn" -> setSpawn(sender, args);
            case "setlobby" -> setLobby(sender);
            case "setreturn" -> setReturn(sender);
            default -> sender.sendMessage(Component.text("Subcomando desconhecido."));
        }
        return true;
    }

    private void join(CommandSender sender) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null || me.role() != ClanRole.LEADER) { sender.sendMessage(plugin.messages().get("errors.not-leader")); return; }
        if (plugin.gladiator().state() != GladiatorEvent.State.SIGNUP_OPEN) { sender.sendMessage(plugin.messages().get("gladiator.signup.not-open")); return; }
        if (plugin.gladiator().registeredClans().contains(me.clanId())) { sender.sendMessage(plugin.messages().get("gladiator.signup.already")); return; }
        int minMembers = plugin.getConfig().getInt("gladiator.min-members-per-clan", 2);
        if (plugin.clans().membersOf(me.clanId()).size() < minMembers) {
            sender.sendMessage(plugin.messages().get("gladiator.signup.not-enough", "min", String.valueOf(minMembers)));
            return;
        }
        if (plugin.gladiator().registerClan(me.clanId())) {
            Clan c = plugin.clans().getById(me.clanId()).orElseThrow();
            sender.sendMessage(plugin.messages().get("gladiator.signup.joined", "tag", c.tag()));
        }
    }

    private void leave(CommandSender sender) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null || me.role() != ClanRole.LEADER) { sender.sendMessage(plugin.messages().get("errors.not-leader")); return; }
        if (plugin.gladiator().unregisterClan(me.clanId())) {
            sender.sendMessage(plugin.messages().get("gladiator.signup.left"));
        } else {
            sender.sendMessage(plugin.messages().get("gladiator.signup.not-in"));
        }
    }

    private void info(CommandSender sender) {
        GladiatorEvent e = plugin.gladiator();
        sender.sendMessage(Component.text("Estado: " + e.state()));
        sender.sendMessage(Component.text("Clas inscritos: " + e.registeredClans().size()));
        if (e.state() == GladiatorEvent.State.RUNNING) {
            sender.sendMessage(Component.text("Participantes vivos: " + e.aliveParticipants().size()));
        }
    }

    private void history(CommandSender sender) {
        plugin.database().dao().recentGladiatorWinners(10).thenAccept(lines -> {
            if (lines.isEmpty()) { sender.sendMessage(plugin.messages().get("gladiator.history.empty")); return; }
            sender.sendMessage(plugin.messages().get("gladiator.history.header"));
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.ZoneId zone = java.time.ZoneId.of(plugin.getConfig().getString("gladiator.schedule.timezone", "America/Sao_Paulo"));
            for (String entry : lines) {
                String[] parts = entry.split("\\|", 2);
                long week;
                try { week = Long.parseLong(parts[0]); } catch (NumberFormatException e) { continue; }
                String winnerId = parts.length > 1 ? parts[1] : null;
                String date = java.time.Instant.ofEpochMilli(week).atZone(zone).format(fmt);
                String tag = "-";
                if (winnerId != null && !winnerId.isBlank() && !winnerId.equals("null")) {
                    try {
                        java.util.UUID uuid = java.util.UUID.fromString(winnerId);
                        tag = plugin.clans().getById(uuid).map(com.bryanmz.betterclans.clan.Clan::tag).orElse("?");
                    } catch (IllegalArgumentException ignored) { tag = "?"; }
                }
                sender.sendMessage(plugin.messages().raw("gladiator.history.line", "week", date, "winner", tag));
            }
        });
    }

    private void startAdmin(CommandSender sender) {
        if (!sender.hasPermission("betterclans.admin.gladiator")) { sender.sendMessage(plugin.messages().get("errors.no-permission")); return; }
        plugin.gladiator().closeSignups();
        plugin.gladiator().startMatch();
    }

    private void setSpawn(CommandSender sender, String[] args) {
        if (!sender.hasPermission("betterclans.admin.gladiator")) { sender.sendMessage(plugin.messages().get("errors.no-permission")); return; }
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (args.length < 2) { sender.sendMessage(Component.text("Uso: /gladiator setspawn <n>")); return; }
        int idx;
        try { idx = Integer.parseInt(args[1]); } catch (NumberFormatException e) { sender.sendMessage(plugin.messages().get("errors.invalid-number")); return; }

        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<?, ?>> raw = plugin.getConfig().getMapList("gladiator.spawns");
        for (Map<?, ?> m : raw) {
            Map<String, Object> entry = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) entry.put(String.valueOf(e.getKey()), e.getValue());
            list.add(entry);
        }
        while (list.size() <= idx) list.add(new LinkedHashMap<>());
        list.set(idx, locToMap(p.getLocation()));
        plugin.getConfig().set("gladiator.spawns", list);
        plugin.saveConfig();
        sender.sendMessage(plugin.messages().get("gladiator.spawn-set", "n", String.valueOf(idx)));
    }

    private void setLobby(CommandSender sender) {
        if (!sender.hasPermission("betterclans.admin.gladiator")) { sender.sendMessage(plugin.messages().get("errors.no-permission")); return; }
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        plugin.getConfig().set("gladiator.lobby", locToMap(p.getLocation()));
        plugin.saveConfig();
        sender.sendMessage(plugin.messages().get("gladiator.lobby-set"));
    }

    private void setReturn(CommandSender sender) {
        if (!sender.hasPermission("betterclans.admin.gladiator")) { sender.sendMessage(plugin.messages().get("errors.no-permission")); return; }
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        plugin.getConfig().set("gladiator.return-spawn", locToMap(p.getLocation()));
        plugin.saveConfig();
        sender.sendMessage(plugin.messages().get("gladiator.return-set"));
    }

    private Map<String, Object> locToMap(Location loc) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("x", loc.getX());
        map.put("y", loc.getY());
        map.put("z", loc.getZ());
        map.put("yaw", (double) loc.getYaw());
        map.put("pitch", (double) loc.getPitch());
        return map;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> base = new ArrayList<>(List.of("join", "leave", "info", "history"));
            if (sender.hasPermission("betterclans.admin.gladiator")) {
                base.addAll(List.of("start", "setspawn", "setlobby", "setreturn"));
            }
            List<String> out = new ArrayList<>();
            for (String s : base) if (s.startsWith(prefix)) out.add(s);
            return out;
        }
        return List.of();
    }
}
