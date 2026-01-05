package services;
import entities.enums.Result;
import entities.Transaction;
import entities.enums.TransactionType;
import exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TransactionService {

    private Map<Long, Transaction> transactionHistory = new HashMap<>();
    private long operationCounter = 1;

    // Servicios con los que hablo
    private final AccountService accountService;
    private final CreditService creditService;
    private final AlertService alertService;

    public TransactionService(AccountService accountService, CreditService creditService, AlertService alertService) {
        this.accountService = accountService;
        this.creditService = creditService;
        this.alertService = alertService;
    }

    // Metodo retirar
    public void withdraw(Long accountId, BigDecimal amount, boolean register) throws InsufficientFundsException {
        Account acc = accountService.getAccountsMap().get(accountId);

        //Lógica de dinero (Saldo o Sobregiro)
        if (acc.getBalance().compareTo(amount) < 0) {
            if (!creditService.applyOverdraft(accountId)) {
                saveRecord(acc, amount, Result.FAILED); // Registro fallo
                throw new InsufficientFundsException("No money");
            }
        }

        //Actualizar saldo
        acc.setBalance(acc.getBalance().subtract(amount));

        //Registro y alerta
        if(register) {
            Transaction t = new Transaction(operationCounter++, amount, LocalDateTime.now(), TransactionType.WITHDRAWN, accountId, Result.SUCCESS);
            transactionHistory.put(operationCounter, t);
        }

        if (acc.getBalance().compareTo(new BigDecimal("100")) < 0) {
            alertService.sendLowBalanceAlert(accountId);
        }
    }

    // deposit
    public void deposit (Long destId, BigDecimal amount, boolean register) throws InsufficientFundsException {
        // Ponemos en la otra
        Account dest = accountService.getAccountsMap().get(destId);
        dest.setBalance(dest.getBalance().add(amount));

        // Registro de la transferencia
        if(register) {
            Transaction t = new Transaction(operationCounter++, amount, LocalDateTime.now(), TransactionType.DEPOSIT, destId, Result.SUCCESS);
            transactionHistory.put(operationCounter, t);
        }
    }


    // Metodo transferir
    public void transfer(Long srcId, Long destId, BigDecimal amount) throws InvalidTransactionException {
        try {
            // Quitamos de una cuenta (ya registra el retiro solo)
            withdraw(srcId, amount, false);
            deposit(destId, amount, false);
            Transaction t = new Transaction(operationCounter++, amount, LocalDateTime.now(), srcId, destId, Result.SUCCESS);
            transactionHistory.put(operationCounter, t);

        } catch (Exception e) {
            throw new InvalidTransactionException("Transfer failed");
        }
    }
}