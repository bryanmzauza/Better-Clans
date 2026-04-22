package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class ListSub extends AbstractSub {
    public ListSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "list"; }
    @Override public String permission() { return "betterclans.use"; }
}
