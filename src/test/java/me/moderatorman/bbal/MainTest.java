package me.moderatorman.bbal;

public class MainTest
{
    public static void main(String[] args)
    {
        System.out.println("$500,000 - " + applyTaxDelta(500_000));
        System.out.println("$1,000,000 - " + applyTaxDelta(1_000_000));
        System.out.println("$2,500,000 - " + applyTaxDelta(2_500_000));
    }

    private static double applyTaxDelta(double balance)
    {
        double newBalance = reduceByPercent(balance, 0.3);
        return balance - newBalance;
    }

    private static double reduceByPercent(double value, double percent)
    {
        return value * (1.0 - percent / 100.0);
    }
}
