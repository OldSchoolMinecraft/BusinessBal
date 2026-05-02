package net.oldschoolminecraft.bbal;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAccountProvider implements AccountInterface
{
    private final Economy economy;

    public VaultAccountProvider(Plugin plugin)
    {
        this.economy = setupEconomy(plugin);
        if (this.economy == null)
            throw new IllegalStateException("Vault economy not found");
    }

    private Economy setupEconomy(Plugin plugin)
    {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null)
            return null;

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        return (rsp != null) ? rsp.getProvider() : null;
    }

    @Override
    public double getMoney(String username)
    {
        return economy.getBalance(username);
    }

    @Override
    public void setMoney(String username, double balance)
    {
        double current = economy.getBalance(username);
        double delta = balance - current;

        if (delta > 0)
            economy.depositPlayer(username, delta);
        else if (delta < 0)
            economy.withdrawPlayer(username, -delta);
    }

    @Override
    public void deposit(String username, double amount)
    {
        if (amount <= 0) return;

        EconomyResponse res = economy.depositPlayer(username, amount);
        if (!res.transactionSuccess())
            throw new RuntimeException("Deposit failed: " + res.errorMessage);
    }

    @Override
    public boolean withdraw(String username, double amount)
    {
        if (amount <= 0) return false;

        EconomyResponse res = economy.withdrawPlayer(username, amount);
        return res.transactionSuccess();
    }

    @Override
    public boolean has(String username, double amount)
    {
        return economy.has(username, amount);
    }

    @Override
    public String format(double amount)
    {
        return economy.format(amount);
    }
}