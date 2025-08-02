package com.techlabs.SmartBankingSystem.service;

import java.util.Scanner;

public class BankFacade {

	private CustomerService customerService;
	private AccountService accountService;
	private TransactionService transactionService;
	private LoginService loginService;
	private LoanService loanService;

	public BankFacade() {
		customerService = new CustomerService();
		accountService = new AccountService();
		transactionService = new TransactionService();
		loginService = new LoginService();
		loanService = new LoanService();
	}

	Scanner scanner = new Scanner(System.in);

	public void start() {
		while (true) {
			System.out.println("=== Welcome to Smart Banking System ===");
			System.out.println("1. Login");
			System.out.println("2. Create Account");
			System.out.println("3. Exit");
			System.out.print("Enter your choice: ");

			try {
				if (scanner.hasNextInt()) {
					int choice = scanner.nextInt();
					scanner.nextLine();

					switch (choice) {
					case 1:
						login();
						break;

					case 2:
						createCustomer();
						break;

					case 3:
						System.out.println("Thank you for using Smart Banking System!");
						scanner.close();
						return;

					default:
						System.out.println("Invalid option. Please try again.");
					}
				} else {
					System.out.println("Please enter a valid number.");
					scanner.nextLine();
				}
			} catch (Exception e) {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine();
			}
		}
	}

	public void login() {

		System.out.print("Do you want to login? (yes/no): ");
		String wantLogin = scanner.nextLine().trim().toLowerCase();

		if (!wantLogin.equals("yes") && !wantLogin.equals("y")) {
			System.out.println("Login cancelled.");
			return;
		}

		System.out.print("Enter your customer ID: ");
		int customerId = scanner.nextInt();
		scanner.nextLine();

		System.out.print("Enter your password: ");
		String inputPassword = scanner.nextLine();

		if (!loginService.validateCustomer(customerId, inputPassword)) {
			System.out.println("Invalid credentials or inactive account.");
			return;
		}

		System.out.println("Login successful!");

		boolean loggedIn = true;
		while (loggedIn) {
			System.out.println("\n--- Customer Menu ---");
			System.out.println("1. Check Balance");
			System.out.println("2. Deposit");
			System.out.println("3. Withdraw");
			System.out.println("4. Transfer");
			System.out.println("5. Transaction History");
			System.out.println("6. Loan");
			System.out.println("7. Edit Profile");
			System.out.println("8. Close Account");
			System.out.println("9. Logout");
			System.out.print("Enter your choice: ");

			if (!scanner.hasNextInt()) {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine();
				continue;
			}

			int option = scanner.nextInt();
			scanner.nextLine();

			switch (option) {
			case 1:
				transactionService.checkBalance(customerId);
				break;
			case 2:
				transactionService.depositMoney(customerId);
				break;
			case 3:
				transactionService.withdrawMoney(customerId);
				break;
			case 4:
				transactionService.transferMoney(customerId);
				break;
			case 5:
				transactionService.transactionHistory(customerId);
				break;
			case 6:
				loanService.applyLoan(customerId);
				break;
			case 7:
				customerService.editProfile(customerId);
				break;
			case 8:
				transactionService.closeAccount(customerId);
				loggedIn = false;
				break;
			case 9:
				System.out.print("Are you sure you want to logout? (yes/no): ");
				String confirmLogout = scanner.nextLine().trim().toLowerCase();
				if (confirmLogout.equals("yes") || confirmLogout.equals("y")) {
					System.out.println("Logging out...");
					loggedIn = false;
				} else {
					System.out.println("Logout cancelled.");
				}
				break;
			default:
				System.out.println("Invalid option. Try again.");
			}
		}

	}

	public int createCustomer() {
		int customer_id = customerService.createCustomer();
		if (customer_id != -1) {
			System.out.println("Customer created successfully! Customer ID: " + customer_id);
			accountService.createAccount(customer_id);
		} else {
			System.out.println("Customer creation failed.");
		}
		return customer_id;
	}

}
