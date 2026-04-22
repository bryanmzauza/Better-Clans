package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class TopSub extends AbstractSub {

    public TopSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "top"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String metric = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "xp";
        Comparator<Clan> cmp;
        java.util.function.Function<Clan, String> fmt;

        switch (metric) {
            case "kills" -> { cmp = Comparator.comparingInt(Clan::kills).reversed(); fmt = c -> String.valueOf(c.kills()); }
            case "wins", "gladiator" -> { cmp = Comparator.comparingInt(Clan::wins).reversed(); fmt = c -> String.valueOf(c.wins()); }
            case "kd" -> { cmp = Comparator.comparingDouble(Clan::kdRatio).reversed(); fmt = c -> String.format("%.2f", c.kdRatio()); }
            default -> { metric = "xp"; cmp = Comparator.comparingLong(Clan::xp).reversed(); fmt = c -> String.valueOf(c.xp()); }
        }

        List<Clan> sorted = plugin.clans().all().stream().sorted(cmp).limit(10).toList();
        sender.sendMessage(plugin.messages().get("clan.top.header", "metric", metric));
        int pos = 1;
        for (Clan c : sorted) {
            sender.sendMessage(plugin.messages().raw("clan.top.line",
                    "pos", String.valueOf(pos++),
                    "tag", c.tag(),
                    "value", fmt.apply(c)));
        }
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? completeLiteral(args[0], "xp", "kills", "wins", "kd") : java.util.Collections.emptyList();
    }
}
