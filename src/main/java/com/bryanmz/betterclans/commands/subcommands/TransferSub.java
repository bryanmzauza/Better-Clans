package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class TransferSub extends AbstractSub {
    public TransferSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "transfer"; }
    @Override public String permission() { return "betterclans.use"; }
}
