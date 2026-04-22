package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class InfoSub extends AbstractSub {
    public InfoSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "info"; }
    @Override public String permission() { return "betterclans.use"; }
}
