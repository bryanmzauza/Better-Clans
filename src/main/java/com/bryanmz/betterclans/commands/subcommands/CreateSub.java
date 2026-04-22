package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.util.TagValidator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CreateSub extends AbstractSub {

    public CreateSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "create"; }
    @Override public String permission() { return "betterclans.create"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(plugin.messages().get("general.player-only"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.messages().get("general.usage", "usage", "/clan create <TAG> <nome>"));
            return;
        }
        String tag = args[0].toUpperCase();
        StringBuilder nb = new StringBuilder();
        for (int i = 1; i < args.length; i++) { if (i > 1) nb.append(' '); nb.append(args[i]); }
        String name = nb.toString();

        int maxName = plugin.getConfig().getInt("clan.name.max-length", 32);
        if (name.length() > maxName) {
            sender.sendMessage(plugin.messages().get("clan.name.too-long", "max", String.valueOf(maxName)));
            return;
        }

        var res = plugin.tagValidator().validate(tag);
        if (res == TagValidator.Result.INVALID_FORMAT) { sender.sendMessage(plugin.messages().get("clan.tag.invalid")); return; }
        if (res == TagValidator.Result.RESERVED) { sender.sendMessage(plugin.messages().get("clan.tag.reserved")); return; }

        if (plugin.clans().getMember(p.getUniqueId()).isPresent()) {
            sender.sendMessage(plugin.messages().get("errors.already-in-clan"));
            return;
        }
        if (plugin.clans().tagExists(tag)) { sender.sendMessage(plugin.messages().get("clan.tag.taken")); return; }
        if (plugin.clans().nameExists(name)) { sender.sendMessage(plugin.messages().get("clan.name.taken")); return; }

        double cost = plugin.getConfig().getDouble("clan.creation-cost", 10000);
        if (cost > 0 && plugin.vault().hasEconomy()) {
            if (!plugin.vault().has(p, cost)) {
                sender.sendMessage(plugin.messages().get("clan.create.not-enough-money", "cost", String.valueOf((long) cost)));
                return;
            }
            plugin.vault().withdraw(p, cost);
        }

        plugin.clans().create(tag, name, p.getUniqueId()).thenAccept((Clan c) -> {
            p.sendMessage(plugin.messages().get("clan.create.success", "tag", c.tag()));
            plugin.nametag().apply(p);
        });
    }
}
