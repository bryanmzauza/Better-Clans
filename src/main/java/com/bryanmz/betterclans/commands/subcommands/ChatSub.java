package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

import java.util.List;

public final class ChatSub extends AbstractSub {
    public ChatSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "chat"; }
    @Override public List<String> aliases() { return List.of("c"); }
    @Override public String permission() { return "betterclans.chat"; }
}
