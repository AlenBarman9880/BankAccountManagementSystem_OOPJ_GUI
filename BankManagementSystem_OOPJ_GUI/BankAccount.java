public class BankAccount {
    // Encapsulated data fields
    private int accountNumber;
    private String accountHolderName;
    private double balance;

    // Constructor to initialize a new account
    public BankAccount(int accountNumber, String accountHolderName, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
    }

    // Getters
    public int getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    // Method to deposit money
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Success! Deposited Rs." + amount + " to account " + accountNumber);
        } else {
            System.out.println("Error: Deposit amount must be greater than zero.");
        }
    }

    // Method to withdraw money
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Error: Withdrawal amount must be greater than zero.");
        } else if (amount > balance) {
            System.out.println("Error: Insufficient funds! Current balance is Rs." + balance);
        } else {
            balance -= amount;
            System.out.println("Success! Withdrew Rs." + amount + " from account " + accountNumber);
        }
    }

    // Display account information
    public void displayAccountInfo() {
        System.out.println("Account Number: " + accountNumber + " | Name: " + accountHolderName + " | Balance: Rs." + balance);
    }
}