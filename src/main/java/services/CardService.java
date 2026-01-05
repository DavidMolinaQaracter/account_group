package org.example;

import java.math.BigDecimal;
import java.util.HashMap;

public class CardService {
    HashMap<String, Card> cards;

    public CardService() {
        cards = new HashMap<>();
    }

    public boolean addCard(Card card) {
        // Return false if the card or the card number is null.
        if (card == null || card.getCardNumber() == null) {
            return false;
        }

        // Prevent duplicate card numbers
        if (cards.containsKey(card.getCardNumber())) {
            return false;
        }

        cards.put(card.getCardNumber(), card);
        return true;
    }

    // TODO: Will throw CardBlockedException
    public void blockCard(String cardNum) {
        // Cards list is empty or invalid card number
        if (cards.isEmpty() || cardNum == null) {
            return;
        }

        Card card = cards.get(cardNum);

        // Card with the requested card number is not in the cards hashmap.
        if (card == null) {
            return;
        }

        card.setIsBlocked(Status.CLOSED);
    }

    public boolean updateLimit(String cardNum, BigDecimal newLimit) {
        // Cards list is empty, invalid card number or limit
        if (cards.isEmpty() || cardNum == null || newLimit.signum() <= 0) {
            return false;
        }

        Card card = cards.get(cardNum);

        // Card with the requested card number is not in the cards hashmap.
        if (card == null) {
            return false;
        }

        if (card instanceof CreditCard creditCard) {
            creditCard.setLimit(newLimit);
            return true;
        } else {
            // Card exists but is not a credit card
            return false;
        }


    }
}
