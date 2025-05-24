package com.manager.Service.TransactionService;

import com.manager.DTO.TransactionRequestDto;
import com.manager.DTO.TransactionResponseDto;
import com.manager.entity.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionResponseDto debitTransaction(TransactionRequestDto request);

    List<Transaction> findAllExpense();

    Transaction getById(Long id);

    List<Transaction> getByType(String type);

    List<Transaction> getByCategory(String category);

    List<Transaction> getByDate(LocalDate date);

    List<Transaction> getByAmount(Long amount);

    TransactionResponseDto updateExpense(Long id, TransactionRequestDto request);

    String deleteExpense(Long id);

}
