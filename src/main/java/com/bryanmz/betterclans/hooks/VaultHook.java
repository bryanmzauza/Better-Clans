package com.bryanmz.betterclans.hooks;

import com.bryanmz.betterclans.BetterClansPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Integracao com Vault para saldo/cobranca/deposito. Hard-depend.
 */
public final class VaultHook {

    private final BetterClansPlugin plugin;
    private Economy economy;

    public VaultHook(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().severe("Vault nao encontrado. Algumas funcoes (criar cla, apostas) ficarao indisponiveis.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().severe("Nenhum provedor de economia registrado em Vault.");
            return false;
        }
        this.economy = rsp.getProvider();
        plugin.getLogger().info("Economia conectada via Vault: " + economy.getName());
        return true;
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    public Economy economy() {
        return economy;
    }

    public boolean has(OfflinePlayer player, double amount) {
        return economy != null && economy.has(player, amount);
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        return economy != null && economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean deposit(OfflinePlayer player, double amount) {
        return economy != null && economy.depositPlayer(player, amount).transactionSuccess();
    }
}
