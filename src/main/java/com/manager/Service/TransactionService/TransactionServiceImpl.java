package com.manager.Service.TransactionService;

import com.manager.DTO.TransactionRequestDto;
import com.manager.DTO.TransactionResponseDto;
import com.manager.Repository.SavingRepo;
import com.manager.Repository.TransactionRepo;
import com.manager.entity.Saving;
import com.manager.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl {

    private TransactionRepo transactionRepo;
    private SavingRepo savingRepo;

    @Autowired
    public TransactionServiceImpl(TransactionRepo transactionRepo,  SavingRepo savingRepo)
    {
        this.transactionRepo = transactionRepo;
        this.savingRepo = savingRepo;
    }

    public TransactionResponseDto debitTransaction(TransactionRequestDto request)
    {
        Transaction lastTransaction = transactionRepo.findTopByTypeOrderByIdDesc("DEBIT").orElse(null);
        Saving saving = savingRepo.findTopByOrderByIdDesc();

        Long totalExpense = request.getAmount();
        Long remainingBalance = saving.getRemainingIncome() - request.getAmount(); // remainingBalance is reducing


        if(lastTransaction != null) {
           totalExpense += lastTransaction.getAmount(); //expense is adding
        }else{
            totalExpense = request.getAmount();
        }

        saving.setRemainingIncome(remainingBalance);
        saving.setTotalSaving(remainingBalance);
        savingRepo.save(saving);


        Transaction addTransaction = new Transaction();
        addTransaction.setDate(request.getDate());
        addTransaction.setAmount(request.getAmount());
        addTransaction.setType(request.getType());
        addTransaction.setCategory(request.getCategory());
        addTransaction.setDescription(request.getDescription());
        addTransaction.setRemainingIncome(remainingBalance);
        addTransaction.setTotalExpense(totalExpense);

        Transaction debit = transactionRepo.save(addTransaction);

        TransactionResponseDto response = new TransactionResponseDto();

        response.setId(debit.getId());
        response.setDate(debit.getDate());
        response.setAmount(debit.getAmount());
        response.setType(debit.getType());
        response.setCategory(debit.getCategory());
        response.setDescription(debit.getDescription());
        response.setTotalExpense(totalExpense);
        response.setRemainingIncome(remainingBalance);
        response.setDescription(response.getDescription());

        return response;

        //if there is any credit transaction then if i make debit then total expense dont inscrease becuase last transaction dont had expense
        //sol1: keep adding expense as it is in although its credit
        // sol2: change repo method
    }


}
