package entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import entities.enums.Status;

public class DebitCard extends Card {
    private BigDecimal balance;

    public DebitCard(String cardNumber, LocalDate expirationDate, Status isBlocked, BigDecimal balance) {
        super(cardNumber, expirationDate, isBlocked);
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
