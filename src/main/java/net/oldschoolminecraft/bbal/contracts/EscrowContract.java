package net.oldschoolminecraft.bbal.contracts;

import java.util.ArrayList;
import java.util.List;

public class EscrowContract extends Contract
{
    private final List<Deposit> deposits = new ArrayList<>();

    private Runnable settlement;

    public EscrowContract(String creator)
    {
        super(creator);
    }

    public void addDeposit(Deposit deposit)
    {
        deposits.add(deposit);
    }

    public void setSettlement(Runnable settlement)
    {
        this.settlement = settlement;
    }

    @Override
    public boolean canFulfill()
    {
        for (Deposit d : deposits)
        {
            if (!d.isSatisfied())
                return false;
        }

        return true;
    }

    @Override
    public void execute()
    {
        if (!canFulfill())
            return;
        settlement.run();
        fulfill();
    }
}
