package com.atm;

import java.sql.*;
import java.util.Scanner;

public class ATM {
    private final Scanner scanner = new Scanner(System.in);
    private final Connection conn;

    public ATM() throws SQLException {
        conn = Database.getConnection();
    }

    public User login() throws SQLException {
        System.out.print("Enter Account Number: ");
        int accNo = scanner.nextInt();
        System.out.print("Enter PIN: ");
        int pin = scanner.nextInt();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE account_number=? AND pin=?");
        ps.setInt(1, accNo);
        ps.setInt(2, pin);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new User(
                rs.getInt("account_number"),
                rs.getInt("pin"),
                rs.getString("name"),
                rs.getDouble("balance")
            );
        } else {
            System.out.println("Invalid credentials.");
            return null;
        }
    }

    public void checkBalance(User user) {
        System.out.println("Current Balance: ₹" + user.getBalance());
    }

    public void deposit(User user) throws SQLException {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        double newBalance = user.getBalance() + amount;

        PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance=? WHERE account_number=?");
        ps.setDouble(1, newBalance);
        ps.setInt(2, user.getAccountNumber());

        if (ps.executeUpdate() > 0) {
            user.setBalance(newBalance);
            logTransaction(user.getAccountNumber(), "Deposit", amount);
            System.out.println("Deposit successful. New Balance: ₹" + user.getBalance());
        } else {
            System.out.println("Deposit failed.");
        }
    }

    public void withdraw(User user) throws SQLException {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();

        if (amount <= 0 || amount > user.getBalance()) {
            System.out.println("Invalid or insufficient amount.");
            return;
        }

        double newBalance = user.getBalance() - amount;

        PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance=? WHERE account_number=?");
        ps.setDouble(1, newBalance);
        ps.setInt(2, user.getAccountNumber());

        if (ps.executeUpdate() > 0) {
            user.setBalance(newBalance);
            logTransaction(user.getAccountNumber(), "Withdraw", amount);
            System.out.println("Withdrawal successful. New Balance: ₹" + user.getBalance());
        } else {
            System.out.println("Withdrawal failed.");
        }
    }

    public void viewMiniStatement(User user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM transactions WHERE account_number = ? ORDER BY date_time DESC LIMIT 5"
        );
        ps.setInt(1, user.getAccountNumber());
        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== Mini Statement =====");
        System.out.printf("%-20s %-10s %-10s\n", "Date/Time", "Type", "Amount");

        while (rs.next()) {
            Timestamp dateTime = rs.getTimestamp("date_time");
            String type = rs.getString("type");
            double amount = rs.getDouble("amount");

            System.out.printf("%-20s %-10s ₹%.2f\n", dateTime, type, amount);
        }
    }
    
    public void changePin(User user) throws SQLException {
        System.out.print("Enter current PIN: ");
        int currentPin = scanner.nextInt();

        if (currentPin != user.getPin()) {
            System.out.println("Incorrect current PIN.");
            return;
        }

        System.out.print("Enter new PIN (4 digits): ");
        int newPin = scanner.nextInt();

        System.out.print("Confirm new PIN: ");
        int confirmPin = scanner.nextInt();

        if (newPin != confirmPin) {
            System.out.println("PINs do not match.");
            return;
        }

        PreparedStatement ps = conn.prepareStatement("UPDATE users SET pin=? WHERE account_number=?");
        ps.setInt(1, newPin);
        ps.setInt(2, user.getAccountNumber());

        if (ps.executeUpdate() > 0) {
            user = new User(user.getAccountNumber(), newPin, user.getName(), user.getBalance()); // Update session PIN
            System.out.println("PIN changed successfully.");
        } else {
            System.out.println("Failed to change PIN.");
        }
    }


    public void logTransaction(int accNo, String type, double amount) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO transactions(account_number, type, amount, date_time) VALUES (?, ?, ?, NOW())"
        );
        ps.setInt(1, accNo);
        ps.setString(2, type);
        ps.setDouble(3, amount);
        ps.executeUpdate();
    }
}
