# 🏦 Bank Account Management System

A Java-based desktop banking application built with **Java Swing** for the GUI and **Oracle XE** as the backend database. Developed as a semester project for college coursework.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Database Setup](#database-setup)
- [How to Run](#how-to-run)
- [Module Descriptions](#module-descriptions)
- [Screenshots](#screenshots)
- [Group Info](#group-info)

---

## Overview

This system simulates core banking operations through a user-friendly graphical interface. It supports account creation, deposits, withdrawals, balance checks, and real-time database integration. If the database is unavailable, the system automatically falls back to an in-memory offline mode — ensuring no data is lost.

---

## Features

- ✅ Create bank accounts with auto-generated account numbers
- ✅ Deposit and withdraw money with validation
- ✅ Check account balance in real time
- ✅ View all accounts in a sortable table
- ✅ Database summary (total accounts, total balance, average)
- ✅ Output terminal for system messages and logs
- ✅ Chat assistant for guided banking operations
- ✅ Offline fallback mode when Oracle DB is unavailable

---

## Project Structure

```
Java_Semester_Project/
│
├── src/
│   ├── Main.java                  # Entry point – launches the GUI
│   ├── BankAccount.java           # Data model for a bank account
│   ├── BankManager.java           # Core business logic
│   ├── BankManagerGUI.java        # Main GUI window (JFrame)
│   ├── DataBaseHelper.java        # Oracle DB operations via JDBC
│   ├── TestConnection.java        # Standalone DB connectivity tester
│   └── [GUI Panel Classes]/       # 8 individual panel classes
│
└── Java_Semester_Project.iml      # IntelliJ IDEA project config
```

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java (JDK 8+) | Core programming language |
| Java Swing | GUI framework |
| Oracle XE | Relational database |
| JDBC (ojdbc) | Java–Database connectivity |
| IntelliJ IDEA | IDE |

---

## Database Setup

1. Install **Oracle XE** on your machine
2. Create a user with the appropriate credentials
3. Update the connection details in `DataBaseHelper.java`:

```java
private static final String URL  = "jdbc:oracle:thin:@localhost:1521:xe";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

4. The `accounts` table is created automatically on first run:

```sql
CREATE TABLE accounts (
    accountNumber     NUMBER PRIMARY KEY,
    accountHolderName VARCHAR2(100),
    balance           NUMBER
);
```

5. Make sure the **Oracle JDBC driver** (`ojdbc.jar`) is added to your project classpath.

> **Note:** You can verify your connection before running the main app using `TestConnection.java`.

---

## How to Run

1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/bank-management-system.git
   ```

2. Open the project in **IntelliJ IDEA**

3. Add `ojdbc.jar` to the project dependencies:
   - `File → Project Structure → Libraries → + → ojdbc.jar`

4. Configure your Oracle DB credentials in `DataBaseHelper.java`

5. Run `Main.java`

> If Oracle DB is not available, the system will start in **offline mode** automatically.

---

## Module Descriptions

### `BankAccount.java`
The data model. Stores account number, holder name, and balance. Provides `deposit()` and `withdraw()` methods with input validation.

### `BankManager.java`
The brain of the system. Handles account creation, transaction logic, DB sync, and offline fallback. Acts as the bridge between the GUI and the database.

### `BankManagerGUI.java`
The main application window. Hosts all 8 panels using Java Swing layout managers. Routes user actions to `BankManager`.

### `DataBaseHelper.java`
The data access object (DAO). Handles all Oracle DB operations — table creation, insert, update, and fetch — using JDBC `PreparedStatement` to prevent SQL injection.

### `Main.java`
The entry point. Launches `BankManagerGUI` on the Swing Event Dispatch Thread (EDT) for thread safety.

### `TestConnection.java`
A standalone utility to verify Oracle DB connectivity. Run this independently to diagnose connection issues before launching the main app.

### GUI Panel Classes
Eight dedicated Swing panels, one per feature:
- `CreateAccountPanel` — account creation form
- `DepositPanel` — deposit interface
- `WithdrawPanel` — withdrawal interface
- `BalancePanel` — balance inquiry
- `AllAccountsPanel` — JTable of all accounts
- `DatabaseSummaryPanel` — aggregate statistics
- `OutputTerminalPanel` — system log display
- `ChatAssistantPanel` — conversational help

---


## Group Info

| Field | Details |
|---|---|
| Project Title | Bank Account Management System |
| Group Number | 6 |
| Submission Type | Individual |
| Course | _(your course name)_ |
| Instructor | _(your instructor name)_ |

---

> _Built with Java Swing + Oracle XE as part of a college semester project._
