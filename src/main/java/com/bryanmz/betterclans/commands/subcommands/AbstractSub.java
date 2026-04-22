package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base de todos os subs de /clan: resposta padrao "em breve" ate que a logica seja implementada.
 */
public abstract class AbstractSub implements SubCommand {

    protected final BetterClansPlugin plugin;

    protected AbstractSub(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(plugin.messages().get("general.not-implemented"));
    }

    protected List<String> completePlayers(String prefix) {
        String p = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (var online : Bukkit.getOnlinePlayers()) {
            if (online.getName().toLowerCase(Locale.ROOT).startsWith(p)) out.add(online.getName());
        }
        return out;
    }

    protected List<String> completeTags(String prefix) {
        String p = prefix == null ? "" : prefix.toUpperCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (var c : plugin.clans().all()) {
            if (c.tag().startsWith(p)) out.add(c.tag());
        }
        return out;
    }

    protected List<String> completeClanMembers(java.util.UUID clanId, String prefix) {
        String p = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (var m : plugin.clans().membersOf(clanId)) {
            var off = Bukkit.getOfflinePlayer(m.playerUuid());
            String name = off.getName();
            if (name != null && name.toLowerCase(Locale.ROOT).startsWith(p)) out.add(name);
        }
        return out;
    }

    protected List<String> completeLiteral(String prefix, String... options) {
        String p = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String o : options) if (o.toLowerCase(Locale.ROOT).startsWith(p)) out.add(o);
        return out;
    }
}
