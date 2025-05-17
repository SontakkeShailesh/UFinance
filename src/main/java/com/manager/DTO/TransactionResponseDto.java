package com.manager.DTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionResponseDto {

    private Long id;

    private LocalDate date;

    private Long amount;

    private String type;

    private String category;

    private Long totalExpense;

    private Long remainingIncome;

    private String description;

}
