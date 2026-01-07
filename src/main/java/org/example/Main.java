package org.example;

import entities.enums.AccountType;
import entities.enums.Status;
import exceptions.*;
import services.*;
import entities.*;

import java.math.BigDecimal;
import java.util.Scanner;

import static entities.enums.Status.ACTIVE;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Customer customer;
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

        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1 -> login();
                case 2 -> register();
                case 3 -> System.exit(0);
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void login(){
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();
        try {
            customer = authService.login(username, password);
            showMainMenu();
        } catch (Exception e){
            System.out.println("Invalid credentials.");
        }
    }

    private static void register(){
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        try {
            authService.register(username, firstName, lastName, email, password);
            showMainMenu();
        } catch (Exception e){
            System.out.println("Account already exists for this username.");
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
                case 4 -> customerService.updateCustomerInfo(customer);
                case 5 -> manageAccount();
                case 6 -> System.exit(0);
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void withdraw() {
        System.out.print("Account ID: ");
        Long accountId = Long.parseLong(scanner.nextLine());
        if (!checkAccountID(accountId))
            return;
        System.out.print("Amount: ");
        BigDecimal amount = BigDecimal.valueOf(Long.parseLong(scanner.nextLine()));
        try {
            transactionService.withdraw(accountId, amount, true);
        } catch (InsufficientFundsException e){
            System.out.println("Insufficient funds, operation cancelled");
        }
    }

    private static void transfer() {
        System.out.print("From Account ID: ");
        long from = Long.parseLong(scanner.nextLine());
        if (!checkAccountID(from))
            return;
        System.out.print("To Account ID: ");
        Long to = Long.parseLong(scanner.nextLine());
        System.out.print("Amount: ");
        BigDecimal amount = BigDecimal.valueOf(Long.parseLong(scanner.nextLine()));
        try {
            transactionService.transfer(from, to, amount);
        } catch (InvalidTransactionException e){
            System.out.println("Invalid transaction, operation cancelled");
        }
    }

    private static void manageCard() {
        System.out.print("Card number: ");
        String cardNumber = scanner.nextLine();
    }

    private static void manageAccount() {
        System.out.println("1. Create account");
        System.out.println("2. Close account");
        int option = Integer.parseInt(scanner.nextLine());

        if (option == 1) {
            try{

                System.out.println("Enter account number: ");
                int id = Integer.parseInt(scanner.nextLine());
                System.out.println("Enter account type (default: savings)");
                System.out.println(" 1: Savings");
                System.out.println(" 2: Current");
                int type = Integer.parseInt(scanner.nextLine());
                AccountType accountType;
                switch (type) {
                    case 1 -> accountType = AccountType.SAVINGS;
                    case 2 -> accountType = AccountType.CURRENT;
                    default -> accountType = AccountType.SAVINGS;
                }
                Account account = accountService.createAccount(customer, id, accountType, Status.ACTIVE, null);
            } catch (DuplicateAccountException e){
                System.out.println("Duplicate account, operation cancelled");
            }
        } else if (option == 2) {
            System.out.print("Account ID: ");
            long id = Long.parseLong(scanner.nextLine());
            if (!checkAccountID(id))
                return;
            accountService.closeAccount(id);
        }
    }

    private static boolean checkAccountID(long id){
        boolean result = false;
        try {
            if (accountService.checkAccountFromCustomer(customer, id)) {
                result = true;
            } else {
                System.out.println("This account does not belong to the current user");
            }
        } catch (Exception e) {
            System.out.println("Invalid account.");
        }
        return result;
    }
}