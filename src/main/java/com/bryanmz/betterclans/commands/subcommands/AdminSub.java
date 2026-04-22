package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.Locale;

/**
 * /clan admin <reload|delete|setlevel|setmax>
 */
public final class AdminSub extends AbstractSub {

    public AdminSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "admin"; }
    @Override public String permission() { return "betterclans.admin"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Uso: /clan admin <reload|delete|setlevel|setmax>"));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                if (!sender.hasPermission("betterclans.admin.reload")) { sender.sendMessage(plugin.messages().get("errors.no-permission")); return; }
                plugin.reloadConfig();
                plugin.messages().load(plugin.getConfig().getString("language", "pt_BR"));
                sender.sendMessage(plugin.messages().get("general.reloaded"));
            }
            case "delete" -> {
                if (args.length < 2) { sender.sendMessage(Component.text("Uso: /clan admin delete <TAG>")); return; }
                Clan c = plugin.clans().getByTag(args[1]).orElse(null);
                if (c == null) { sender.sendMessage(plugin.messages().get("errors.clan-not-found", "tag", args[1])); return; }
                plugin.clans().disband(c.id()).thenRun(() -> sender.sendMessage(plugin.messages().get("clan.disband.success")));
            }
            case "setlevel" -> {
                if (args.length < 3) { sender.sendMessage(Component.text("Uso: /clan admin setlevel <TAG> <nivel>")); return; }
                Clan c = plugin.clans().getByTag(args[1]).orElse(null);
                if (c == null) { sender.sendMessage(plugin.messages().get("errors.clan-not-found", "tag", args[1])); return; }
                try {
                    int lvl = Integer.parseInt(args[2]);
                    c.setLevel(lvl);
                    plugin.clans().saveClan(c).thenRun(() -> sender.sendMessage(Component.text("Nivel do cla " + c.tag() + " definido para " + lvl)));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.messages().get("errors.invalid-number"));
                }
            }
            case "setmax" -> {
                if (args.length < 2) { sender.sendMessage(Component.text("Uso: /clan admin setmax <n>")); return; }
                try {
                    int n = Integer.parseInt(args[1]);
                    plugin.getConfig().set("clan.max-members", n);
                    plugin.saveConfig();
                    sender.sendMessage(Component.text("max-members atualizado para " + n));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.messages().get("errors.invalid-number"));
                }
            }
            default -> sender.sendMessage(Component.text("Subcomando admin desconhecido: " + args[0]));
        }
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return completeLiteral(args[0], "reload", "delete", "setlevel", "setmax");
        if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (sub.equals("delete") || sub.equals("setlevel")) return completeTags(args[1]);
        }
        return java.util.Collections.emptyList();
    }
}
