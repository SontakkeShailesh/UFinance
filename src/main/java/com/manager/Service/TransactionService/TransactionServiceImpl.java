package com.manager.Service.TransactionService;
import com.manager.DTO.TransactionRequestDto;
import com.manager.DTO.TransactionResponseDto;
import com.manager.Exceptions.ResouceNotFoundException;
import com.manager.Repository.SavingRepo;
import com.manager.Repository.TransactionRepo;
import com.manager.entity.Saving;
import com.manager.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl {

    private TransactionRepo transactionRepo;
    private SavingRepo savingRepo;

    @Autowired
    public TransactionServiceImpl(TransactionRepo transactionRepo,  SavingRepo savingRepo) //all done
    {
        this.transactionRepo = transactionRepo;
        this.savingRepo = savingRepo;
    }

    public TransactionResponseDto debitTransaction(TransactionRequestDto request)
    {
        Transaction lastTransaction = transactionRepo.findTopByTypeOrderByIdDesc("DEBIT"); // normal find method will also work since now credit records also holds totalExpense

        Long totalExpense = 0L;
        if (lastTransaction != null) {
            totalExpense = lastTransaction.getTotalExpense();
        }
        // Add current DEBIT amount to totalExpense
        totalExpense += request.getAmount();

        Saving saving = savingRepo.findTopByOrderByIdDesc();
        Long remainingBalance = saving.getRemainingIncome() - request.getAmount();

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
        response.setDescription(debit.getDescription());

        return response;

    }

    public List<Transaction> findAllExpense()
    {
        return transactionRepo.findAll();
    }

    public Transaction getById(Long id)
    {
        Transaction record = transactionRepo.findById(id).orElse(null);
        if(record == null) throw new ResouceNotFoundException("No data with id" + id);
        return record;
    }

    public List<Transaction> getByType(String type)
    {
        List<Transaction> records = transactionRepo.findByType(type).orElse(null);
        if(records == null) throw new ResouceNotFoundException("No data with type" + type);
        return records;
    }

    public List<Transaction> getByCategory(String category)
    {
        List<Transaction> records = transactionRepo.findByCategory(category).orElse(null);
        if(records == null) throw new ResouceNotFoundException("No data with category" + category);
        return records;
    }

    public List<Transaction> getByDate(LocalDate date) {
        List<Transaction> records = transactionRepo.findByDate(date).orElse(null);
        if(records == null) throw new ResouceNotFoundException("No data with date" + date);
        return records;
    }

    public List<Transaction> getByAmount(Long amount) {
        List<Transaction> records = transactionRepo.findByAmount(amount).orElse(null);
        if(records == null) throw new ResouceNotFoundException("No data with amount" + amount);
        return records;
    }

    @Transactional //totalExpense after updating first expense records store old value
    public TransactionResponseDto updateExpense(Long id, TransactionRequestDto request)
    {
        Transaction expense = transactionRepo.findById(id).orElse(null);
        if(expense == null){
            throw new ResouceNotFoundException("No data with id "+ id);
        }

        if (request.getDate() != null) {
            expense.setDate(request.getDate());
        }
        if (request.getType() != null) {
            expense.setType(request.getType());
        }
        if (request.getCategory() != null) {
            expense.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            expense.setDescription(request.getDescription());
        }

        Long difference = 0L;
        Long keepOldExpense = expense.getAmount(); // for using later in saving table

        if (request.getAmount() != null) {

            Long oldAmount = expense.getAmount();
            Long newAmount = request.getAmount();
            difference = newAmount - oldAmount;

            expense.setAmount(newAmount);

            expense.setRemainingIncome(expense.getRemainingIncome() - difference);

            transactionRepo.save(expense);

            List<Transaction> futureTransactions = transactionRepo.findByIdGreaterThan(id);
            for(Transaction txn: futureTransactions)
            {
                txn.setRemainingIncome(txn.getRemainingIncome() - difference);
                txn.setTotalExpense(txn.getTotalExpense() + difference);
            }
        }

        // older version of solution to fetch and update remainingIncome and TotalSaving  in saving table
//        Transaction latestTxn = transactionRepo.findTopByOrderByIdDesc();
//        if (latestTxn != null) {
//            Long latestRemainingIncome = latestTxn.getRemainingIncome();
//
//            // Step: Fetch latest saving entry
//            Saving latestSaving = savingRepo.findTopByOrderByIdDesc();
//            if (latestSaving != null) {
//                latestSaving.setRemainingIncome(latestRemainingIncome);
//                latestSaving.setTotalSaving(latestRemainingIncome); // Assuming totalSaving should match
//                savingRepo.save(latestSaving);
//            }
//        }

//  fetch below credit transaction and get savingId from that ans update all records coming below that id, if not then fetch least recent [above]

        //check this
        //problem1: total_income and total_saving is wrong in saving
        //prb 2: totalExpense is wrong for first updated record
        Long updateTxnId = id;
        Transaction creditTxn = transactionRepo
                .findFirstByIdGreaterThanAndTypeOrderByIdAsc(updateTxnId, "CREDITED")
                .orElseGet(() -> transactionRepo
                        .findFirstByIdLessThanAndTypeOrderByIdDesc(updateTxnId, "CREDITED")
                        .orElse(null));

         if (creditTxn != null && creditTxn.getSaving() != null) {
            Long savingId = creditTxn.getSaving().getId();

             if (savingId != null) {
                 Long diff = request.getAmount() - keepOldExpense;

                 // Include the current savingId also (not just greater than)
                 List<Saving> savings = savingRepo.findByIdGreaterThanEqualOrderById(savingId);
                 for (Saving s : savings) {
                     s.setRemainingIncome(s.getRemainingIncome() - diff);
                     s.setTotalSaving(s.getTotalSaving() - diff);
                 }
                 savingRepo.saveAll(savings);
             }
         }

        //Response
        TransactionResponseDto response = new TransactionResponseDto();
        response.setId(expense.getId());
        response.setDate(expense.getDate());
        response.setAmount(expense.getAmount());
        response.setType(expense.getType());
        response.setTotalExpense(expense.getTotalExpense());
        response.setRemainingIncome(expense.getRemainingIncome());
        response.setDescription(expense.getDescription());

        return response;
    }

    @Transactional
    public String deleteExpense(Long id)
    {
        Transaction txn = transactionRepo.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("No data with id " + id));

        if (!"DEBIT".equalsIgnoreCase(txn.getType())) {
            transactionRepo.deleteById(id);
            return "Non-expense record deleted.";
        }

        Long amount = txn.getAmount();
        Long deletedTxnId = txn.getId();

        Transaction creditTxn = transactionRepo
                .findFirstByIdGreaterThanAndTypeOrderByIdAsc(deletedTxnId, "CREDITED")
                .orElseGet(() -> transactionRepo
                        .findFirstByIdLessThanAndTypeOrderByIdDesc(deletedTxnId, "CREDITED")
                        .orElse(null));

        //here only that saving is updated check for future updation
        if (creditTxn != null && creditTxn.getSaving() != null) {
            Long savingId = creditTxn.getSaving().getId();
            List<Saving> saving = savingRepo.findByIdGreaterThanEqualOrderByIdAsc(savingId);
            for (Saving s : saving) {
                s.setRemainingIncome(s.getRemainingIncome() + amount);
                s.setTotalSaving(s.getTotalSaving() + amount);
            }
            savingRepo.saveAll(saving);


            List<Transaction> futureTxns = transactionRepo.findByIdGreaterThanOrderByIdAsc(deletedTxnId);
            for (Transaction t : futureTxns) {
                t.setRemainingIncome(t.getRemainingIncome() + amount);
                t.setTotalExpense(t.getTotalExpense() - amount);
            }
            transactionRepo.saveAll(futureTxns);

            transactionRepo.deleteById(deletedTxnId);

        }
        return "Expense record with id " + deletedTxnId + " deleted and savings updated.";
    }
}
