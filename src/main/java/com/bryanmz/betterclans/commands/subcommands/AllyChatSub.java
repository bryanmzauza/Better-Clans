package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AllyChatSub extends AbstractSub {

    public AllyChatSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "allychat"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        if (plugin.clans().getMember(p.getUniqueId()).isEmpty()) {
            sender.sendMessage(plugin.messages().get("errors.not-in-clan"));
            return;
        }
        String current = plugin.clans().chatMode(p.getUniqueId());
        if ("ally".equals(current)) {
            plugin.clans().setChatMode(p.getUniqueId(), null);
            p.sendMessage(plugin.messages().get("chat.toggled-off"));
        } else {
            plugin.clans().setChatMode(p.getUniqueId(), "ally");
            p.sendMessage(plugin.messages().get("chat.toggled-on", "mode", "ally"));
        }
    }
}
