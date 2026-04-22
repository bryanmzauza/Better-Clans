package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import org.bukkit.command.CommandSender;

import java.util.Locale;

/**
 * /clan admin <reload|delete|setlevel|setmax|migrate>
 */
public final class AdminSub extends AbstractSub {

    public AdminSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "admin"; }
    @Override public String permission() { return "betterclans.admin"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.messages().get("general.not-implemented"));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                if (!sender.hasPermission("betterclans.admin.reload")) {
                    sender.sendMessage(plugin.messages().get("errors.no-permission"));
                    return;
                }
                plugin.reloadConfig();
                plugin.messages().load(plugin.getConfig().getString("language", "pt_BR"));
                sender.sendMessage(plugin.messages().get("general.reloaded"));
            }
            default -> sender.sendMessage(plugin.messages().get("general.not-implemented"));
        }
    }
}
