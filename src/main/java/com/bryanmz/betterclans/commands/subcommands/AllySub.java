package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class AllySub extends AbstractSub {
    public AllySub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "ally"; }
    @Override public String permission() { return "betterclans.use"; }
}
