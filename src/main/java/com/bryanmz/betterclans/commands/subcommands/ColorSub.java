package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

public final class ColorSub extends AbstractSub {
    public ColorSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "color"; }
    @Override public String permission() { return "betterclans.use"; }
}
