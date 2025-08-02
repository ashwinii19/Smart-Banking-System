package com.techlabs.SmartBankingSystem.test;

import java.util.Scanner;

import com.techlabs.SmartBankingSystem.service.BankFacade;

public class SmartBankingSystem {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		BankFacade bankFacade = new BankFacade();
		bankFacade.start();
	}
}
