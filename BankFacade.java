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

	public void login() {
		System.out.print("Enter your customer ID: ");
		if (!scanner.hasNextInt()) {
			System.out.println("Invalid input. Please enter a number.");
			scanner.nextLine();
			return;
		}
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
				transactionService.depositMoney();
				break;
			case 3:
				transactionService.withdrawMoney();
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
			    System.out.println("Logging out...");
			    loggedIn = false;
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
