package com.techlabs.SmartBankingSystem.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.techlabs.SmartBankingSystem.model.Transaction;

public class TransactionService {

    final String URL = "jdbc:mysql://localhost:3306/smart_bank_system";
    final String USER = "root";
    final String PASS = "admin#123";

    Scanner scanner = new Scanner(System.in);

    public boolean checkCustomerExists(int customer_id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            String checkQuery = "SELECT * FROM accounts WHERE customer_id = ? AND status = 'ACTIVE'";
            PreparedStatement ps = connection.prepareStatement(checkQuery);
            ps.setInt(1, customer_id);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void checkBalance(int customerId) {
        if (!checkCustomerExists(customerId)) {
            System.out.println("Customer not found or inactive.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {

            String checkQuery = "SELECT a.accountNumber, c.name, a.accountType, a.status, a.balance "
                    + "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id "
                    + "WHERE a.customer_id = ? AND a.status = 'ACTIVE'";
            PreparedStatement ps = connection.prepareStatement(checkQuery);

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println();
                System.out.println("---- Account Balance Details ----");
                System.out.println("Account Number   : " + rs.getString("accountNumber"));
                System.out.println("Account Holder   : " + rs.getString("name"));
                System.out.println("Account Type     : " + rs.getString("accountType"));
                System.out.println("Account Status   : " + rs.getString("status"));
                System.out.println("Balance          : ₹" + rs.getDouble("balance"));
                System.out.println("---------------------------------");
            } else {
                System.out.println("No account found for the given Customer ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void depositMoney(int depositCustomerId) {
        System.out.println("--- Deposit Money ---");
        
        System.out.print("Do you want to deposit money? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Deposit cancelled.");
            return;
        }
        
        double depositamount;

        while (true) {
            System.out.println("Please enter the amount to deposit: ");
            if (scanner.hasNextDouble()) {
                depositamount = scanner.nextDouble();
                scanner.nextLine();

                if (depositamount > 0) {
                    break;
                } else {
                    System.out.println("Amount must be greater than 0. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid amount.");
                scanner.nextLine();
            }
        }

        Transaction deposit = new Transaction(0, depositCustomerId, "deposit", depositamount);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {

            String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE customer_id = ? AND status = 'ACTIVE'";
            PreparedStatement pst = connection.prepareStatement(updateQuery);

            pst.setDouble(1, deposit.getAmount());
            pst.setInt(2, deposit.getCustomerId());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                String txnInsert = "INSERT INTO transactions (customer_id, type, amount) VALUES (?, 'deposit', ?)";
                PreparedStatement txnPs = connection.prepareStatement(txnInsert);
                txnPs.setInt(1, depositCustomerId);
                txnPs.setDouble(2, depositamount);
                txnPs.executeUpdate();

                System.out.println("Amount deposited successfully!");

            } else {
                System.out.println("No account updated. Check if the Customer ID exists.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double getCurrentBalance(int customerId) {
        double balance = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            String query = "SELECT balance FROM accounts WHERE customer_id = ? AND status = 'ACTIVE'";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public void withdrawMoney(int withdrawcustomerid) {

        System.out.println("--- Withdraw Money ---");
        
        System.out.print("Do you want to withdraw money? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Withdrawal cancelled.");
            return;
        }
        
        double withdrawamount;

        while (true) {
            System.out.println("Please enter the amount to withdraw: ");
            if (scanner.hasNextDouble()) {
                withdrawamount = scanner.nextDouble();
                scanner.nextLine();

                double currentBalance = getCurrentBalance(withdrawcustomerid);
                if (withdrawamount <= 0 || withdrawamount > currentBalance) {
                    System.out.println("Insufficient balance or invalid amount.");
                    return;
                }
                break;

            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        Transaction withdraw = new Transaction(0, withdrawcustomerid, "withdraw", withdrawamount);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {

            String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE customer_id = ? AND status = 'ACTIVE'";
            PreparedStatement pst = connection.prepareStatement(withdrawQuery);

            pst.setDouble(1, withdraw.getAmount());
            pst.setInt(2, withdraw.getCustomerId());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                String txnInsert = "INSERT INTO transactions (customer_id, type, amount) VALUES (?, 'withdraw', ?)";
                PreparedStatement txnPs = connection.prepareStatement(txnInsert);
                txnPs.setInt(1, withdrawcustomerid);
                txnPs.setDouble(2, withdrawamount);
                txnPs.executeUpdate();

                System.out.println("Amount withdrawn successfully!");

            } else {
                System.out.println("No account updated. Check if the Customer ID exists.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveTransaction(Connection connection, Transaction txn) throws SQLException {
        String insertQuery = "INSERT INTO transactions (customer_id, type, amount) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setInt(1, txn.getCustomerId());
            ps.setString(2, txn.getType());
            ps.setDouble(3, txn.getAmount());
            ps.executeUpdate();
        }
    }

    public void transferMoney(int fromId) {

        System.out.println("--- Transfer Money ---");
        
        System.out.print("Do you want to transfer money? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Transfer cancelled.");
            return;
        }
        
        int toId;
        double transferAmount;

        while (true) {
            System.out.println("Please enter the Customer ID of receiver: ");
            if (scanner.hasNextInt()) {
                toId = scanner.nextInt();
                scanner.nextLine();

                if (fromId == toId) {
                    System.out.println("Sender and Receiver cannot be the same. Self-transfer is not allowed.");
                    continue;
                }

                if (checkCustomerExists(toId)) {
                    break;
                } else {
                    System.out.println("Receiver Account not found. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        while (true) {
            System.out.println("Please enter the amount to transfer: ");
            if (scanner.hasNextDouble()) {
                transferAmount = scanner.nextDouble();
                scanner.nextLine();

                double senderBalance = getCurrentBalance(fromId);
                if (transferAmount <= 0 || transferAmount > senderBalance) {
                    System.out.println("Insufficient balance or invalid amount.");
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid input. Please enter a valid amount.");
                scanner.nextLine();
            }
        }
        
        System.out.print("Please enter your password to confirm the transfer: ");
        String inputPassword = scanner.nextLine();

        LoginService loginService = new LoginService();
        if (!loginService.validateCustomer(fromId, inputPassword)) {
            System.out.println("Password verification failed. Transfer cancelled. Please try again.");
            return; 
        }

        Transaction transferSent = new Transaction(0, fromId, "Transfer Sent", transferAmount);
        Transaction transferReceived = new Transaction(0, toId, "Transfer Received", transferAmount);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            connection.setAutoCommit(false);
            try {
                String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE customer_id = ?";
                PreparedStatement debitPs = connection.prepareStatement(debitQuery);
                debitPs.setDouble(1, transferAmount);
                debitPs.setInt(2, fromId);
                debitPs.executeUpdate();

                String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE customer_id = ?";
                PreparedStatement creditPs = connection.prepareStatement(creditQuery);
                creditPs.setDouble(1, transferAmount);
                creditPs.setInt(2, toId);
                creditPs.executeUpdate();

                saveTransaction(connection, transferSent);
                saveTransaction(connection, transferReceived);

                connection.commit();
                System.out.println("Transfer successful!");

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Transfer failed. Transaction rolled back.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transactionHistory(int customer_id) {
        System.out.println("--- Transaction History ---");
        boolean found = false;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            String query = "SELECT a.accountNumber, c.name as accountHolder, t.type, a.status, t.amount, t.date_time "
                    + "FROM transactions t "
                    + "JOIN accounts a ON t.customer_id = a.customer_id "
                    + "JOIN customers c ON a.customer_id = c.customer_id "
                    + "WHERE t.customer_id = ? ORDER BY t.date_time ASC";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, customer_id);

            ResultSet rs = ps.executeQuery();

            System.out.printf("%-18s %-20s %-18s %-15s %-12s %-20s%n", 
                "Account Number", "Account Holder", "Transaction Type", "Status", "Amount", "Date & Time");
            System.out.println("---------------------------------------------------------------------------------------------");

            while (rs.next()) {
                found = true;
                System.out.printf("%-18s %-20s %-18s %-15s ₹%-11.2f %-20s%n",
                    rs.getString("accountNumber"),
                    rs.getString("accountHolder"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("date_time").toString()
                );
            }


            if (!found) {
                System.out.println("No transaction history found for Customer ID: " + customer_id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeAccount(int customer_id) {
        System.out.println("--- Close Account ---");
        
        System.out.print("Do you want to close your account? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Account closure cancelled.");
            return;
        }
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            if (!checkCustomerExists(customer_id)) {
                System.out.println("No active Customer found for Customer ID: " + customer_id);
                return;
            }

            String updateAccountsQuery = "UPDATE accounts SET status = 'INACTIVE' WHERE customer_id = ? AND status = 'ACTIVE'";
            PreparedStatement psAccounts = connection.prepareStatement(updateAccountsQuery);
            psAccounts.setInt(1, customer_id);
            int accountsUpdated = psAccounts.executeUpdate();

            String updateCustomerQuery = "UPDATE customers SET status = 'INACTIVE' WHERE customer_id = ? AND status = 'ACTIVE'";
            PreparedStatement psCustomer = connection.prepareStatement(updateCustomerQuery);
            psCustomer.setInt(1, customer_id);
            int customersUpdated = psCustomer.executeUpdate();

            if (accountsUpdated > 0 && customersUpdated > 0) {
                System.out.println("Account and customer status updated to INACTIVE successfully.");
            } else if (accountsUpdated > 0) {
                System.out.println("Account closed successfully, but customer status was not updated.");
            } else {
                System.out.println("No active account found for the given Customer ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
