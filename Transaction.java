package com.techlabs.SmartBankingSystem.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Transaction {

	private int transactionId;
	private int toId;
	private int customerId;
	private String type;
	private double amount;
	private LocalDateTime timestamp;

	public Transaction( int toId, int customerId, String type, double amount) {
		super();
		this.toId = toId;
		this.customerId = customerId;
		this.type = type;
		this.amount = amount;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
