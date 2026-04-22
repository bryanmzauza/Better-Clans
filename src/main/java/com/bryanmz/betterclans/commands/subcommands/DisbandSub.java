package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class DisbandSub extends AbstractSub {
    public DisbandSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "disband"; }
    @Override public String permission() { return "betterclans.use"; }
}
