package com.bryanmz.betterclans.menu;

import com.bryanmz.betterclans.BetterClansPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Menu base: cria ItemStacks e registra o inventario no MenuListener.
 */
abstract class JavaMenuBase {

    protected final BetterClansPlugin plugin;

    protected JavaMenuBase(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void open(Player player);

    public abstract void handleClick(Player player, int slot);

    protected Inventory createInventory(Player holder, int size, String title) {
        Inventory inv = Bukkit.createInventory(holder, size, Component.text(title, NamedTextColor.DARK_GREEN));
        MenuListener.register(holder.getUniqueId(), this);
        return inv;
    }

    protected ItemStack item(Material mat, String name, List<String> lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name, NamedTextColor.YELLOW));
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream().map(l -> (Component) Component.text(l, NamedTextColor.GRAY)).toList());
            }
            it.setItemMeta(meta);
        }
        return it;
    }
}
