package com.manager.Repository;

import com.manager.entity.Saving;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SavingRepo extends JpaRepository<Saving, Long> {

    Saving findTopByOrderByIdDesc();

    Optional<List<Saving>> findByDate(LocalDate date);

    Optional<List<Saving>> findByincomeType(String type);

    Optional<List<Saving>> findByIncome(Long amount);

    List<Saving> findByIdGreaterThanOrderById(Long id);

    List<Saving> findByIdGreaterThanEqualOrderByIdAsc(Long id);

    List<Saving> findByIdGreaterThanEqualOrderById(Long id);




}
