package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class DenySub extends AbstractSub {
    public DenySub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "deny"; }
    @Override public String permission() { return "betterclans.use"; }
}
