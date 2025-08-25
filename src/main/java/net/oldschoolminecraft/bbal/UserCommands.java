package net.oldschoolminecraft.bbal;

import net.oldschoolminecraft.bbal.ex.InsufficientFundsException;
import net.oldschoolminecraft.bbal.ex.UnauthorizedTransactionException;
import net.oldschoolminecraft.bbal.ex.WithdrawLimitExceededException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class UserCommands implements CommandExecutor
{
    private BusinessBal plugin;

    public UserCommands(BusinessBal plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage(ChatColor.DARK_GRAY + "------ " + ChatColor.GOLD + "BusinessBal User Commands" + ChatColor.DARK_GRAY + " ------");
            sender.sendMessage(ChatColor.YELLOW + "/bbal view <account_name>");
            sender.sendMessage(ChatColor.WHITE + " - View the balance of an account.");

            sender.sendMessage(ChatColor.YELLOW + "/bbal deposit <account_name> <amount>");
            sender.sendMessage(ChatColor.WHITE + " - Deposit money into account.");

            sender.sendMessage(ChatColor.YELLOW + "/bbal withdraw <account_name> <amount>");
            sender.sendMessage(ChatColor.WHITE + " - Withdraw money from account.");

            // account owner commands
            sender.sendMessage(ChatColor.YELLOW + "/bbal addtrustee <account_name> <player_name>");
            sender.sendMessage(ChatColor.WHITE + " - Add a trustee to account.");

            sender.sendMessage(ChatColor.YELLOW + "/bbal removetrustee <account_name> <player_name>");
            sender.sendMessage(ChatColor.WHITE + " - Remove a trustee from account.");

            sender.sendMessage(ChatColor.YELLOW + "/bbal setwithdrawlimit <account_name> <amount>");
            sender.sendMessage(ChatColor.WHITE + " - Set the withdraw limit for account.");

            sender.sendMessage(ChatColor.YELLOW + "/bbal allowviewingbalance <account_name> <true|false>");
            sender.sendMessage(ChatColor.WHITE + " - Toggle balance visibility for trustees.");

            sender.sendMessage(ChatColor.DARK_GRAY + "-------------------------------------");
            return true;
        }

        if (!sender.hasPermission("bbal.user"))
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        String subcommand = args.length > 0 ? args[0] : "";

        // /bbal view <account_name>
        if (subcommand.equalsIgnoreCase("view"))
        {
            if (args.length-1 < 1)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /bbal view <account_name>");
                return true;
            }

            String accountName = args[1];

            AccountUtility.BusinessAccount account;
            try
            {
                account = AccountUtility.loadAccount(accountName);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error loading account: " + e.getMessage());
                return true;
            }

            if (account == null)
            {
                sender.sendMessage(ChatColor.RED + "Account '" + ChatColor.GRAY + accountName + ChatColor.RED + "' does not exist.");
                return true;
            }

            if (!account.trustees.contains(sender.getName()) && !sender.getName().equalsIgnoreCase(account.owner))
            {
                sender.sendMessage(ChatColor.RED + "You are not a trustee of this account.");
                return true;
            }

            if (!account.canTrusteesViewBalance && !sender.getName().equalsIgnoreCase(account.owner))
            {
                sender.sendMessage(ChatColor.RED + "Balance viewing on this account is disabled for trustees.");
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "' balance: " + ChatColor.YELLOW + "$" + account.balance);
            return true;
        }

        // /bbal deposit <account_name> <amount>
        if (subcommand.equalsIgnoreCase("deposit"))
        {
            if (args.length-1 < 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /bbal deposit <account_name> <amount>");
                return true;
            }

            String accountName = args[1];
            double amount;
            try
            {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[2]);
                return true;
            }

            if (amount <= 0)
            {
                sender.sendMessage(ChatColor.RED + "Amount must be greater than zero.");
                return true;
            }

            AccountUtility.BusinessAccount account;
            try
            {
                account = AccountUtility.loadAccount(accountName);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error loading account: " + e.getMessage());
                return true;
            }

            if (account == null)
            {
                sender.sendMessage(ChatColor.RED + "Account '" + ChatColor.GRAY + accountName + ChatColor.RED + "' does not exist.");
                return true;
            }

            if (!account.trustees.contains(sender.getName()) && !sender.getName().equalsIgnoreCase(account.owner))
            {
                sender.sendMessage(ChatColor.RED + "You are not a trustee of this account.");
                return true;
            }

            String currencySymbol = plugin.config.getString("currency_symbol", "$");

            // check if their essentials balance is sufficient
            if (plugin.ess != null)
            {
                double userBalance = plugin.ess.getUser(sender.getName()).getMoney();
                if (userBalance < amount)
                {
                    sender.sendMessage(ChatColor.RED + "Insufficient balance for deposit: " + ChatColor.YELLOW + currencySymbol + userBalance);
                    return true;
                }

                // withdraw from essentials
                plugin.ess.getUser(sender.getName()).setMoney(userBalance - amount);
            }

            account.balance += amount;

            try
            {
                AccountUtility.saveAccount(account);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error saving account: " + e.getMessage());
                return true;
            }

            if (account.canTrusteesViewBalance || sender.getName().equalsIgnoreCase(account.owner)) {
                sender.sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.YELLOW + currencySymbol + amount + ChatColor.GREEN + " to account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "'. New balance: " + ChatColor.YELLOW + currencySymbol + account.balance);
            } else {
                sender.sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.YELLOW + currencySymbol + amount + ChatColor.GREEN + " to account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "'.");
            }

            return true;
        }

        // /bbal withdraw <account_name> <amount>
        if (subcommand.equalsIgnoreCase("withdraw"))
        {
            if (args.length - 1 < 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /bbal withdraw <account_name> <amount>");
                return true;
            }

            String accountName = args[1];
            double amount;
            try
            {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e)
            {
                sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[2]);
                return true;
            }

            if (amount <= 0)
            {
                sender.sendMessage(ChatColor.RED + "Amount must be greater than zero.");
                return true;
            }

            AccountUtility.BusinessAccount account;
            try
            {
                account = AccountUtility.loadAccount(accountName);
            } catch (Exception e)
            {
                sender.sendMessage(ChatColor.RED + "Error loading account: " + e.getMessage());
                return true;
            }

            if (account == null)
            {
                sender.sendMessage(ChatColor.RED + "Account '" + ChatColor.GRAY + accountName + ChatColor.RED + "' does not exist.");
                return true;
            }

            if (!account.trustees.contains(sender.getName()) && !sender.getName().equalsIgnoreCase(account.owner))
            {
                sender.sendMessage(ChatColor.RED + "You are not a trustee of this account.");
                return true;
            }

            String currencySymbol = plugin.config.getString("currency_symbol", "$");

            try
            {
                account.withdraw(sender.getName(), amount);
                AccountUtility.saveAccount(account);

                // deposit to essentials
                if (plugin.ess != null)
                {
                    double userBalance = plugin.ess.getUser(sender.getName()).getMoney();
                    plugin.ess.getUser(sender.getName()).setMoney(userBalance + amount);
                }

                if (account.canTrusteesViewBalance || sender.getName().equalsIgnoreCase(account.owner)) {
                    sender.sendMessage(ChatColor.GREEN + "Withdrew " + ChatColor.YELLOW + currencySymbol + amount + ChatColor.GREEN + " from account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "'. New balance: " + ChatColor.YELLOW + currencySymbol + account.balance);
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Withdrew " + ChatColor.YELLOW + currencySymbol + amount + ChatColor.GREEN + " from account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "'.");
                }
            } catch (InsufficientFundsException e) {
                if (account.canTrusteesViewBalance || sender.getName().equalsIgnoreCase(account.owner)) {
                    sender.sendMessage(ChatColor.RED + "Insufficient funds in account. Current balance: " + ChatColor.YELLOW + currencySymbol + account.balance);
                } else {
                    sender.sendMessage(ChatColor.RED + "Insufficient funds in account.");
                }
            } catch (WithdrawLimitExceededException e) {
                sender.sendMessage(ChatColor.RED + "Withdraw amount exceeds the account's withdraw limit of " + ChatColor.YELLOW + currencySymbol + account.withdrawLimit);
            } catch (UnauthorizedTransactionException e) {
                sender.sendMessage(ChatColor.RED + "You must be a trustee to withdraw from this account.");
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error saving account: " + e.getMessage());
            }

            return true;
        }

        // /bbal addtrustee <account_name> <player_name>
        if (subcommand.equalsIgnoreCase("addtrustee"))
        {
            if (args.length-1 < 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /bbal addtrustee <account_name> <player_name>");
                return true;
            }

            String accountName = args[1];
            String playerName = args[2];

            AccountUtility.BusinessAccount account;
            try
            {
                account = AccountUtility.loadAccount(accountName);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error loading account: " + e.getMessage());
                return true;
            }

            if (account == null)
            {
                sender.sendMessage(ChatColor.RED + "Account '" + ChatColor.GRAY + accountName + ChatColor.RED + "' does not exist.");
                return true;
            }

            if (!sender.getName().equalsIgnoreCase(account.owner))
            {
                sender.sendMessage(ChatColor.RED + "Only the account owner can add trustees.");
                return true;
            }

            if (account.trustees.contains(playerName))
            {
                sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.GRAY + playerName + ChatColor.RED + "' is already a trustee of this account.");
                return true;
            }

            account.addTrustee(playerName);

            try
            {
                AccountUtility.saveAccount(account);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error saving account: " + e.getMessage());
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Added '" + ChatColor.GRAY + playerName + ChatColor.GREEN + "' as a trustee to account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "'.");
            return true;
        }

        // /bbal removetrustee <account_name> <player_name>
        if (subcommand.equalsIgnoreCase("removetrustee"))
        {
            if (args.length-1 < 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /bbal removetrustee <account_name> <player_name>");
                return true;
            }

            String accountName = args[1];
            String playerName = args[2];

            AccountUtility.BusinessAccount account;
            try
            {
                account = AccountUtility.loadAccount(accountName);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error loading account: " + e.getMessage());
                return true;
            }

            if (account == null)
            {
                sender.sendMessage(ChatColor.RED + "Account '" + ChatColor.GRAY + accountName + ChatColor.RED + "' does not exist.");
                return true;
            }

            if (!sender.getName().equalsIgnoreCase(account.owner))
            {
                sender.sendMessage(ChatColor.RED + "Only account owners can remove trustees.");
                return true;
            }

            if (!account.trustees.contains(playerName))
            {
                sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.GRAY + playerName + ChatColor.RED + "' is not a trustee of this account.");
                return true;
            }

            account.removeTrustee(playerName);

            try
            {
                AccountUtility.saveAccount(account);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error saving account: " + e.getMessage());
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Removed '" + ChatColor.GRAY + playerName + ChatColor.GREEN + "' as a trustee from account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "'.");
            return true;
        }

        if (subcommand.equalsIgnoreCase("setwithdrawlimit"))
        {
            if (args.length-1 < 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /bbal setwithdrawlimit <account_name> <amount>");
                return true;
            }

            String accountName = args[1];
            double amount;
            try
            {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[2]);
                return true;
            }

            if (amount <= 0)
            {
                sender.sendMessage(ChatColor.RED + "Amount must be greater than zero.");
                return true;
            }

            AccountUtility.BusinessAccount account;
            try
            {
                account = AccountUtility.loadAccount(accountName);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error loading account: " + e.getMessage());
                return true;
            }

            if (account == null)
            {
                sender.sendMessage(ChatColor.RED + "Account '" + ChatColor.GRAY + accountName + ChatColor.RED + "' does not exist.");
                return true;
            }

            if (!sender.getName().equalsIgnoreCase(account.owner))
            {
                sender.sendMessage(ChatColor.RED + "Only account owners can change the withdrawal limit.");
                return true;
            }

            account.withdrawLimit = amount;

            try
            {
                AccountUtility.saveAccount(account);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error saving account: " + e.getMessage());
                return true;
            }

            String currencySymbol = plugin.config.getString("currency_symbol", "$");
            sender.sendMessage(ChatColor.GREEN + "Set withdraw limit of account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "' to: " + ChatColor.YELLOW + currencySymbol + amount);
            return true;
        }

        // /bbal allowviewingbalance <account_name> <true|false>
        if (subcommand.equalsIgnoreCase("allowviewingbalance"))
        {
            if (args.length-1 < 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /bbal allowviewingbalance <account_name> <true|false>");
                return true;
            }

            String accountName = args[1];
            boolean allow;
            try
            {
                allow = Boolean.parseBoolean(args[2]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid value: " + args[2] + ". Use true or false.");
                return true;
            }

            AccountUtility.BusinessAccount account;
            try
            {
                account = AccountUtility.loadAccount(accountName);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error loading account: " + e.getMessage());
                return true;
            }

            if (account == null)
            {
                sender.sendMessage(ChatColor.RED + "Account '" + ChatColor.GRAY + accountName + ChatColor.RED + "' does not exist.");
                return true;
            }

            if (!sender.getName().equalsIgnoreCase(account.owner))
            {
                sender.sendMessage(ChatColor.RED + "Only account owners can change balance viewing permissions.");
                return true;
            }

            account.canTrusteesViewBalance = allow;

            try
            {
                AccountUtility.saveAccount(account);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error saving account: " + e.getMessage());
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Set balance viewing permission for trustees of account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "' to: " + ChatColor.YELLOW + (allow ? "ALLOWED" : "DENIED"));
            return true;
        }

        return false;
    }
}
