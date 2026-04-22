package com.bryanmz.betterclans.menu;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JavaClanListMenu extends JavaMenuBase {

    private List<Clan> cache = List.of();

    public JavaClanListMenu(BetterClansPlugin plugin) { super(plugin); }

    @Override
    public void open(Player player) {
        cache = new ArrayList<>(plugin.clans().all());
        cache.sort(Comparator.comparing(Clan::tag));
        int size = 54;
        Inventory inv = createInventory(player, size, "Clas do servidor");
        int max = Math.min(cache.size(), size - 9);
        for (int i = 0; i < max; i++) {
            Clan c = cache.get(i);
            inv.setItem(i, item(Material.WHITE_BANNER,
                    "[" + c.tag() + "] " + c.name(),
                    List.of(
                            "Nivel " + c.level() + " - " + c.xp() + " XP",
                            "Membros: " + plugin.clans().membersOf(c.id()).size(),
                            "Clique para ver detalhes"
                    )));
        }
        inv.setItem(size - 5, item(Material.ARROW, "Voltar", List.of()));
        inv.setItem(size - 1, item(Material.BARRIER, "Fechar", List.of()));
        player.openInventory(inv);
    }

    @Override
    public void handleClick(Player p, int slot) {
        if (slot < cache.size() && slot < 45) {
            Clan c = cache.get(slot);
            p.closeInventory();
            p.performCommand("clan info " + c.tag());
            return;
        }
        if (slot == 49) { new JavaClanMenu(plugin).open(p); return; }
        if (slot == 53) p.closeInventory();
    }
}
