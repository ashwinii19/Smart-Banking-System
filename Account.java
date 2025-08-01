package com.techlabs.SmartBankingSystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Account {

	private String account_number;
	private int customerId;
	private AccountType account_type;
	private double balance;
	private Status status;
	private LocalDateTime dateCreated;

	public Account(String account_number, int customerId, AccountType account_type, double balance) {
		this.account_number = account_number;
		this.customerId = customerId;
		this.account_type = account_type;
		this.balance = balance;
		this.status = Status.ACTIVE;
		this.dateCreated = LocalDateTime.now();
	}

	public String getAccount_number() {
		return account_number;
	}

	public int getCustomerId() {
		return customerId;
	}

	public AccountType getAccount_type() {
		return account_type;
	}

	public double getBalance() {
		return balance;
	}

	public Status getStatus() {
		return status;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setAccount_type(AccountType account_type) {
		this.account_type = account_type;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
