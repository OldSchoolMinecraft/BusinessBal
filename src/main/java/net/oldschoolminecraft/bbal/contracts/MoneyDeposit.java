package net.oldschoolminecraft.bbal.contracts;

import org.bukkit.entity.Player;

public class MoneyDeposit implements Deposit
{
    private final String player;
    private final double amount;

    private boolean deposited;

    public MoneyDeposit(String player, double amount)
    {
        this.player = player;
        this.amount = amount;
    }

    @Override
    public boolean isSatisfied()
    {
        return deposited;
    }

    @Override
    public void deposit(Player player)
    {
        // withdraw from Essentials

        deposited = true;
    }

    @Override
    public void refund(Player player)
    {
        // return money
    }

}