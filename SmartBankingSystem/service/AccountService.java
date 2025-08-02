package com.techlabs.SmartBankingSystem.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import com.techlabs.SmartBankingSystem.model.Account;
import com.techlabs.SmartBankingSystem.model.AccountType;
import com.techlabs.SmartBankingSystem.model.Customer;

public class AccountService {

	final String URL = "jdbc:mysql://localhost:3306/smart_bank_system";
	final String USER = "root";
	final String PASS = "admin#123";

	Scanner scanner = new Scanner(System.in);

	public String createAccount(int customer_id) {

		if (customer_id == -1) {
			System.out.println("Customer creation failed.");
			return null;
		}

		System.out.println("\n=== Create Account for Customer ===");
		System.out.println("Select Account Type:");
		System.out.println("1. Saving");
		System.out.println("2. Current");
		System.out.println("3. Fixed");

		int typeChoice;
		AccountType accountType = null;

		while (accountType == null) {
			System.out.print("Enter your choice (1-3): ");
			typeChoice = scanner.nextInt();
			scanner.nextLine();

			switch (typeChoice) {
			case 1:
				accountType = AccountType.SAVINGS;
				break;
			case 2:
				accountType = AccountType.CURRENT;
				break;
			case 3:
				accountType = AccountType.FIXED;
				break;
			default:
				System.out.println("Invalid choice. Please select 1, 2, or 3.");
			}
		}

		double initialbalance = 0.0;
		while (true) {
			System.out.println("Enter the initial deposit amount:");
			if (scanner.hasNextDouble()) {
				initialbalance = scanner.nextDouble();
				scanner.nextLine();
				if (initialbalance < 0) {
					System.out.println("Amount cannot be negative. Try again.");
					continue;
				}
				break;
			} else {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine();
			}
		}

		LocalDateTime dateCreated = LocalDateTime.now();

		String account_number = "ACCT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

		Account newAccount = new Account(account_number, customer_id, accountType, initialbalance);

		try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
			String insertQuery = "INSERT INTO accounts(accountNumber, accountType, balance, status, dateCreated, customer_id) VALUES (?, ?, ?, 'ACTIVE', ?, ?)";
			PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, newAccount.getAccount_number());
			ps.setString(2, newAccount.getAccount_type().toString());
			ps.setDouble(3, newAccount.getBalance());
			ps.setTimestamp(4, Timestamp.valueOf(newAccount.getDateCreated()));
			ps.setInt(5, newAccount.getCustomerId());

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Account created successfully. Account Number: " + account_number);
				return account_number;
			} else {
				System.out.println("Failed to create account.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
