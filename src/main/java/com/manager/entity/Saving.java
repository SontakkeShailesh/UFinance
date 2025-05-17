package com.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity(name = "Saving")
public class Saving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long income;

    private String incomeType;

    private Long totalIncome;

    private Long remainingIncome;

    private Long totalSaving;

    @JsonIgnore
    @OneToMany(mappedBy = "saving", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactionList;
}
