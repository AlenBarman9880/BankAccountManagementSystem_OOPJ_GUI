import java.util.ArrayList;

public class BankManager {
    private DataBaseHelper dbHelper;
    private ArrayList<BankAccount> localAccounts;
    private int nextAccountNumber = 1001;
    private boolean useDatabase = true;

    public BankManager() {
        localAccounts = new ArrayList<>();

        try {
            dbHelper = new DataBaseHelper();
            ArrayList<BankAccount> dbAccounts = dbHelper.fetchAllAccounts();
            if (dbAccounts != null) {
                localAccounts.addAll(dbAccounts);
                for (BankAccount acc : dbAccounts) {
                    if (acc.getAccountNumber() >= nextAccountNumber) {
                        nextAccountNumber = acc.getAccountNumber() + 1;
                    }
                }
            }
            useDatabase = true;
        } catch (Exception e) {
            System.out.println("Warning: Database unavailable, switching to offline mode. " + e.getMessage());
            useDatabase = false;
        }
    }

    // Create a new account and immediately insert to DB if enabled (else in-memory only).
    // Returns the generated account number or -1 if invalid input.
    public int createAccount(String name, double initialDeposit) {
        if (initialDeposit < 0) {
            System.out.println("Error: Initial deposit cannot be negative.");
            return -1;
        }

        int assignedAccountNumber = nextAccountNumber;
        BankAccount newAccount = new BankAccount(assignedAccountNumber, name, initialDeposit);
        localAccounts.add(newAccount);

        if (useDatabase && dbHelper != null) {
            try {
                dbHelper.insertAccount(newAccount);
                System.out.println("\nAccount created successfully and saved to database!");
            } catch (Exception e) {
                System.out.println("Warning: Could not save to DB, offline mode is active. " + e.getMessage());
                useDatabase = false;
            }
        } else {
            System.out.println("\nAccount created in offline mode.");
        }

        System.out.println("Your new Account Number is: " + assignedAccountNumber);
        nextAccountNumber++;
        return assignedAccountNumber;
    }

    private BankAccount findAccountLocally(int accountNumber) {
        for (BankAccount acc : localAccounts) {
            if (acc.getAccountNumber() == accountNumber) {
                return acc;
            }
        }
        return null;
    }

    // Public helper for GUI workflows
    public BankAccount getAccountByNumber(int accountNumber) {
        return findAccountLocally(accountNumber);
    }

    public void performDeposit(int accountNumber, double amount) {
        BankAccount acc = findAccountLocally(accountNumber);
        if (acc != null) {
            acc.deposit(amount);
            if (useDatabase && dbHelper != null) {
                dbHelper.updateBalance(accountNumber, acc.getBalance());
            }
        } else {
            System.out.println("Error: Account not found.");
        }
    }

    public void performWithdrawal(int accountNumber, double amount) {
        BankAccount acc = findAccountLocally(accountNumber);
        if (acc != null) {
            if (amount > 0 && amount <= acc.getBalance()) {
                acc.withdraw(amount);
                if (useDatabase && dbHelper != null) {
                    dbHelper.updateBalance(accountNumber, acc.getBalance());
                }
            } else {
                acc.withdraw(amount);
            }
        } else {
            System.out.println("Error: Account not found.");
        }
    }

    public void checkBalance(int accountNumber) {
        BankAccount acc = findAccountLocally(accountNumber);
        if (acc != null) {
            System.out.println("\n--- Account Details ---");
            acc.displayAccountInfo();
        } else {
            System.out.println("Error: Account not found.");
        }
    }

    public ArrayList<BankAccount> getAllAccounts() {
        return new ArrayList<>(localAccounts);
    }

    public void displayAllAccounts() {
        ArrayList<BankAccount> accounts = getAllAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        String source = useDatabase ? "Oracle DB" : "Offline cache";
        System.out.println("\n--- All Bank Accounts (" + source + ") ---");
        for (BankAccount acc : accounts) {
            acc.displayAccountInfo();
        }
    }

    public void setUseDatabase(boolean useDatabase) {
        this.useDatabase = useDatabase;
        if (useDatabase && dbHelper == null) {
            dbHelper = new DataBaseHelper();
        }
    }

    public boolean isUseDatabase() {
        return useDatabase;
    }
}
