package com.manager.DTO;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
public class SavingRequestDto {

    private Long id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Income is required")
    private Long income;

    private String incomeType;
}
