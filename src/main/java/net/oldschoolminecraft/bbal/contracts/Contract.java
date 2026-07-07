package net.oldschoolminecraft.bbal.contracts;

import java.util.UUID;

public abstract class Contract
{

    private final UUID id = UUID.randomUUID();

    protected ContractState state = ContractState.DRAFT;

    protected final String creator;
    protected String counterparty;

    protected long createdAt = System.currentTimeMillis();

    protected Long expiryTime;

    public Contract(String creator)
    {
        this.creator = creator;
    }

    public UUID getId()
    {
        return id;
    }

    public ContractState getState()
    {
        return state;
    }

    public boolean isExpired()
    {
        return expiryTime != null && System.currentTimeMillis() > expiryTime;
    }

    public void tick()
    {

        if (state != ContractState.ACTIVE)
            return;

        if (isExpired())
        {
            expire();
        }
    }

    public void activate()
    {
        if (state != ContractState.DRAFT)
            return;

        state = ContractState.ACTIVE;
    }

    protected void fulfill()
    {
        state = ContractState.FULFILLED;
    }

    protected void cancel()
    {
        state = ContractState.CANCELLED;
    }

    protected void expire()
    {
        state = ContractState.EXPIRED;
    }

    public abstract boolean canFulfill();

    public abstract void execute();

}