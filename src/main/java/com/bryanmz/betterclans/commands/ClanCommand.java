package com.bryanmz.betterclans.commands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Router do /clan. Registra todos os subcomandos da spec section 5.1.
 */
public final class ClanCommand implements CommandExecutor, TabCompleter {

    private final BetterClansPlugin plugin;
    private final Map<String, SubCommand> subs = new LinkedHashMap<>();

    public ClanCommand(BetterClansPlugin plugin) {
        this.plugin = plugin;
        register(new CreateSub(plugin));
        register(new InviteSub(plugin));
        register(new AcceptSub(plugin));
        register(new DenySub(plugin));
        register(new LeaveSub(plugin));
        register(new KickSub(plugin));
        register(new PromoteSub(plugin));
        register(new DemoteSub(plugin));
        register(new TransferSub(plugin));
        register(new DisbandSub(plugin));
        register(new AllySub(plugin));
        register(new RivalSub(plugin));
        register(new InfoSub(plugin));
        register(new ListSub(plugin));
        register(new TopSub(plugin));
        register(new ChatSub(plugin));
        register(new AllyChatSub(plugin));
        register(new MotdSub(plugin));
        register(new ColorSub(plugin));
        register(new MenuSub(plugin));
        register(new AdminSub(plugin));
    }

    private void register(SubCommand sub) {
        subs.put(sub.name().toLowerCase(Locale.ROOT), sub);
        for (String alias : sub.aliases()) {
            subs.put(alias.toLowerCase(Locale.ROOT), sub);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof org.bukkit.entity.Player p) {
                plugin.menus().openMain(p);
            } else {
                sendHelp(sender);
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }
        String head = args[0].toLowerCase(Locale.ROOT);
        SubCommand sub = subs.get(head);
        if (sub == null) {
            sender.sendMessage(plugin.messages().get("general.not-implemented"));
            return true;
        }
        if (!sub.permission().isEmpty() && !sender.hasPermission(sub.permission())) {
            sender.sendMessage(plugin.messages().get("errors.no-permission"));
            return true;
        }
        String[] tail = Arrays.copyOfRange(args, 1, args.length);
        sub.execute(sender, tail);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.messages().get("clan.help.header"));
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (SubCommand sub : subs.values()) {
            if (!seen.add(sub.name())) continue;
            if (!sub.permission().isEmpty() && !sender.hasPermission(sub.permission())) continue;
            sender.sendMessage(plugin.messages().raw("clan.help.line", "name", sub.name()));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> out = new ArrayList<>();
            for (Map.Entry<String, SubCommand> e : subs.entrySet()) {
                if (e.getKey().startsWith(prefix)
                        && (e.getValue().permission().isEmpty() || sender.hasPermission(e.getValue().permission()))
                        && !out.contains(e.getValue().name())) {
                    out.add(e.getValue().name());
                }
            }
            return out;
        }
        SubCommand sub = subs.get(args[0].toLowerCase(Locale.ROOT));
        if (sub == null) return List.of();
        return sub.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
