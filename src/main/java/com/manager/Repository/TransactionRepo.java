package com.manager.Repository;

import com.manager.entity.Saving;
import com.manager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTopByTypeOrderByIdDesc(String type);

    Transaction findFirstBySavingIdAndTypeOrderByIdAsc(Long id, String credited);

    List<Transaction> findByIdGreaterThanEqualOrderByIdAsc(Long expenseId);
}
