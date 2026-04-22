package com.bryanmz.betterclans.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Contrato para um subcomando de /clan.
 */
public interface SubCommand {

    String name();

    default List<String> aliases() {
        return Collections.emptyList();
    }

    String permission();

    void execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
