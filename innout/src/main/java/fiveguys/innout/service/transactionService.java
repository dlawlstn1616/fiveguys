package fiveguys.innout.service;

import fiveguys.innout.entity.Transaction;

import java.util.List;

public interface transactionService {
    Transaction createTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(int transactionId);

    Transaction updateTransaction(int transactionId, Transaction updatedTransaction);

    void deleteTransaction(int transactionId);
}
