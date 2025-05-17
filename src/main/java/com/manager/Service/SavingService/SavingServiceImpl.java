package com.manager.Service.SavingService;

import com.manager.DTO.SavingRequestDto;
import com.manager.DTO.SavingResponseDto;
import com.manager.Exceptions.ResouceNotFoundException;
import com.manager.Repository.SavingRepo;
import com.manager.Repository.TransactionRepo;
import com.manager.entity.Saving;
import com.manager.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class SavingServiceImpl {

    private SavingRepo savingRepo;
    private TransactionRepo transactionRepo;

    @Autowired
    public SavingServiceImpl(SavingRepo savingRepo, TransactionRepo transactionRepo) {
        this.savingRepo = savingRepo;
        this.transactionRepo = transactionRepo;
    }

    public SavingResponseDto addIncome(SavingRequestDto request)
    {
        Saving lastSaving = savingRepo.findTopByOrderByIdDesc();

        Long totalIncome = request.getIncome();
        Long remainingIncome = request.getIncome();
        Long totalSaving = request.getIncome();

        if(lastSaving != null)
        {
            totalIncome += lastSaving.getTotalIncome();
            remainingIncome += lastSaving.getRemainingIncome();
            totalSaving += lastSaving.getTotalSaving();
        }

        //Saving table
        Saving saving = new Saving();
        saving.setDate(request.getDate());
        saving.setIncome(request.getIncome());
        saving.setIncomeType(request.getIncomeType());
        saving.setTotalIncome(totalIncome);
        saving.setRemainingIncome(remainingIncome);
        saving.setTotalSaving(totalSaving);

        Saving saved = savingRepo.save(saving);

        //Transaction table
        Transaction creditTransaction = new Transaction();
        creditTransaction.setSaving(saved);
        creditTransaction.setDate(request.getDate());
        creditTransaction.setAmount(request.getIncome());
        creditTransaction.setType("CREDITED");
        creditTransaction.setCategory("");
        creditTransaction.setDescription("");
        creditTransaction.setRemainingIncome(remainingIncome);

        transactionRepo.save(creditTransaction);

        //returning required response
        SavingResponseDto response = new SavingResponseDto();
        response.setId(saved.getId());
        response.setDate(saved.getDate());
        response.setIncome(saved.getIncome());
        response.setIncomeType(saved.getIncomeType());
        response.setTotalIncome(saved.getTotalIncome());
        response.setRemainingIncome(saved.getRemainingIncome());
        response.setTotalSaving(saved.getTotalSaving());

        return response;
    }

    public List<Saving> incomeHistory()
    {
        List<Saving> response = savingRepo.findAll();
        return response;
    }

    public Saving searchById(Long id)
    {
        Saving response = savingRepo.findById(id).orElse(null);
        if(response == null)
        {
            throw new ResouceNotFoundException("No data with id "+ id);
        }
        return response;
    }

    public Saving searchByDate(LocalDate date) {
        Saving response = savingRepo.findByDate(date).orElse(null);
        if(response == null)
        {
            throw new ResouceNotFoundException("No data with date "+ date);
        }
        return response;
    }

    public List<Saving> searchByType(String type) {
        List<Saving> response = savingRepo.findByincomeType(type).orElse(null);
        if(response == null)
        {
            throw new ResouceNotFoundException("No data with income Type "+ type);
        }
        return response;
    }

    public List<Saving> searchByAmount(Long amount) {
        List<Saving> response = savingRepo.findByIncome(amount).orElse(null);
        if(response == null)
        {
            throw new ResouceNotFoundException("No data with amount "+ amount );
        }
        return response;
    }


    public SavingResponseDto updateSaving(Long id, SavingRequestDto request)
        {
            Saving saving = savingRepo.findById(id).orElse(null);
            if(saving == null)
            {
                throw new ResouceNotFoundException("Given saving id " + request.getId() + " is not present");
            }

            if(request.getDate() != null)
            {
                saving.setDate(request.getDate());
            }

            List<Saving> futureRecordsUpdating = new ArrayList<>();
            Long keepOldIncome = saving.getIncome(); // for transaction table [it hold oldIncome] just like swap w no using three variable called temp

            if(request.getIncome() != null)
            {
                Long newIncome = request.getIncome();
                Long oldIncome = saving.getIncome();
                Long difference = newIncome - oldIncome; // +/- automatically get using grater value sign

                //Saving new changes in saving table
                saving.setIncome(newIncome);
                saving.setTotalIncome(saving.getTotalIncome() + difference);
                saving.setRemainingIncome(saving.getRemainingIncome() + difference);
                saving.setTotalSaving(saving.getRemainingIncome());

                //updating all records coming after updated record because [RemainingIncome, TotalSaving, TotalIncome] this fields get affected
                futureRecordsUpdating = savingRepo.findByIdGreaterThanOrderById(id);

                for(Saving future: futureRecordsUpdating)
                {
                    future.setRemainingIncome(future.getRemainingIncome() + difference);
                    future.setTotalSaving(future.getTotalSaving() + difference);
                    future.setTotalIncome(future.getTotalIncome() + difference);
                }
            }

            if(request.getIncomeType() != null)
            {
                saving.setIncomeType(request.getIncomeType());
            }

            Saving updated = savingRepo.save(saving); // saving current record
            savingRepo.saveAll(futureRecordsUpdating); // saving all record coming after that record

            //updating expense table from given id to all onwards
            // 1. Get the CREDITED transaction using this saving ID
            Transaction creditedTxn = transactionRepo.findFirstBySavingIdAndTypeOrderByIdAsc(id, "CREDITED");

            if (creditedTxn != null && request.getIncome() != null) {
                Long newIncome = request.getIncome();
                Long difference = newIncome - keepOldIncome;

                // Update the credited transaction's amount
                creditedTxn.setAmount(newIncome);

                // Get all transactions having ID >= this one // getting id of transaction table from saving_id
                List<Transaction> futureTransactions = transactionRepo.findByIdGreaterThanEqualOrderByIdAsc(creditedTxn.getId());

                for (Transaction txn : futureTransactions) {
                    txn.setRemainingIncome(txn.getRemainingIncome() + difference);
                }

                transactionRepo.save(creditedTxn);
                transactionRepo.saveAll(futureTransactions);
            }

        //sending response after updating
        SavingResponseDto response = new SavingResponseDto();
        response.setId(updated.getId());
        response.setDate(updated.getDate());
        response.setIncome(updated.getIncome());
        response.setIncomeType(updated.getIncomeType());
        response.setTotalIncome(updated.getTotalIncome());
        response.setRemainingIncome(updated.getRemainingIncome());
        response.setTotalSaving(updated.getTotalSaving());

        return response;
    }

    public String deleteById(Long id) {

       Saving saving = savingRepo.findById(id).orElse(null);
       if(saving == null)
       {
           throw new ResouceNotFoundException("Record with id " + id + "is not found ");
       }
       Long subIncome = saving.getIncome(); //income to subtract from all below records

        List<Saving> savingList = savingRepo.findByIdGreaterThanOrderById(id);
        for(Saving update: savingList)
        {
            update.setRemainingIncome(update.getRemainingIncome() - subIncome);
            update.setTotalIncome(update.getTotalIncome() - subIncome);
            update.setTotalSaving(update.getTotalSaving()- subIncome);
        }

        savingRepo.deleteById(id);
        savingRepo.saveAll(savingList);

        //Reflecting changes in expense table by changing remainingIncome for all onwards records

        Transaction allTransactions = transactionRepo.findFirstBySavingIdAndTypeOrderByIdAsc(id, "CREDITED");

        if ( allTransactions != null) {
            Long startingTransactionId = allTransactions.getId();

            // getting id of transaction table from saving_id
            List<Transaction> expenseList = transactionRepo.findByIdGreaterThanEqualOrderByIdAsc(id);

            for (Transaction expense : expenseList) {
                expense.setRemainingIncome(expense.getRemainingIncome() - subIncome);
            }
            transactionRepo.saveAll(expenseList);
        }else{
            System.out.println("No CREDITED transaction found for saving_id:" + id);
        }

        return "Record with id " + id + " deleted successfully";
    }
}


