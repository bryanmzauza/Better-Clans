package com.bryanmz.betterclans.menu;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public final class JavaClanMenu extends JavaMenuBase {

    public JavaClanMenu(BetterClansPlugin plugin) { super(plugin); }

    @Override
    public void open(Player player) {
        Inventory inv = createInventory(player, 27, "BetterClans");
        ClanMember me = plugin.clans().getMember(player.getUniqueId()).orElse(null);

        if (me == null) {
            inv.setItem(11, item(Material.BOOK, "Lista de clas",
                    List.of("Ver todos os clas do servidor.")));
            inv.setItem(13, item(Material.GOLD_INGOT, "Ranking (Top 10)",
                    List.of("Melhores clas por XP.")));
            inv.setItem(15, item(Material.BARRIER, "Fechar", List.of()));
        } else {
            Clan c = plugin.clans().getById(me.clanId()).orElse(null);
            if (c == null) { player.closeInventory(); return; }
            inv.setItem(4, item(Material.WHITE_BANNER,
                    "[" + c.tag() + "] " + c.name(),
                    List.of(
                            "Nivel: " + c.level() + " (" + c.xp() + " XP)",
                            "K/D: " + c.kills() + "/" + c.deaths() + " (" + String.format("%.2f", c.kdRatio()) + ")",
                            "Wins no Gladiador: " + c.wins(),
                            "Membros: " + plugin.clans().membersOf(c.id()).size(),
                            "Seu cargo: " + me.role().name()
                    )));
            inv.setItem(10, item(Material.PAPER, "Informacoes", List.of("/clan info")));
            inv.setItem(11, item(Material.BOOK, "Lista de clas", List.of()));
            inv.setItem(12, item(Material.GOLD_INGOT, "Ranking (Top 10)", List.of()));
            inv.setItem(14, item(Material.WRITABLE_BOOK, "Chat do cla", List.of("Alterna o modo de chat.")));
            inv.setItem(15, item(Material.FEATHER, "Chat da alianca", List.of("Alterna o modo de chat aliado.")));
            inv.setItem(16, item(Material.IRON_SWORD, "Gladiador",
                    List.of("Clique: /gladiator join", "Tecla E: /gladiator info")));
            inv.setItem(22, item(Material.RED_WOOL, "Sair do cla", List.of("/clan leave")));
            inv.setItem(26, item(Material.BARRIER, "Fechar", List.of()));
        }
        player.openInventory(inv);
    }

    @Override
    public void handleClick(Player p, int slot) {
        ClanMember me = plugin.clans().getMember(p.getUniqueId()).orElse(null);
        if (me == null) {
            switch (slot) {
                case 11 -> new JavaClanListMenu(plugin).open(p);
                case 13 -> new JavaTopMenu(plugin).open(p);
                case 15 -> p.closeInventory();
            }
            return;
        }
        switch (slot) {
            case 10 -> { p.closeInventory(); p.performCommand("clan info"); }
            case 11 -> new JavaClanListMenu(plugin).open(p);
            case 12 -> new JavaTopMenu(plugin).open(p);
            case 14 -> { p.closeInventory(); p.performCommand("clan chat"); }
            case 15 -> { p.closeInventory(); p.performCommand("clan allychat"); }
            case 16 -> { p.closeInventory(); p.performCommand("gladiator join"); }
            case 22 -> { p.closeInventory(); p.performCommand("clan leave"); }
            case 26 -> p.closeInventory();
        }
    }
}
