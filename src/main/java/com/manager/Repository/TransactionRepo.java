package com.manager.Repository;

import com.manager.entity.Saving;
import com.manager.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    Transaction findTopByTypeOrderByIdDesc(String type);

    Transaction findFirstBySavingIdAndTypeOrderByIdAsc(Long id, String credited);

    List<Transaction> findByIdGreaterThanEqualOrderByIdAsc(Long expenseId);

    Optional<List<Transaction>> findByType(String type);

    Optional<List<Transaction>> findByTypeAndCategory(String type, String category);

    Optional<List<Transaction>> findByTypeAndDate(String type, LocalDate date);

    Optional<List<Transaction>> findByTypeAndAmount(String type, Long amount);

    List<Transaction> findByIdGreaterThan(Long id);

    Optional<Transaction> findFirstByIdGreaterThanAndTypeOrderByIdAsc(Long id, String type);

    Optional<Transaction> findFirstByIdLessThanAndTypeOrderByIdDesc(Long id, String type);

    List<Transaction> findByIdGreaterThanOrderByIdAsc(Long id);



}
