package net.oldschoolminecraft.bbal;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class BBalConfig extends Configuration
{
    public BBalConfig(File file)
    {
        super(file);
    }

    public void reload()
    {
        load();
        write();
        save();
    }

    private void write()
    {
        generateConfigOption("currency_symbol", "$");
        generateConfigOption("default_withdraw_limit", 10000);
        generateConfigOption("balance_visibility_default", true);
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null) this.setProperty(key, defaultValue);
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public Object getConfigOption(String key)
    {
        return this.getProperty(key);
    }

    public Object getConfigOption(String key, Object defaultValue)
    {
        Object value = getConfigOption(key);
        if (value == null) value = defaultValue;
        return value;
    }
}
