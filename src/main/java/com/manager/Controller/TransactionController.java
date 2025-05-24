package com.manager.Controller;

import com.manager.DTO.TransactionRequestDto;
import com.manager.DTO.TransactionResponseDto;
import com.manager.Service.TransactionService.TransactionServiceImpl;
import com.manager.entity.Transaction;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/debit")
public class TransactionController {

    private TransactionServiceImpl transactionService;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponseDto> debitTransaction(@RequestBody @Valid TransactionRequestDto request)
    {
        TransactionResponseDto response = transactionService.debitTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/expenseHistory")
    public ResponseEntity<List<Transaction>> getallExpense()
    {
        List<Transaction> list = transactionService.findAllExpense();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("expense/id/{id}")
    public ResponseEntity<Transaction> byid(@PathVariable Long id){
        Transaction record = transactionService.getById(id);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    @GetMapping("expense/type/{type}")
    public ResponseEntity<List<Transaction>> byType(@PathVariable String type)
    {
        List<Transaction> record = transactionService.getByType(type);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    @GetMapping("/expense/category/{category}")
    public ResponseEntity<List<Transaction>> byCategory(@PathVariable String category)
    {
        List<Transaction> record = transactionService.getByCategory(category);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    @GetMapping("/expense/date/{date}")
    public ResponseEntity<List<Transaction>> byDate(@PathVariable LocalDate  date)
    {
        List<Transaction> record = transactionService.getByDate(date);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    @GetMapping("/expense/amount/{amount}")
    public ResponseEntity<List<Transaction>> byDate(@PathVariable Long amount)
    {
        List<Transaction> record = transactionService.getByAmount(amount);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    @PutMapping("/expense/update/{id}")
    public ResponseEntity<TransactionResponseDto> updateExpense(@PathVariable Long id, @RequestBody TransactionRequestDto request)
    {
        TransactionResponseDto response = transactionService.updateExpense(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/expense/delete/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id)
    {
        String message = transactionService.deleteExpense(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


}
