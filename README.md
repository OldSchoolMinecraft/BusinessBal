# BusinessBal

This plugin integrates with Essentials economy to allow a shared business account system. Admins are responsible for managing the creation and deletion of business accounts, and account owners are able to add trustees who can deposit and withdraw money from the account.
### Admin Commands

---
* `/bbaladmin reload` - Reloads the plugin configuration.
* `/bbaladmin create` - Creates a new account
* `/bbaladmin delete` - Deletes an account

### Player Commands

---
* `/bbal view <account_name>` - Views the balance of an account
* `/bbal deposit <account_name> <amount>` - Deposits money into an account
* `/bbal withdraw <account_name> <amount>` - Withdraws money from an account
* `/bbal addtrustee <account_name> <player_name>` - Adds a trustee to an account
* `/bbal removetrustee <account_name> <player_name>` - Removes a trustee from an account
* `/bbal setwithdrawlimit <account_name> <amount>` - Sets the withdrawal limit for an account
* `/bbal allowviewingbalance <account_name> <true|false>` - Toggle balance visibility for trustees