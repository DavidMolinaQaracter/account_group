package org.example;

import entities.enums.AccountType;
import entities.enums.Status;
import exceptions.*;
import services.*;
import entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
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
            customer = authService.register(username, firstName, lastName, email, password);
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
            System.out.println("4. Create / Block / Manage card");
            System.out.println("5. Update personal information");
            System.out.println("6. Create / Close account");
            System.out.println("7. View Account Details");
            System.out.println("8. Log off");

            try {
                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1 -> withdraw();
                    case 2 -> deposit();
                    case 3 -> transfer();
                    case 4 -> manageCard();
                    case 5 -> customerService.updateCustomerInfo(customer);
                    case 6 -> manageAccount();
                    case 7 -> showAccountDetails();
                    case 8 -> {
                        System.out.println("Logging off...");
                        customer = null;
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
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
        System.out.print("Select option to perform:");
        System.out.println("1. Create debit card.");
        System.out.println("2. Create credit card.");
        System.out.println("3. Manage card.");
        System.out.println("4. Exit.");

        int i = Integer.parseInt(scanner.nextLine());

        switch (i) {
            case 1:
                System.out.print("Enter debit card number to create: ");
                String cN = scanner.nextLine();

                DebitCard dCard = new DebitCard(cN, LocalDate.now().plusYears(5), ACTIVE);

                if(cardService.addCard(dCard)) {
                    System.out.println("Debit card created successfully.");
                } else {
                    System.out.println("Debit card creation failed.");
                }

                break;
            case 3:
                System.out.print("Enter card number: ");
                String cardNumber = scanner.nextLine();

                Card card = cardService.getCard(cardNumber);

                if(card == null){
                    System.out.println("Invalid card number.");
                } else {
                    System.out.println("Select option to perform:");
                    System.out.println("1. Block card.");
                    System.out.println("2. Update limit");

                    int j = scanner.nextInt();
                    switch (j) {
                        case 1:
                            try {
                                cardService.blockCard(card.getCardNumber());
                                System.out.println("Card successfully blocked.");
                            } catch (CardBlockedException e) {
                                System.out.println("Card is already blocked.");
                            }
                            break;
                        case 2:
                            BigDecimal bg = scanner.nextBigDecimal();

                            if (cardService.updateLimit(card.getCardNumber(), bg)) {
                                System.out.println("Card limit successfully updated.");
                            } else {
                                System.out.println("Error updating card limit.");
                            }
                            break;
                        default:
                            System.out.println("Invalid option.");
                            break;
                    }
                }
                break;
            case 2:
                System.out.print("Enter credit card number to create: ");
                String ccN = scanner.nextLine();

                System.out.print("Enter limit amount: ");
                BigDecimal bg = scanner.nextBigDecimal();

                CreditCard cCard = new CreditCard(ccN, LocalDate.now().plusYears(5), ACTIVE, bg);

                if(cardService.addCard(cCard)) {
                    System.out.println("Debit card created successfully.");
                } else {
                    System.out.println("Debit card creation failed.");
                }
                break;
                case 4:
                    System.out.print("Returning to main menu.");
                    break;
            default:
                System.out.println("Invalid option.");
                break;

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

    private static void showAccountDetails() {
        System.out.println("\n--- VIEW ACCOUNT DETAILS ---");
        System.out.print("Enter Account ID: ");

        try {
            Long accountId = Long.parseLong(scanner.nextLine());

            if (!checkAccountID(accountId)) {
                return;
            }

            Account account = accountService.getAccount(accountId);

            if (account == null) {
                System.out.println("Account not found (System Error).");
                return;
            }

            System.out.println(" Account ID: " + account.getAccountId());
            System.out.println(" Type:       " + account.getType());
            System.out.println(" Balance:    " + account.getBalance());
            System.out.println(" Status:     " + account.getStatus());

            Map<String, Card> accountCards = account.getCards();

            System.out.println(" Linked Cards (" + accountCards.size() + "):");

            if (accountCards.isEmpty()) {
                System.out.println("No cards attached to this account.");
            } else {
                for (Card c : accountCards.values()) {
                    System.out.println("  ------------------");
                    System.out.println("  Number: " + c.getCardNumber());

                    if (c instanceof CreditCard) {
                        System.out.println("  Type:   CREDIT");
                        System.out.println("  Limit:  " + ((CreditCard) c).getLimit());
                    } else {
                        System.out.println("  Type:   DEBIT");
                    }
                    String status = (c.getIsBlocked() == Status.CLOSED) ? "BLOCKED" : "ACTIVE";
                    System.out.println("  Status: " + status);
                    System.out.println("  Expires:" + c.getExpirationDate());
                }
                System.out.println("  ------------------");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID");
        } catch (Exception e) {
        }
    }
}