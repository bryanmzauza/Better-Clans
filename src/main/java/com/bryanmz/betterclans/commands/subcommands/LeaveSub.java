package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.ClanMember;
import com.bryanmz.betterclans.clan.ClanRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LeaveSub extends AbstractSub {

    public LeaveSub(BetterClansPlugin plugin) { super(plugin); }

    @Override public String name() { return "leave"; }
    @Override public String permission() { return "betterclans.use"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(plugin.messages().get("general.player-only")); return; }
        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null) { sender.sendMessage(plugin.messages().get("errors.not-in-clan")); return; }
        if (plugin.gladiator().isParticipant(p.getUniqueId())) {
            sender.sendMessage(plugin.messages().get("errors.in-gladiator"));
            return;
        }
        if (me.role() == ClanRole.LEADER) {
            sender.sendMessage(plugin.messages().get("clan.leave.leader-must-transfer"));
            return;
        }
        plugin.clans().removeMember(p.getUniqueId()).thenRun(() -> {
            p.sendMessage(plugin.messages().get("clan.leave.success"));
            plugin.nametag().clear(p);
        });
    }
}
