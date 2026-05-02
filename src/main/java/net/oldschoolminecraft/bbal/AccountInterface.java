package net.oldschoolminecraft.bbal;

public interface AccountInterface
{
    double getMoney(String username);
    void setMoney(String username, double balance);
    void deposit(String username, double amount);
    boolean withdraw(String username, double amount);
    boolean has(String username, double amount);
    String format(double amount);
}