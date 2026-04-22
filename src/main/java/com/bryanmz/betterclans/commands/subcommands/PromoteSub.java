package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class PromoteSub extends AbstractSub {
    public PromoteSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "promote"; }
    @Override public String permission() { return "betterclans.use"; }
}
