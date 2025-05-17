package com.manager.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequestDto {

    @NotNull(message = "Data is required")
    private LocalDate date;

    @NotNull(message = "Amount is required")
    private Long amount;

    private String type;

    private String category;

    private String description;
}
