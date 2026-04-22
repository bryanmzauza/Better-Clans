package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class KickSub extends AbstractSub {
    public KickSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "kick"; }
    @Override public String permission() { return "betterclans.use"; }
}
