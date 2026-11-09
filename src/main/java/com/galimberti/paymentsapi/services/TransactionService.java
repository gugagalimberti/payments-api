package com.galimberti.paymentsapi.services;

import java.util.List;

import com.galimberti.paymentsapi.domain.Transaction;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;

public interface TransactionService {

    List<Transaction> fetchAllTransactions(Integer userId, Integer categoryId);

    Transaction fetchTransactionById(Integer userId, Integer categoryId, Integer transactionId) throws PaResourceNotFoundException;

    Transaction addTransaction(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate);

    void updateTransaction(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction) throws PaBadRequestException;

    void removeTransaction(Integer userId, Integer categoryId, Integer transactionId) throws PaResourceNotFoundException;
}
