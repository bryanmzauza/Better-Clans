package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ChatSub extends AbstractSub {

    public ChatSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "chat"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (plugin.clans().getMember(p.getUniqueId()).isEmpty()) {
            sender.sendMessage(plugin.messages().get("errors.not-in-clan"));
            return;
        }
        String current = plugin.clans().chatMode(p.getUniqueId());
        if ("clan".equals(current)) {
            plugin.clans().setChatMode(p.getUniqueId(), null);
            p.sendMessage(plugin.messages().get("chat.toggled-off"));
        } else {
            plugin.clans().setChatMode(p.getUniqueId(), "clan");
            p.sendMessage(plugin.messages().get("chat.toggled-on", "mode", "clan"));
        }
    }
}
