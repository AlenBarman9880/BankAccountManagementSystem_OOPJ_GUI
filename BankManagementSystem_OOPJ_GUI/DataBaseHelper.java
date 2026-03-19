import java.sql.*;
import java.util.ArrayList;

public class DataBaseHelper {
    // 1. Updated for Oracle!
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; 
    private static final String USER = "u24051061"; 
    private static final String PASSWORD = "u24051061"; 

    public DataBaseHelper() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            String createTableSQL = "CREATE TABLE accounts (" +
                                    "accountNumber NUMBER PRIMARY KEY, " +
                                    "accountHolderName VARCHAR2(100), " +
                                    "balance NUMBER)";
            try {
                stmt.execute(createTableSQL);
            } catch (SQLException e) {
                // Error code 955 means "name is already used by an existing object" (the table already exists)
                if (e.getErrorCode() != 955) {
                    System.out.println("Error creating table: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    public void insertAccount(BankAccount acc) {
        String insertSQL = "INSERT INTO accounts (accountNumber, accountHolderName, balance) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            pstmt.setInt(1, acc.getAccountNumber());
            pstmt.setString(2, acc.getAccountHolderName());
            pstmt.setDouble(3, acc.getBalance());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error inserting account: " + e.getMessage());
        }
    }

    public void updateBalance(int accountNumber, double newBalance) {
        String updateSQL = "UPDATE accounts SET balance = ? WHERE accountNumber = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, accountNumber);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public ArrayList<BankAccount> fetchAllAccounts() {
        ArrayList<BankAccount> loadedAccounts = new ArrayList<>();
        String querySQL = "SELECT * FROM accounts";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {
            
            while (rs.next()) {
                int accNum = rs.getInt("accountNumber");
                String name = rs.getString("accountHolderName");
                double balance = rs.getDouble("balance");
                
                loadedAccounts.add(new BankAccount(accNum, name, balance));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching accounts: " + e.getMessage());
        }
        return loadedAccounts;
    }
}