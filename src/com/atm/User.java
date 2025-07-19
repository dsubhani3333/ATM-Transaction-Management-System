package com.atm;

public class User {
	private int accountNumber;
	private int pin;
	private String name;
	private double balance;

	public User(int accountNumber, int pin, String name, double balance) {
		this.accountNumber = accountNumber;
		this.pin = pin;
		this.name = name;
		this.balance = balance;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public int getPin() {
		return pin;
	}

	public String getName() {
		return name;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
}
