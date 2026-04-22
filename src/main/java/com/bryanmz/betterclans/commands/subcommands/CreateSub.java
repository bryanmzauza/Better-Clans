package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class CreateSub extends AbstractSub {
    public CreateSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "create"; }
    @Override public String permission() { return "betterclans.create"; }
}
