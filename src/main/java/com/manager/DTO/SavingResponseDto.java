package com.manager.DTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SavingResponseDto {

    private Long id;

    private LocalDate date;

    private Long income;

    private String incomeType;

    private Long totalIncome;

    private Long remainingIncome;

    private Long totalSaving;
}
