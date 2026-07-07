package net.oldschoolminecraft.bbal.contracts;

import org.bukkit.entity.Player;

public interface Deposit
{
    boolean isSatisfied();
    void deposit(Player player);
    void refund(Player player);
}
