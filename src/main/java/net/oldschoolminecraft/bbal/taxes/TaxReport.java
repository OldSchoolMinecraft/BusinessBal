package net.oldschoolminecraft.bbal.taxes;

import java.util.UUID;

public class TaxReport
{
    public String reportID;
    public String accountName;
    public long reportTimestamp;
    public double balanceDeducted;

    public TaxReport(String accountName, double balanceDeducted)
    {
        this.accountName = accountName;
        this.balanceDeducted = balanceDeducted;
        String uuid = UUID.randomUUID().toString();
        this.reportID = uuid.substring(0, 4) + uuid.substring(uuid.length() - 4);
        this.reportTimestamp = System.currentTimeMillis();
    }
}
