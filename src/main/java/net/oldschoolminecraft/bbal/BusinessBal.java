package net.oldschoolminecraft.bbal;

import com.earth2me.essentials.Essentials;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BusinessBal extends JavaPlugin
{
    public BBalConfig config;
    public Essentials ess;

    @Override
    public void onEnable()
    {
        config = new BBalConfig(new File(getDataFolder(), "config.yml"));
        ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");


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
