package com.bryanmz.betterclans.menu;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Comparator;
import java.util.List;

public final class JavaTopMenu extends JavaMenuBase {

    private List<Clan> cache = List.of();

    public JavaTopMenu(BetterClansPlugin plugin) { super(plugin); }

    @Override
    public void open(Player player) {
        cache = plugin.clans().all().stream()
                .sorted(Comparator.comparingLong(Clan::xp).reversed())
                .limit(10)
                .toList();
        Inventory inv = createInventory(player, 27, "Ranking - XP");
        Material[] medals = { Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.COPPER_BLOCK };
        int[] slots = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21 };
        for (int i = 0; i < cache.size() && i < slots.length; i++) {
            Clan c = cache.get(i);
            Material m = i < 3 ? medals[i] : Material.BOOK;
            inv.setItem(slots[i], item(m,
                    "#" + (i + 1) + " [" + c.tag() + "] " + c.name(),
                    List.of(
                            "XP: " + c.xp(),
                            "Kills: " + c.kills(),
                            "K/D: " + String.format("%.2f", c.kdRatio()),
                            "Wins: " + c.wins()
                    )));
        }
        inv.setItem(22, item(Material.ARROW, "Voltar", List.of()));
        inv.setItem(26, item(Material.BARRIER, "Fechar", List.of()));
        player.openInventory(inv);
    }

    @Override
    public void handleClick(Player p, int slot) {
        if (slot == 22) { new JavaClanMenu(plugin).open(p); return; }
        if (slot == 26) p.closeInventory();
    }
}
