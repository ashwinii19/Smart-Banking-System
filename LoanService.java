package com.techlabs.SmartBankingSystem.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoanService {

    final String URL = "jdbc:mysql://localhost:3306/smart_bank_system";
    final String USER = "root";
    final String PASS = "admin#123";

    Scanner scanner = new Scanner(System.in);

    public void applyLoan(int customerId) {
        while (true) {
            System.out.println("\n--- Loan Section ---");
            System.out.println("1. Apply for Loan");
            System.out.println("2. Repay Loan");
            System.out.println("3. Check Loan Balance");
            System.out.print("Choose option (1, 2 or 3): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {

                switch (choice) {
                    case 1: {
                        double loanAmount;
                        while (true) {
                            System.out.print("Enter loan amount: ");
                            if (scanner.hasNextDouble()) {
                                loanAmount = scanner.nextDouble();
                                scanner.nextLine();
                                if (loanAmount <= 0) {
                                    System.out.println("Amount must be greater than 0.");
                                } else {
                                    break;
                                }
                            } else {
                                System.out.println("Invalid input. Try again.");
                                scanner.nextLine();
                            }
                        }

                        String insertQuery = "INSERT INTO loans (customer_id, amount, start_date) VALUES (?, ?, ?)";
                        PreparedStatement ps = connection.prepareStatement(insertQuery);
                        ps.setInt(1, customerId);
                        ps.setDouble(2, loanAmount);
                        ps.setDate(3, Date.valueOf(LocalDate.now()));

                        int rows = ps.executeUpdate();
                        System.out.println(rows > 0 ? "Loan applied successfully!" : "Loan application failed.");
                        break;
                    }

                    case 2: {
                        String fetchLoansQuery = "SELECT loan_id, amount FROM loans WHERE customer_id = ? AND amount > 0 ORDER BY start_date ASC";
                        PreparedStatement fetchStmt = connection.prepareStatement(fetchLoansQuery);
                        fetchStmt.setInt(1, customerId);
                        ResultSet rs = fetchStmt.executeQuery();

                        double totalLoan = 0;
                        List<Integer> loanIds = new ArrayList<>();
                        List<Double> loanAmounts = new ArrayList<>();

                        while (rs.next()) {
                            loanIds.add(rs.getInt("loan_id"));
                            loanAmounts.add(rs.getDouble("amount"));
                            totalLoan += rs.getDouble("amount");
                        }

                        if (loanIds.isEmpty()) {
                            System.out.println("You have no outstanding loans.");
                            break;
                        }

                        System.out.println("Total loan balance: ₹" + totalLoan);

                        double repayAmount;
                        while (true) {
                            System.out.print("Enter repayment amount: ");
                            if (scanner.hasNextDouble()) {
                                repayAmount = scanner.nextDouble();
                                scanner.nextLine();
                                if (repayAmount <= 0) {
                                    System.out.println("Amount must be greater than 0.");
                                } else if (repayAmount > totalLoan) {
                                    System.out.println("You only owe ₹" + totalLoan + ". Please enter a valid amount.");
                                } else {
                                    break;
                                }
                            } else {
                                System.out.println("Invalid input. Try again.");
                                scanner.nextLine();
                            }
                        }

                        for (int i = 0; i < loanIds.size(); i++) {
                            int loanId = loanIds.get(i);
                            double loanAmt = loanAmounts.get(i);

                            if (repayAmount == 0) break;

                            double deduction = Math.min(loanAmt, repayAmount);

                            String updateLoanQuery = "UPDATE loans SET amount = amount - ? WHERE loan_id = ?";
                            PreparedStatement updateStmt = connection.prepareStatement(updateLoanQuery);
                            updateStmt.setDouble(1, deduction);
                            updateStmt.setInt(2, loanId);
                            updateStmt.executeUpdate();

                            repayAmount -= deduction;
                        }

                        System.out.println("Repayment successful.");
                        break;
                    }

                    case 3: {
                        String checkQuery = "SELECT SUM(amount) as total FROM loans WHERE customer_id = ? AND amount > 0";
                        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                        checkStmt.setInt(1, customerId);
                        ResultSet rs = checkStmt.executeQuery();

                        if (rs.next()) {
                            double total = rs.getDouble("total");
                            if (rs.wasNull() || total <= 0) {
                                System.out.println("You have no active loans.");
                            } else {
                                System.out.println("Total outstanding loan: ₹" + total);
                            }
                        } else {
                            System.out.println("No loan record found.");
                        }
                        break;
                    }

                    default:
                        System.out.println("Invalid option.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("An error occurred during loan processing.");
            }

            System.out.print("\nDo you want to continue? (y/n or yes/no): ");
            String continueChoice = scanner.nextLine().trim().toLowerCase();

            if (!(continueChoice.equals("y") || continueChoice.equals("yes"))) {
                System.out.println("Exiting loan section...");
                break;
            }
        }
    }

}

