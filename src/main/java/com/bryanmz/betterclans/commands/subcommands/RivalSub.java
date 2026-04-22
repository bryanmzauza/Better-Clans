package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class RivalSub extends AbstractSub {
    public RivalSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "rival"; }
    @Override public String permission() { return "betterclans.use"; }
}
