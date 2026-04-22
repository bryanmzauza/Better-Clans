package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class MotdSub extends AbstractSub {
    public MotdSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "motd"; }
    @Override public String permission() { return "betterclans.use"; }
}
