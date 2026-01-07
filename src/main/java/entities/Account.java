package entities;

import entities.enums.AccountType;
import entities.enums.Status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Account {

    private Customer customer;
    private Map<String, Card> cards;
    private int accountId;
    private BigDecimal balance;
    private AccountType type;
    private Status status;

    public Account(Customer c, int accId, AccountType type, Status s, Card card) {
        this.customer = c;
        this.accountId = accId;
        this.balance = BigDecimal.valueOf(0.0);
        this.type = type;
        this.status = s;

        this.cards = new HashMap<>();

        if (card != null) {
            addCard(card);
        }
    }

    public void addCard(Card card) {
        if (card != null && card.getCardNumber() != null) {
            this.cards.put(card.getCardNumber(), card);
        }
    }

    public void removeCard(String cardNumber) {
        if (this.cards.containsKey(cardNumber)) {
            this.cards.remove(cardNumber);
        }
    }

    public Card getCard(String cardNumber) {
        return this.cards.get(cardNumber);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Map<String, Card> getCards() {
        return new HashMap<>(this.cards);
    }

    public int getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}