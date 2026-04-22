package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import org.bukkit.command.CommandSender;

public final class ListSub extends AbstractSub {

    public ListSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "list"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        var all = plugin.clans().all();
        if (all.isEmpty()) {
            sender.sendMessage(plugin.messages().get("clan.list.empty"));
            return;
        }
        sender.sendMessage(plugin.messages().get("clan.list.header"));
        for (Clan c : all) {
            int size = plugin.clans().membersOf(c.id()).size();
            sender.sendMessage(plugin.messages().raw("clan.list.line",
                    "clan_color", c.tagColor(),
                    "tag", c.tag(),
                    "name", c.name(),
                    "members", String.valueOf(size)));
        }
    }
}
