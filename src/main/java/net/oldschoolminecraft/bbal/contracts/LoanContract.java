package net.oldschoolminecraft.bbal.contracts;

public class LoanContract extends Contract
{
    private final String lender;
    private final String borrower;

    private final double principal;
    private final double repayment;

    private final long repaymentDeadline;

    private boolean funded;
    private boolean repaid;

    public LoanContract(
            String lender,
            String borrower,
            double principal,
            double repayment,
            long repaymentDeadline)
    {
        super(lender);

        this.lender = lender;
        this.borrower = borrower;
        this.principal = principal;
        this.repayment = repayment;
        this.repaymentDeadline = repaymentDeadline;
    }

    public void fundLoan()
    {
        // withdraw lender

        // pay borrower

        funded = true;

        activate();
    }

    public void repay()
    {
        // withdraw borrower

        // pay lender

        repaid = true;

        fulfill();
    }

    @Override
    public boolean canFulfill()
    {
        return funded && repaid;
    }

    @Override
    public void execute()
    {

    }

    @Override
    protected void expire()
    {
        if (!repaid)
        {
            state = ContractState.DEFAULTED;
            return;
        }

        super.expire();
    }
}