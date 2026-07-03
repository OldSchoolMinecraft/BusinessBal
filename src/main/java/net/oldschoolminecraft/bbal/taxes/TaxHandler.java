package net.oldschoolminecraft.bbal.taxes;

import com.earth2me.essentials.api.Economy;
import com.google.gson.Gson;
import net.oldschoolminecraft.bbal.AccountUtility;
import net.oldschoolminecraft.bbal.TaxDayConfig;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Objects;

public class TaxHandler implements Runnable
{
    private static final Gson gson = new Gson();

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

            try (FileReader reader = new FileReader(file))
            {
                AccountUtility.BusinessAccount account = gson.fromJson(reader, AccountUtility.BusinessAccount.class);
                TaxReport report = account.tax(0.3);
                //TODO: save tax report to folder specific to the account in question
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        // taxes ran, we need to record that in the config
        taxDayConfig.setProperty("last_tax_day", getToday());
        taxDayConfig.save();
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
