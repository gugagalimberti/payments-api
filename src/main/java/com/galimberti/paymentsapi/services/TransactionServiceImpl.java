package com.galimberti.paymentsapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.galimberti.paymentsapi.domain.Transaction;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;
import com.galimberti.paymentsapi.repositories.CategoryRepository;
import com.galimberti.paymentsapi.repositories.TransactionRepository;

@Service 
@Transactional 
public class TransactionServiceImpl implements TransactionService {

    @Autowired 
    TransactionRepository transactionRepository;

    @Autowired 
    CategoryRepository categoryRepository;

    @Override
    public Transaction addTransaction(Integer userId, Integer categoryId, Double amount, String note,
            Long transactionDate) {
        categoryRepository.findById(userId, categoryId);
        int transactionId = transactionRepository.create(userId, categoryId, amount, note, transactionDate);
        return transactionRepository.findById(userId, categoryId, transactionId);
    }

    @Override
    public List<Transaction> fetchAllTransactions(Integer userId, Integer categoryId) {
        return transactionRepository.findAll(userId, categoryId);
    }

    @Override
    public Transaction fetchTransactionById(Integer userId, Integer categoryId, Integer transactionId)
            throws PaResourceNotFoundException {
        return transactionRepository.findById(userId, categoryId, transactionId);
    }

    @Override
    public void removeTransaction(Integer userId, Integer categoryId, Integer transactionId)
            throws PaResourceNotFoundException {
        transactionRepository.removeById(userId, categoryId, transactionId);
    }

    @Override
    public void updateTransaction(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction)
            throws PaBadRequestException {
        transactionRepository.update(userId, categoryId, transactionId, transaction);
    }

}
