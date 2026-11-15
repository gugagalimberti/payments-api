package com.galimberti.paymentsapi.repositories;

import java.util.List;

import com.galimberti.paymentsapi.domain.Transaction;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;

public interface TransactionRepository {

    List<Transaction> findAll(Integer userId, Integer categoryId);

    Transaction findById(Integer userId, Integer categoryId, Integer transactionId) throws PaResourceNotFoundException;

    Integer create(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate) throws PaBadRequestException;

    void update(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction) throws PaBadRequestException, PaResourceNotFoundException;

    void removeById(Integer userId, Integer categoryId, Integer transactionId) throws PaResourceNotFoundException;
}
