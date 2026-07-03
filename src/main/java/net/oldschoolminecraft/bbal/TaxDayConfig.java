package net.oldschoolminecraft.bbal;

import net.oldschoolminecraft.bbal.taxes.TaxHandler;
import org.bukkit.util.config.Configuration;

import java.io.File;

public class TaxDayConfig extends Configuration
{
    public TaxDayConfig(File file)
    {
        super(file);
        reload();
    }

    public void reload()
    {
        load();
        write();
        save();
    }

    private void write()
    {
        generateConfigOption("last_tax_day", TaxHandler.getToday());
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
