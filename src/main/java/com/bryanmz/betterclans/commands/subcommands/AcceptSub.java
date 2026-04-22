package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class AcceptSub extends AbstractSub {
    public AcceptSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "accept"; }
    @Override public String permission() { return "betterclans.use"; }
}
