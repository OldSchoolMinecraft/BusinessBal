package net.oldschoolminecraft.bbal;

import org.bukkit.plugin.java.JavaPlugin;

public class BusinessBal extends JavaPlugin
{
    public BBalConfig config;

    @Override
    public void onEnable()
    {
        getCommand("bbaladmin").setExecutor(new AdminCommands(this));
        getCommand("bbal").setExecutor(new UserCommands(this));

        System.out.println("BusinessBal enabled");
    }

    @Override
    public void onDisable()
    {
        System.out.println("BusinessBal disabled");
    }
}
