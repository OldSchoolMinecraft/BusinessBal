package net.oldschoolminecraft.bbal;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class AdminCommands implements CommandExecutor
{
    private BusinessBal plugin;

    public AdminCommands(BusinessBal plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage(ChatColor.DARK_GRAY + "------ " + ChatColor.GOLD + "BusinessBal Admin Commands" + ChatColor.DARK_GRAY + " ------");
            sender.sendMessage(ChatColor.YELLOW + "/bbaladmin reload");
            sender.sendMessage(ChatColor.WHITE + " - Reload the config file.");

            sender.sendMessage(ChatColor.YELLOW + "/bbaladmin create <account_name> <account_owner>");
            sender.sendMessage(ChatColor.WHITE + " - Create a new business account.");

            sender.sendMessage(ChatColor.YELLOW + "/bbaladmin delete <account_name>");
            sender.sendMessage(ChatColor.WHITE + " - Delete a business account.");
            return true;
        }

        if (!sender.hasPermission("bbal.admin"))
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        String subcommand = args.length > 0 ? args[0] : "";

        if (subcommand.equalsIgnoreCase("reload"))
        {
            plugin.config.reload();
            sender.sendMessage(ChatColor.GREEN + "Config reloaded.");
            return true;
        }

        if (subcommand.equalsIgnoreCase("create"))
        {
            if (args.length-1 < 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /create <account_name> <account_owner>");
                return true;
            }

            String accountName = args[1];
            String accountOwner = args[2];

            AccountUtility.BusinessAccount account = new AccountUtility.BusinessAccount(accountName, accountOwner, plugin.config.getInt("default_withdraw_limit", 10000), plugin.config.getBoolean("balance_visibility_default", true));

            try
            {
                AccountUtility.saveAccount(account);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error creating account: " + e.getMessage());
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "' created for: " + ChatColor.GRAY + accountOwner);
            return true;
        }

        if (subcommand.equalsIgnoreCase("delete"))
        {
            if (args.length-1 < 1)
            {
                sender.sendMessage(ChatColor.RED + "Usage: /delete <account_name>");
                return true;
            }

            String accountName = args[1];
            if (!AccountUtility.getAccountFile(accountName).exists())
            {
                sender.sendMessage(ChatColor.RED + "Account '" + ChatColor.GRAY + accountName + ChatColor.RED + "' does not exist.");
                return true;
            }

            if (AccountUtility.getAccountFile(accountName).delete())
            {
                sender.sendMessage(ChatColor.GREEN + "Account '" + ChatColor.GRAY + accountName + ChatColor.GREEN + "' deleted.");
            } else {
                sender.sendMessage(ChatColor.RED + "Error deleting account '" + ChatColor.GRAY + accountName + ChatColor.RED + "'.");
            }
            return true;
        }

        return false;
    }
}
