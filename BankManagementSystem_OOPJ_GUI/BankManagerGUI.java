import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BankManagerGUI extends JFrame {
    private BankManager bankManager;

    private final Color MAIN_BG = new Color(232, 244, 255);
    private final Color PANEL_BG = new Color(245, 251, 255);
    private final Color CONTROL_BG = new Color(255, 255, 255);
    private final Color BUTTON_BG = new Color(20, 96, 170);
    private final Color BUTTON_FG = Color.WHITE;
    private final Color OUTPUT_BG = new Color(248, 252, 255);
    private final Color HEADER_FG = new Color(20, 50, 100);

    private JTextField createNameField;
    private JTextField createDepositField;
    private JTextField depositAccountField;
    private JTextField depositAmountField;
    private JTextField withdrawAccountField;
    private JTextField withdrawAmountField;
    private JTextField checkAccountField;
    private JTextArea outputArea;
    private JTable accountsTable;

    // Database summary labels (sidebar gist)
    private JLabel totalAccountsLabel;
    private JLabel totalBalanceLabel;
    private JLabel averageBalanceLabel;
    private JLabel dsStatusLabel;
    private JCheckBox useDatabaseCheckBox;

    // Assistant query widgets
    private JTextField queryInputField;
    private JTextArea queryResponseArea;
    private JTextArea queryArea;

    public BankManagerGUI() {
        bankManager = new BankManager();

        setTitle("Bank Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 670);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(MAIN_BG);

        outputArea = new JTextArea(8, 80);
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        outputArea.setBackground(OUTPUT_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(MAIN_BG);
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        topPanel.setBackground(MAIN_BG);

        JPanel summaryPanel = createDatabaseSummaryPanel();

        add(summaryPanel, BorderLayout.EAST);

        topPanel.add(createAccountPanel());
        topPanel.add(depositPanel());
        topPanel.add(withdrawPanel());
        topPanel.add(checkBalancePanel());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(createAccountListPanel(), BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        updateAccountTable();
        updateDatabaseSummary();

        setVisible(true);
    }

    private JPanel createDatabaseSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Database Summary"));
        panel.setPreferredSize(new Dimension(260, 0));

        JPanel top = new JPanel(new GridLayout(0, 1, 4, 4));
        top.setBackground(PANEL_BG);

        totalAccountsLabel = new JLabel("Total Accounts: 0");
        totalBalanceLabel = new JLabel("Total Balance: Rs.0.00");
        averageBalanceLabel = new JLabel("Average Balance: Rs.0.00");
        dsStatusLabel = new JLabel("Status: ");

        useDatabaseCheckBox = new JCheckBox("Use Database (Oracle)", bankManager.isUseDatabase());
        useDatabaseCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bankManager.setUseDatabase(useDatabaseCheckBox.isSelected());
                appendOutput("Switched to " + (useDatabaseCheckBox.isSelected() ? "database mode" : "offline mode") + ".");
                updateDatabaseSummary();
            }
        });

        top.add(useDatabaseCheckBox);
        top.add(dsStatusLabel);
        top.add(new JLabel("Source: localhost:1521/xe"));
        top.add(new JLabel("Schema: u24051061"));
        top.add(totalAccountsLabel);
        top.add(totalBalanceLabel);
        top.add(averageBalanceLabel);

        JButton refreshSummaryButton = new JButton("Refresh Summary");
        styleButton(refreshSummaryButton);
        refreshSummaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDatabaseSummary();
            }
        });

        top.add(refreshSummaryButton);

        queryArea = new JTextArea();
        queryArea.setBackground(OUTPUT_BG);
        queryArea.setEditable(false);
        queryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        queryArea.setText("-- Personal Assistant (help prompts):\n"
            + "Type your question in the field below, then press Ask Assistant.\n"
            + "Examples:\n"
            + " - create account\n"
            + " - deposit money\n"
            + " - withdraw money\n"
            + " - check balance\n"
            + " - switch to offline mode\n"
            + "- use database on/off\n");

        queryInputField = new JTextField();

        JButton askButton = new JButton("Ask Assistant");
        styleButton(askButton);
        askButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userQuery = queryInputField.getText().trim();
                if (!userQuery.isEmpty()) {
                    queryResponseArea.setText(processAssistantQuery(userQuery));
                }
            }
        });

        JPanel queryEntryPanel = new JPanel(new BorderLayout(5, 5));
        queryEntryPanel.add(queryInputField, BorderLayout.CENTER);
        queryEntryPanel.add(askButton, BorderLayout.EAST);

        queryResponseArea = new JTextArea(5, 20);
        queryResponseArea.setEditable(false);
        queryResponseArea.setBackground(OUTPUT_BG);
        queryResponseArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

        JPanel queryPanel = new JPanel(new BorderLayout(6, 6));
        queryPanel.setBorder(BorderFactory.createTitledBorder("Personal Assistant"));
        queryPanel.add(new JScrollPane(queryArea), BorderLayout.NORTH);
        queryPanel.add(queryEntryPanel, BorderLayout.CENTER);
        queryPanel.add(new JScrollPane(queryResponseArea), BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(queryPanel, BorderLayout.CENTER);

        return panel;
    }

    private void updateDatabaseSummary() {
        ArrayList<BankAccount> accounts = bankManager.getAllAccounts();
        int totalAccounts = accounts.size();
        double totalBalance = 0;

        for (BankAccount acc : accounts) {
            totalBalance += acc.getBalance();
        }

        double averageBalance = totalAccounts > 0 ? totalBalance / totalAccounts : 0;

        totalAccountsLabel.setText("Total Accounts: " + totalAccounts);
        totalBalanceLabel.setText(String.format("Total Balance: Rs.%.2f", totalBalance));
        averageBalanceLabel.setText(String.format("Average Balance: Rs.%.2f", averageBalance));
        dsStatusLabel.setText("Status: " + (bankManager.isUseDatabase() ? "Database" : "Offline"));
        useDatabaseCheckBox.setSelected(bankManager.isUseDatabase());
    }

    private String processAssistantQuery(String query) {
        String q = query.toLowerCase();

        if (q.contains("create") && q.contains("account")) {
            return "To create an account: fill name and initial deposit, click Create Account. Account number appears in output.";
        }
        if (q.contains("deposit")) {
            return "To deposit: enter account number and deposit amount, then click Deposit. Ensure amount > 0.";
        }
        if (q.contains("withdraw")) {
            return "To withdraw: enter account number and withdrawal amount, then click Withdraw. Ensure sufficient balance.";
        }
        if (q.contains("balance")) {
            return "To check balance: enter account number in Check Balance field and click Check Balance. It works in both modes, using local buffer first.";
        }
        if (q.contains("offline") || q.contains("database")) {
            return "Use the checkbox in Database Summary to toggle between offline mode and Oracle database mode. Click Refresh Summary after switching.";
        }
        if (q.contains("connection")) {
            return "If DB connection fails, app runs offline mode (local cache). Keep working and reconnect later.";
        }

        return "I'm here to help. Use keywords like create, deposit, withdraw, balance, offline, database.";
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 3, 3));
        panel.setBorder(BorderFactory.createTitledBorder("Create New Account"));
        panel.setBackground(PANEL_BG);

        createNameField = new JTextField();
        createDepositField = new JTextField();
        createNameField.setBackground(CONTROL_BG);
        createDepositField.setBackground(CONTROL_BG);

        panel.add(new JLabel("Account Holder Name:"));
        panel.add(createNameField);
        panel.add(new JLabel("Initial Deposit:"));
        panel.add(createDepositField);

        JButton createButton = new JButton("Create Account");
        styleButton(createButton);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });

        panel.add(createButton);
        return panel;
    }

    private JPanel depositPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 3, 3));
        panel.setBorder(BorderFactory.createTitledBorder("Deposit Money"));
        panel.setBackground(PANEL_BG);

        depositAccountField = new JTextField();
        depositAmountField = new JTextField();
        depositAccountField.setBackground(CONTROL_BG);
        depositAmountField.setBackground(CONTROL_BG);

        panel.add(new JLabel("Account Number:"));
        panel.add(depositAccountField);
        panel.add(new JLabel("Amount to Deposit:"));
        panel.add(depositAmountField);

        JButton depositButton = new JButton("Deposit");
        styleButton(depositButton);
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performDeposit();
            }
        });

        panel.add(depositButton);
        return panel;
    }

    private JPanel withdrawPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 3, 3));
        panel.setBorder(BorderFactory.createTitledBorder("Withdraw Money"));
        panel.setBackground(PANEL_BG);

        withdrawAccountField = new JTextField();
        withdrawAmountField = new JTextField();
        withdrawAccountField.setBackground(CONTROL_BG);
        withdrawAmountField.setBackground(CONTROL_BG);

        panel.add(new JLabel("Account Number:"));
        panel.add(withdrawAccountField);
        panel.add(new JLabel("Amount to Withdraw:"));
        panel.add(withdrawAmountField);

        JButton withdrawButton = new JButton("Withdraw");
        styleButton(withdrawButton);
        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performWithdrawal();
            }
        });

        panel.add(withdrawButton);
        return panel;
    }

    private JPanel checkBalancePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 3, 3));
        panel.setBorder(BorderFactory.createTitledBorder("Check Balance"));
        panel.setBackground(PANEL_BG);

        checkAccountField = new JTextField();
        checkAccountField.setBackground(CONTROL_BG);

        panel.add(new JLabel("Account Number:"));
        panel.add(checkAccountField);

        JButton checkButton = new JButton("Check Balance");
        styleButton(checkButton);
        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkBalance();
            }
        });

        panel.add(checkButton);
        return panel;
    }

    private JPanel createAccountListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("All Accounts"));
        panel.setBackground(PANEL_BG);

        accountsTable = new JTable();
        accountsTable.setBackground(Color.WHITE);
        accountsTable.setSelectionBackground(new Color(215, 232, 255));
        accountsTable.setGridColor(new Color(200, 210, 230));
        JScrollPane tableScroll = new JScrollPane(accountsTable);

        JButton refreshButton = new JButton("Refresh List");
        styleButton(refreshButton);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAccountTable();
            }
        });

        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private void createAccount() {
        String name = createNameField.getText().trim();
        String depositStr = createDepositField.getText().trim();

        if (name.isEmpty()) {
            appendOutput("Error: Name cannot be empty.");
            return;
        }

        try {
            double initialDeposit = Double.parseDouble(depositStr);
            if (initialDeposit < 0) {
                appendOutput("Error: Initial deposit cannot be negative.");
                return;
            }

            int newAccountNumber = bankManager.createAccount(name, initialDeposit);
            if (newAccountNumber > 0) {
                appendOutput("Account created for " + name + " with initial deposit Rs." + initialDeposit
                    + " (Account Number: " + newAccountNumber + ")");
            } else {
                appendOutput("Failed to create account, please try again.");
            }
            createNameField.setText("");
            createDepositField.setText("");
            updateAccountTable();

        } catch (NumberFormatException ex) {
            appendOutput("Error: Invalid deposit amount. Enter a number.");
        }
    }

    private void performDeposit() {
        try {
            int accountNumber = Integer.parseInt(depositAccountField.getText().trim());
            double amount = Double.parseDouble(depositAmountField.getText().trim());

            bankManager.performDeposit(accountNumber, amount);
            appendOutput("Deposit Rs." + amount + " to account " + accountNumber + " completed.");
            depositAccountField.setText("");
            depositAmountField.setText("");
            updateAccountTable();

        } catch (NumberFormatException ex) {
            appendOutput("Error: Account number and amount must be numeric.");
        }
    }

    private void performWithdrawal() {
        try {
            int accountNumber = Integer.parseInt(withdrawAccountField.getText().trim());
            double amount = Double.parseDouble(withdrawAmountField.getText().trim());

            bankManager.performWithdrawal(accountNumber, amount);
            appendOutput("Withdraw Rs." + amount + " from account " + accountNumber + " processed.");
            withdrawAccountField.setText("");
            withdrawAmountField.setText("");
            updateAccountTable();

        } catch (NumberFormatException ex) {
            appendOutput("Error: Account number and amount must be numeric.");
        }
    }

    private void checkBalance() {
        try {
            int accountNumber = Integer.parseInt(checkAccountField.getText().trim());
            BankAccount account = getAccountByNumber(accountNumber);

            if (account == null) {
                appendOutput("Error: Account " + accountNumber + " not found.");
            } else {
                appendOutput("Account " + accountNumber + " holder " + account.getAccountHolderName() + " balance Rs." + account.getBalance());
            }
        } catch (NumberFormatException ex) {
            appendOutput("Error: Account number must be numeric.");
        }
        checkAccountField.setText("");
    }

    private BankAccount getAccountByNumber(int accountNumber) {
        return bankManager.getAccountByNumber(accountNumber);
    }

    private void updateAccountTable() {
        ArrayList<BankAccount> accounts = bankManager.getAllAccounts();
        String[] headers = {"Account Number", "Holder Name", "Balance"};
        Object[][] data = new Object[accounts.size()][3];

        for (int i = 0; i < accounts.size(); i++) {
            BankAccount acc = accounts.get(i);
            data[i][0] = acc.getAccountNumber();
            data[i][1] = acc.getAccountHolderName();
            data[i][2] = acc.getBalance();
        }

        accountsTable.setModel(new javax.swing.table.DefaultTableModel(data, headers));
        updateDatabaseSummary();
    }

    private void appendOutput(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void styleButton(JButton button) {
        button.setBackground(BUTTON_BG);
        button.setForeground(BUTTON_FG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BUTTON_BG.darker()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BankManagerGUI();
            }
        });
    }
}
