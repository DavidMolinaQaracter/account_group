package entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import entities.enums.Status;

public class CreditCard extends Card {
    private BigDecimal limit;

    public CreditCard(String cardNumber, LocalDate expirationDate, Status isBlocked, BigDecimal limit) {
        super(cardNumber, expirationDate, isBlocked);
        this.limit = limit;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }
}
