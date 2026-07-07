package net.oldschoolminecraft.bbal.contracts.shares;

import java.util.*;

public class ShareLedger
{
    private final List<ShareTransaction> transactions = new ArrayList<>();

    public Map<String, Integer> calculateOwnership()
    {
        Map<String, Integer> ownership = new HashMap<>();

        for (ShareTransaction tx : transactions)
        {
            if (tx.getFrom() != null)
                ownership.merge(tx.getFrom(), -tx.getShares(), Integer::sum);

            if (tx.getTo() != null)
                ownership.merge(tx.getTo(), tx.getShares(), Integer::sum);
        }

        ownership.values().removeIf(v -> v <= 0);

        return ownership;
    }

    public int getOutstandingShares()
    {
        int total = 0;

        for (ShareTransaction tx : transactions)
        {
            switch (tx.getType())
            {
                case ISSUANCE:
                    total += tx.getShares();
                    break;
                case BUYBACK:
                    total -= tx.getShares();
                    break;
            }
        }

        return total;
    }

    public List<ShareTransaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public int getShares(String player) {
        return calculateOwnership().getOrDefault(player.toLowerCase(), 0);
    }

    public double getOwnershipPercent(String player) {
        int outstanding = getOutstandingShares();
        if (outstanding == 0)
            return 0.0;

        return getShares(player) / (double) outstanding;
    }

    public void issueShares(String recipient, int shares, String reason) {
        if (shares <= 0)
            throw new IllegalArgumentException("Shares must be positive.");

        transactions.add(new ShareTransaction(
                ShareTransaction.Type.ISSUANCE,
                null,
                recipient.toLowerCase(),
                shares,
                null,
                reason
        ));
    }

    public void transferShares(
            String from,
            String to,
            int shares,
            Double pricePerShare,
            String reason) {

        from = from.toLowerCase();
        to = to.toLowerCase();

        if (shares <= 0)
            throw new IllegalArgumentException("Shares must be positive.");

        if (getShares(from) < shares)
            throw new IllegalArgumentException(
                    from + " only owns " + getShares(from) + " shares.");

        transactions.add(new ShareTransaction(
                ShareTransaction.Type.TRANSFER,
                from,
                to,
                shares,
                pricePerShare,
                reason
        ));
    }

    public void buyBackShares(String shareholder, int shares, String reason) {

        shareholder = shareholder.toLowerCase();

        if (shares <= 0)
            throw new IllegalArgumentException("Shares must be positive.");

        if (getShares(shareholder) < shares)
            throw new IllegalArgumentException(
                    shareholder + " only owns " + getShares(shareholder) + " shares.");

        transactions.add(new ShareTransaction(
                ShareTransaction.Type.BUYBACK,
                shareholder,
                null,
                shares,
                null,
                reason
        ));
    }

    public static ShareLedger create(String founder, int initialShares)
    {
        ShareLedger ledger = new ShareLedger();
        ledger.issueShares(founder, initialShares, "Initial company capitalization");
        return ledger;
    }
}
