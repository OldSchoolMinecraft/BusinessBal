package net.oldschoolminecraft.bbal.contracts;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContractManager
{
    private final Map<UUID, Contract> contracts = new HashMap<>();

    public void add(Contract contract)
    {
        contracts.put(contract.getId(), contract);
    }

    public Contract get(UUID id)
    {
        return contracts.get(id);
    }

    public void tick()
    {
        for (Contract contract : contracts.values())
        {
            contract.tick();
        }
    }
}
