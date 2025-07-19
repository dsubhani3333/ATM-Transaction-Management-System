package com.atm;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            ATM atm = new ATM();
            User user = atm.login();

            if (user == null) return;

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n===== ATM MENU =====");
                System.out.println("1. Check Balance");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Mini Statement");
                System.out.println("5. Change PIN");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> atm.checkBalance(user);
                    case 2 -> atm.deposit(user);
                    case 3 -> atm.withdraw(user);
                    case 4 -> atm.viewMiniStatement(user);
                    case 5 -> atm.changePin(user);
                    case 6 -> System.out.println("Thank you for using the ATM.");
                    default -> System.out.println("Invalid choice.");
                }

            } while (choice != 6);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
