package net.oldschoolminecraft.bbal.contracts;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemDeposit implements Deposit
{
    private final String player;

    private final ItemStack item;

    private boolean deposited;

    public ItemDeposit(String player, ItemStack item)
    {
        this.player = player;
        this.item = item;
    }

    @Override
    public boolean isSatisfied()
    {
        return deposited;
    }

    @Override
    public void deposit(Player player)
    {
        // remove item from inventory

        deposited = true;
    }

    @Override
    public void refund(Player player)
    {
        // return item
    }
}
