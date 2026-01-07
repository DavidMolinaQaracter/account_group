package org.example;

import entities.enums.AccountType;
import entities.enums.Status;
import exceptions.*;
import services.*;
import entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import static entities.enums.Status.ACTIVE;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Customer customer;
    // Services (injected / created)
    private static final AuthService authService = new AuthService();
    private static final CustomerService customerService = new CustomerService();
    private static final AccountService accountService = new AccountService();
    private static final CreditService creditService = new CreditService();
    private static final CardService cardService = new CardService();
    private static final AlertService alertService = new AlertService();
    private static final TransactionService transactionService = new TransactionService(accountService, creditService, alertService );


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

    private static void showMainMenu() throws AccountNotFoundException {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Withdraw money");
            System.out.println("2. Deposit money");
            System.out.println("3. Transfer money");
            System.out.println("4. Block / Manage card");
            System.out.println("5. Update personal information");
            System.out.println("6. Create / Close account");
            System.out.println("7. Exit");

            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1 -> withdraw();
                case 2 -> deposit();
                case 3 -> transfer();
                case 4 -> manageCard();
                case 5 -> customerService.updateCustomerInfo(customer);
                case 6 -> manageAccount();
                case 7 -> showLoginMenu();
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

    private static void deposit() {
        System.out.print("Account ID: ");
        Long accountId = Long.parseLong(scanner.nextLine());
        if (!checkAccountID(accountId))
            return;
        System.out.print("Amount: ");
        BigDecimal amount = BigDecimal.valueOf(Long.parseLong(scanner.nextLine()));
        try {
            transactionService.deposit(accountId, amount, true);
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
        System.out.println("1. Add card");
        System.out.println("2. Block card");
        System.out.println("3. Update limit");

        int option = Integer.parseInt(scanner.nextLine());

        switch (option) {
            case 1 -> {
                System.out.println("Select card type:");
                System.out.println("1. Credit card");
                System.out.println("2. Debit card");

                int cardType = Integer.parseInt(scanner.nextLine());

                System.out.println("Write Card Number:");
                String cardNumber = scanner.nextLine();

                System.out.println("Write Expiration Date (YYYY-MM-DD):");
                LocalDate expiryDate;
                try {
                    expiryDate = LocalDate.parse(scanner.nextLine());
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                    return;
                }

                if (cardType == 1) {
                    System.out.println("Write Credit Card Limit:");
                    BigDecimal limit = new BigDecimal(scanner.nextLine());

                    CreditCard creditCard = new CreditCard(cardNumber, expiryDate, Status.ACTIVE, limit);

                    cardService.addCard(creditCard);

                } else if (cardType == 2) {
                    DebitCard debitCard = new DebitCard(cardNumber, expiryDate, Status.ACTIVE, BigDecimal.ZERO);

                    cardService.addCard(debitCard);
                } else {
                    System.out.println("Invalid card type.");
                }
            }

            case 2 -> {
                System.out.println("Write card number to block:");
                String cardNumberToBlock = scanner.nextLine();

                if (cardService.getCard(cardNumberToBlock) == null) {
                    System.out.println("Card does not exist.");
                    return;
                };

                try {
                    cardService.blockCard(cardNumberToBlock);
                    System.out.println("Card blocked successfully.");
                } catch (CardBlockedException e) {
                    System.out.println("Card is already blocked.");
                }
            }

            case 3 -> {
                System.out.println("Write card number to update limit:");
                String cardNumberToUpdateLimit = scanner.nextLine();

                if (cardService.getCard(cardNumberToUpdateLimit) == null) {
                    System.out.println("Card does not exist.");
                    return;
                };

                System.out.println("Write new limit:");
                BigDecimal newLimit = new BigDecimal(scanner.nextLine());

                boolean updated = cardService.updateLimit(cardNumberToUpdateLimit, newLimit);

                if (!updated) {
                    System.out.println("Limit update failed.");
                } else {
                    System.out.println("Limit updated successfully.");
                }
            }

            default -> System.out.println("Invalid option.");
        }
    }

    private static void manageAccount() throws AccountNotFoundException {
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