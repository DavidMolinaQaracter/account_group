package services;

import java.math.BigDecimal;
import java.util.HashMap;
import entities.Card;
import entities.enums.Status;
import entities.CreditCard;
import exceptions.CardBlockedException;
import entities.Account;

public class CardService {
    HashMap<String, Card> cards;

    public CardService() {
        cards = new HashMap<>();
    }

    public boolean addCard(Account account, Card card) {
        // Return false if the card or the card number is null.
        if (account == null || card == null || card.getCardNumber() == null) {
            return false;
        }

        // Prevent duplicate card numbers
        if (cards.containsKey(card.getCardNumber())) {
            return false;
        }

        // Prevent account having duplicate card numbers
        if (account.getCards().containsKey(card.getCardNumber())) {
            return false;
        }

        cards.put(card.getCardNumber(), card);
        account.addCard(card);
        return true;
    }

    public void blockCard(Account account, String cardNum) throws CardBlockedException {
        // Cards list is empty or invalid card number
        if (cards.isEmpty() || cardNum == null) {
            return;
        }

        Card card = account.getCard(cardNum);

        // Card with the requested card number is not in the account cards hashmap.
        if (card == null) {
            return;
        }

        if (card.getIsBlocked() == Status.CLOSED) {
            throw new CardBlockedException();
        }

        card.setIsBlocked(Status.CLOSED);
    }

    public boolean updateLimit(Account account, String cardNum, BigDecimal newLimit) {
        // Cards list is empty, invalid card number or limit
        if (cards.isEmpty() || cardNum == null || newLimit.signum() <= 0) {
            return false;
        }

        Card card = account.getCard(cardNum);

        // Card with the requested card number is not in the account cards hashmap.
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

    public Card  getCard(Account account, String cardNum) {
        Card card = account.getCard(cardNum);
        if (cards.isEmpty() || cardNum == null) {
            return null;
        }

        // Card with the requested card number is not in the account cards hashmap.
        if (card == null) {
            return null;
        }

        return card;
    }
}
