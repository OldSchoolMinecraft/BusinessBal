package net.oldschoolminecraft.bbal;

import net.milkbowl.vault.Vault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BusinessBal extends JavaPlugin
{
    public BBalConfig config;
    private Vault vault;
    private VaultAccountProvider vaultAccountProvider;

    @Override
    public void onEnable()
    {
        config = new BBalConfig(new File(getDataFolder(), "config.yml"));
        vault = (Vault) getServer().getPluginManager().getPlugin("Vault");

        if (vault == null)
        {
            System.err.println("BusinessBal failed to load! Vault handle is null.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        vaultAccountProvider = new VaultAccountProvider(vault);

        getCommand("bbaladmin").setExecutor(new AdminCommands(this));
        getCommand("bbal").setExecutor(new UserCommands(this));

        System.out.println("BusinessBal enabled");
    }

    public AccountInterface getEcoInterface()
    {
        return vaultAccountProvider;
    }

    @Override
    public void onDisable()
    {
        System.out.println("BusinessBal disabled");
    }
}
