package org.example;

import java.time.LocalDate;

public abstract class Card {
    private String cardNumber;
    private LocalDate expirationDate;
    private Status isBlocked;

    protected Card(String cardNumber, LocalDate expirationDate, Status isBlocked) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.isBlocked = isBlocked;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Status getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Status isBlocked) {
        this.isBlocked = isBlocked;
    }
}
