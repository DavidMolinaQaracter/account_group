package entities;

import entities.enums.Result;
import entities.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class Transaction {
    // CLASS ATRIBUTES
    private long operationId;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionType transactionType;
    private long mainAccountID;
    private long transferToAccountID;
    private double creditAmount;
    private Result result;

    // CONSTRUCTORS
    // WITHDRAWAL
    public Transaction(long operationId, BigDecimal amount, LocalDateTime date, TransactionType transactionType, long mainAccountID, Result result) {
        this.operationId = operationId;
        this.amount = amount;
        this.date = date;
        this.transactionType = transactionType;
        this.mainAccountID = mainAccountID;
        this.transferToAccountID = 0;
        this.creditAmount = 0;
        this.result = result;
    }

    // TRANSFER
    public Transaction(long operationId, BigDecimal amount, LocalDateTime date, long mainAccountID, long transferToAccountId, Result result) {
        this.operationId = operationId;
        this.amount = amount;
        this.date = date;
        this.transactionType = TransactionType.TRANSFER;
        this.mainAccountID = mainAccountID;
        this.transferToAccountID = 0;
        this.creditAmount = 0;
        this.result = result;
    }

    // CREDIT
    public Transaction(long operationId, LocalDateTime date, long mainAccountID, double creditAmount, Result result) {
        this.operationId = operationId;
        this.amount = null;
        this.date = date;
        this.transactionType = TransactionType.CREDIT;
        this.mainAccountID = mainAccountID;
        this.transferToAccountID = 0;
        this.creditAmount = creditAmount;
        this.result = result;
    }

    // GETTERS / SETTERS
    public long getOperationId() {
        return operationId;
    }

    public void setOperationId(long operationId) {
        this.operationId = operationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public long getMainAccount() {
        return mainAccountID;
    }

    public void setMainAccount(long mainAccount) {
        this.mainAccountID = mainAccount;
    }

    public long getTransferToAccount() {
        return transferToAccountID;
    }

    public void setTransferToAccount(long transferToAccount) {
        this.transferToAccountID = transferToAccount;
    }

    public double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
