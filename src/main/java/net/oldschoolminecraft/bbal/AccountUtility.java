package net.oldschoolminecraft.bbal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.oldschoolminecraft.bbal.ex.BalanceVisibilityException;
import net.oldschoolminecraft.bbal.ex.InsufficientFundsException;
import net.oldschoolminecraft.bbal.ex.UnauthorizedTransactionException;
import net.oldschoolminecraft.bbal.ex.WithdrawLimitExceededException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class AccountUtility
{
    private static final File PARENT_DIR = new File("plugins/BusinessBal/accounts");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static
    {
        if (!PARENT_DIR.getParentFile().exists())
            PARENT_DIR.getParentFile().mkdirs();
        if (!PARENT_DIR.exists())
            PARENT_DIR.mkdirs();
    }

    public static File getAccountFile(String accountName)
    {
        return new File(PARENT_DIR, accountName.toLowerCase() + ".json");
    }

    public static BusinessAccount loadAccount(String accountName)
    {
        try (FileReader reader = new FileReader(getAccountFile(accountName)))
        {
            return gson.fromJson(reader, BusinessAccount.class);
        } catch (IOException e) {
            return null;
        }
    }

    public static void saveAccount(BusinessAccount account) throws IOException
    {
        File accountFile = getAccountFile(account.name);
        if (!accountFile.exists())
            accountFile.createNewFile();
        String json = gson.toJson(account);
        Files.write(accountFile.toPath(), json.getBytes());
    }

    public static class BusinessAccount
    {
        public String name;
        public String owner;
        public double balance;
        public double withdrawLimit;
        public boolean canTrusteesViewBalance;
        public ArrayList<String> trustees;

        public BusinessAccount(String name, String owner, double withdrawLimit, boolean canTrusteesViewBalance)
        {
            this.name = name;
            this.owner = owner;
            this.balance = 0.0;
            this.withdrawLimit = withdrawLimit;
            this.canTrusteesViewBalance = canTrusteesViewBalance;
            this.trustees = new ArrayList<>();
        }

        public void addTrustee(String playerName)
        {
            if (!trustees.contains(playerName))
                trustees.add(playerName);
        }

        public void removeTrustee(String playerName)
        {
            trustees.remove(playerName);
        }

        public double withdraw(String playerName, double amount) throws InsufficientFundsException, WithdrawLimitExceededException, UnauthorizedTransactionException
        {
            if (!trustees.contains(playerName) && !owner.equals(playerName))
                throw new UnauthorizedTransactionException();
            if (amount > withdrawLimit)
                throw new WithdrawLimitExceededException();
            if (amount > balance)
                throw new InsufficientFundsException();
            balance -= amount;
            return amount;
        }

        public double requestBalance(String playerName) throws UnauthorizedTransactionException, BalanceVisibilityException
        {
            if (!trustees.contains(playerName) && !owner.equals(playerName))
                throw new UnauthorizedTransactionException();
            if (!canTrusteesViewBalance && !owner.equals(playerName))
                throw new BalanceVisibilityException();
            return balance;
        }
    }
}
