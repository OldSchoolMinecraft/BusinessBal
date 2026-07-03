package net.oldschoolminecraft.bbal.taxes;

import net.oldschoolminecraft.bbal.AccountUtility;
import net.oldschoolminecraft.bbal.TaxDayConfig;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;

public class TaxHandler implements Runnable
{
    private TaxDayConfig taxDayConfig;

    public TaxHandler(TaxDayConfig taxDayConfig)
    {
        this.taxDayConfig = taxDayConfig;
    }

    public void run()
    {
        if (!isTaxDay())
            return;

        for (File file : Objects.requireNonNull(AccountUtility.getAccountDir().listFiles()))
        {
            if (file.isDirectory()) continue;
            if (!file.getName().endsWith(".json")) continue;
        }
    }

    private double applyTax(double balance)
    {
        return reduceByPercent(balance, 0.3);
    }

    private double reduceByPercent(double value, double percent)
    {
        return value * (1.0 - percent / 100.0);
    }

    private boolean isTaxDay()
    {
        Calendar cal = Calendar.getInstance();
        String today = getToday();
        return !getLastTaxDay().equals(today) && cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }

    public static String getToday()
    {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH);
    }

    private String getLastTaxDay()
    {
        return String.valueOf(taxDayConfig.getConfigOption("last_tax_day")); // year/month/day 2026/06/02
    }
}
