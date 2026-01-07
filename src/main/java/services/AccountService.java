package services;

import entities.*;
import entities.enums.AccountType;
import entities.enums.Status;
import exceptions.*;

import java.util.HashMap;
import java.util.Map;

public class AccountService {

    private Map<Long, Account> accounts = new HashMap<>();

    public Account getAccount(Long accId) {
        return accounts.get(accId);
    }

    public Account createAccount(Customer c,long accId, AccountType type, Status s, Card card) throws DuplicateAccountException {
        if (accounts.containsKey(accId)) {
            throw new DuplicateAccountException();
        }

        Account acc = new Account(c, accId, type, s, card);
        accounts.put(accId, acc);

        return acc;
    }


    public Account closeAccount(long accId) throws AccountNotFoundException {
        if (!accounts.containsKey(accId)) {
            throw new AccountNotFoundException();
        }

        return accounts.remove(accId);
    }

    public Map<Long, Account> getAllAccounts() {
        return new HashMap<>(accounts);
    }

    public boolean checkAccountFromCustomer(Customer c,long accId) throws AccountNotFoundException {
        if (!accounts.containsKey(accId)) {
            throw new AccountNotFoundException();
        }

        return accounts.get(accId).getCustomer().equals(c);
    }
}