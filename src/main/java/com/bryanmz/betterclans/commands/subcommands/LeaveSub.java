package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class LeaveSub extends AbstractSub {
    public LeaveSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "leave"; }
    @Override public String permission() { return "betterclans.use"; }
}
