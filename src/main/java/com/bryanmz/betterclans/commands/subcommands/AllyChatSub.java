package com.bryanmz.betterclans.commands.subcommands;

import com.bryanmz.betterclans.BetterClansPlugin;

import java.util.List;

public final class AllyChatSub extends AbstractSub {
    public AllyChatSub(BetterClansPlugin plugin) { super(plugin); }
    @Override public String name() { return "ally-chat"; }
    @Override public List<String> aliases() { return List.of("allychat"); }
    @Override public String permission() { return "betterclans.chat.ally"; }
}
