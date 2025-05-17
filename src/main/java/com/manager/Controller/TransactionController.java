package com.manager.Controller;

import com.manager.DTO.TransactionRequestDto;
import com.manager.DTO.TransactionResponseDto;
import com.manager.Service.TransactionService.TransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
