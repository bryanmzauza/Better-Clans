package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class DemoteSub extends AbstractSub {
    public DemoteSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "demote"; }
    @Override public String permission() { return "betterclans.use"; }
}
