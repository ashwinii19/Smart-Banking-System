package com.techlabs.SmartBankingSystem.test;

import java.util.Scanner;

import com.techlabs.SmartBankingSystem.service.BankFacade;

public class SmartBankingSystem {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		BankFacade bankFacade = new BankFacade();

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
						bankFacade.login();
						break;

					case 2:
						bankFacade.createCustomer(); 
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
}
