package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class InviteSub extends AbstractSub {
    public InviteSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "invite"; }
    @Override public String permission() { return "betterclans.use"; }
}
