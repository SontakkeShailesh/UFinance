package com.manager.Service.SavingService;

import com.manager.DTO.SavingRequestDto;
import com.manager.DTO.SavingResponseDto;
import com.manager.entity.Saving;

import java.time.LocalDate;
import java.util.List;

public interface SavingService {

    SavingResponseDto addIncome(SavingRequestDto request);

    List<Saving> incomeHistory();

    Saving searchById(Long id);

    List<Saving> searchByDate(LocalDate date);

    List<Saving> searchByType(String type);

    List<Saving> searchByAmount(Long amount);

    SavingResponseDto updateSaving(Long id, SavingRequestDto request);

    String deleteById(Long id);






}
