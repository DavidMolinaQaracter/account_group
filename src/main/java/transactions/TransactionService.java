package transactions;
import entities.enums.Result;
import exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.LocalDate;
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
            saveRecord(acc, amount, Result.SUCCESS);
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
            Transaction tx = new Transaction(operationCounter++, amount, LocalDate.now(),
                    accountService.getAccountsMap().get(srcId), dest, Result.SUCCESS);
            transactionHistory.put(tx.getOperationId(), tx);
        }
    }




    // Metodo transferir
    public void transfer(Long srcId, Long destId, BigDecimal amount) throws InvalidTransactionException {
        try {
            // Quitamos de una cuenta (ya registra el retiro solo)
            withdraw(srcId, amount, false);
            deposit(destId, amount, false);

        } catch (Exception e) {
            throw new InvalidTransactionException("Transfer failed");
        }
    }

    // Función auxiliar para no repetir código al guardar
    private void saveRecord(Account acc, BigDecimal amount, Result res) {
        Transaction tx = new Transaction(operationCounter++, amount, LocalDate.now(), acc, res);
        transactionHistory.put(tx.getOperationId(), tx);
    }
}