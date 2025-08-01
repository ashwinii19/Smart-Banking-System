package com.techlabs.SmartBankingSystem.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.techlabs.SmartBankingSystem.model.Customer;

public class CustomerService {

	final String URL = "jdbc:mysql://localhost:3306/smart_bank_system";
	final String USER = "root";
	final String PASS = "admin#123";

	Scanner scanner = new Scanner(System.in);

	private boolean isNameValid(String name) {
		return Pattern.matches("^[a-zA-Z ]+$", name);
	}

	private boolean isEmailValid(String email) {
		return Pattern.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$", email);
	}

	private boolean isPhoneValid(String phone) {
		return Pattern.matches("^[6-9]\\d{9}$", phone);
	}

	private boolean isPasswordValid(String password) {
		return Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", password);
	}


	public int createCustomer() {

		System.out.println("--- Let's create a new account. ---");

		String name;
		while (true) {
			System.out.println("Enter the user's name:");
			name = scanner.nextLine();
			if (isNameValid(name)) {
				break;
			} else {
				System.out.println("Invalid name. Please enter only letters and spaces.");
			}
		}

		String email;
		while (true) {
			System.out.print("Enter email: ");
			email = scanner.nextLine();
			if (isEmailValid(email)) {
				break;
			} else {
				System.out.println("Invalid email.");
			}
		}

		String phone;
		while (true) {
			System.out.print("Enter phone: ");
			phone = scanner.nextLine();
			if (isPhoneValid(phone)) {
				break;
			} else {
				System.out.println("Invalid phone. Enter exactly 10 digits.");
			}
		}

		System.out.print("Enter address: ");
		String address = scanner.nextLine();

		String password;
		while (true) {
			System.out.print("Enter password: ");
			password = scanner.nextLine();
			if (isPasswordValid(password)) {
				break;
			} else {
				System.out.println("Invalid password.");
			}
		}

		Customer customer = new Customer(name, email, phone, address, password);

		try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
			String insertQuery = "INSERT INTO customers(name, email, phone, address, password) VALUES(?,?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, customer.getName());
			ps.setString(2, customer.getEmail());
			ps.setString(3, customer.getPhone());
			ps.setString(4, customer.getAddress());
			ps.setString(5, customer.getPassword());

			int rows = ps.executeUpdate();
			if (rows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void editProfile(int customerId) {
		System.out.println("\n--- Edit Your Profile ---");

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {

			String updateQuery = "UPDATE customers SET %s = ? WHERE customer_id = ?";
			boolean updated = false;

			System.out.print("Do you want to update your name? (yes/no): ");
			String choice = scanner.nextLine().trim().toLowerCase();
			if (choice.equals("yes") || choice.equals("y")) {
				System.out.print("Enter new name: ");
				String newName = scanner.nextLine();
				if (isNameValid(newName)) {
					try (PreparedStatement ps = conn.prepareStatement(String.format(updateQuery, "name"))) {
						ps.setString(1, newName);
						ps.setInt(2, customerId);
						ps.executeUpdate();
						System.out.println("Name updated.");
						updated = true;
					}
				} else {
					System.out.println("Invalid name. Skipping update.");
				}
			}

			System.out.print("Do you want to update your email? (yes/no): ");
			choice = scanner.nextLine().trim().toLowerCase();
			if (choice.equals("yes") || choice.equals("y")) {
				System.out.print("Enter new email: ");
				String newEmail = scanner.nextLine();
				if (isEmailValid(newEmail)) {
					try (PreparedStatement ps = conn.prepareStatement(String.format(updateQuery, "email"))) {
						ps.setString(1, newEmail);
						ps.setInt(2, customerId);
						ps.executeUpdate();
						System.out.println("Email updated.");
						updated = true;
					}
				} else {
					System.out.println("Invalid email. Skipping update.");
				}
			}

			System.out.print("Do you want to update your phone? (yes/no): ");
			choice = scanner.nextLine().trim().toLowerCase();
			if (choice.equals("yes") || choice.equals("y")) {
				System.out.print("Enter new phone: ");
				String newPhone = scanner.nextLine();
				if (isPhoneValid(newPhone)) {
					try (PreparedStatement ps = conn.prepareStatement(String.format(updateQuery, "phone"))) {
						ps.setString(1, newPhone);
						ps.setInt(2, customerId);
						ps.executeUpdate();
						System.out.println("Phone number updated.");
						updated = true;
					}
				} else {
					System.out.println("Invalid phone. Skipping update.");
				}
			}

			System.out.print("Do you want to update your address? (yes/no): ");
			choice = scanner.nextLine().trim().toLowerCase();
			if (choice.equals("yes") || choice.equals("y")) {
				System.out.print("Enter new address: ");
				String newAddress = scanner.nextLine();
				try (PreparedStatement ps = conn.prepareStatement(String.format(updateQuery, "address"))) {
					ps.setString(1, newAddress);
					ps.setInt(2, customerId);
					ps.executeUpdate();
					System.out.println("Address updated.");
					updated = true;
				}
			}
			
			System.out.print("Do you want to update your password? (yes/no): ");
			choice = scanner.nextLine().trim().toLowerCase();
			if (choice.equals("yes") || choice.equals("y")) {
			    System.out.print("Enter new password: ");
			    String newPassword = scanner.nextLine();
			    if (isPasswordValid(newPassword)) {  
			        try (PreparedStatement ps = conn.prepareStatement(String.format(updateQuery, "password"))) {
			            ps.setString(1, newPassword);
			            ps.setInt(2, customerId);
			            ps.executeUpdate();
			            System.out.println("Password updated.");
			            updated = true;
			        }
			    } else {
			        System.out.println("Invalid password. Skipping update.");
			    }
			}


			if (!updated) {
				System.out.println("No changes were made.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Something went wrong while updating profile.");
		}
	}

}
