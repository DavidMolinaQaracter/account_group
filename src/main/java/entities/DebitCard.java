package entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import entities.enums.Status;

public class DebitCard extends Card {

    public DebitCard(String cardNumber, LocalDate expirationDate, Status isBlocked) {
        super(cardNumber, expirationDate, isBlocked);
    }
}
