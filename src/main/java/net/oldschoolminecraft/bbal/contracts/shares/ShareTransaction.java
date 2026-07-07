package net.oldschoolminecraft.bbal.contracts.shares;

import java.util.UUID;

public final class ShareTransaction
{
    public enum Type
    {
        ISSUANCE, // business issued new shared
        TRANSFER, // player to player transfer of shares
        BUYBACK, // company removes shares
        SPLIT, // stock split
        MERGE // reverse split / merge
    }

    private final UUID id;
    private final long timestamp;

    private final Type type;

    // Null for ISSUANCE
    private final String from;

    // Null for BUYBACK
    private final String to;

    private final int shares;

    // Optional metadata
    private final Double pricePerShare;
    private final String reason;

    public ShareTransaction(
            Type type,
            String from,
            String to,
            int shares,
            Double pricePerShare,
            String reason)
    {

        this.id = UUID.randomUUID();
        this.timestamp = System.currentTimeMillis();

        this.type = type;
        this.from = from;
        this.to = to;
        this.shares = shares;
        this.pricePerShare = pricePerShare;
        this.reason = reason;
    }

    public UUID getId()
    {
        return id;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public Type getType()
    {
        return type;
    }

    public String getFrom()
    {
        return from;
    }

    public String getTo()
    {
        return to;
    }

    public int getShares()
    {
        return shares;
    }

    public Double getPricePerShare()
    {
        return pricePerShare;
    }

    public String getReason()
    {
        return reason;
    }
}