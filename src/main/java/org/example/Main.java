package org.example;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    // Services (injected / created)
    private static final AuthService authService = new AuthService();
    private static final TransactionService transactionService = new TransactionService();
    private static final CustomerService customerService = new CustomerService();
    private static final AccountService accountService = new AccountService();
    private static final CardService cardService = new CardService();

    public static void main(String[] args) {
        showLoginMenu();
    }

    private static void showLoginMenu() {
        System.out.println("=== Welcome to the Bank ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();
        try {
            authService.login(username, password);
            showMainMenu();
        } catch (Exception e){
                System.out.println("Invalid credentials.");
        }
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Withdraw money");
            System.out.println("2. Transfer money");
            System.out.println("3. Block / Manage card");
            System.out.println("4. Update personal information");
            System.out.println("5. Create / Close account");
            System.out.println("6. Exit");

            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1 -> withdraw();
                case 2 -> transfer();
                case 3 -> manageCard();
                case 4 -> updateProfile();
                case 5 -> manageAccount();
                case 6 -> System.exit(0);
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void withdraw() {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        transactionService.withdraw(accountId, amount);
    }

    private static void transfer() {
        System.out.print("From Account ID: ");
        String from = scanner.nextLine();
        System.out.print("To Account ID: ");
        String to = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        transactionService.transfer(from, to, amount);
    }

    private static void manageCard() {
        System.out.print("Card number: ");
        String cardNumber = scanner.nextLine();
        cardService.blockCard(cardNumber);
    }

    private static void updateProfile() {
        System.out.print("New email: ");
        String email = scanner.nextLine();
        customerService.updatePersonalInfo(email);
    }

    private static void manageAccount() {
        System.out.println("1. Create account");
        System.out.println("2. Close account");
        int option = Integer.parseInt(scanner.nextLine());

        if (option == 1) {
            accountService.createAccount();
        } else if (option == 2) {
            System.out.print("Account ID: ");
            String id = scanner.nextLine();
            accountService.closeAccount(id);
        }
    }
}