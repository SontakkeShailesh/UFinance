package com.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import java.time.LocalDate;

@Data
@Entity(name = "Expense")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long amount;

    private String type;
    public void setType(String type) {
        if (type != null) {
            this.type = type.toUpperCase(); // ensures value like "debit" -> "DEBIT".
        } else {
            this.type = null;
        }
    }


    private String category;

    private Long totalExpense;

    private Long remainingIncome;

    private String description;

    @ManyToOne
    @JoinColumn(name = "saving_id")
    @JsonIgnore
    private Saving saving;

}
