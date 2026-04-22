package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class TopSub extends AbstractSub {
    public TopSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "top"; }
    @Override public String permission() { return "betterclans.use"; }
}
