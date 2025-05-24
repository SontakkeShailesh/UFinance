package com.manager.Controller;

import com.manager.DTO.SavingRequestDto;
import com.manager.DTO.SavingResponseDto;
import com.manager.Exceptions.ResouceNotFoundException;
import com.manager.Service.SavingService.SavingServiceImpl;
import com.manager.entity.Saving;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/credit")
public class SavingController {

    private SavingServiceImpl savingService;

    @Autowired
    public SavingController(SavingServiceImpl savingService) {
        this.savingService = savingService;
    }

    @PostMapping("/addIncome")
   public ResponseEntity<SavingResponseDto> addIncome(@RequestBody @Valid SavingRequestDto requestDto)
    {
        SavingResponseDto responseDto = savingService.addIncome(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/incomeHistory")
    public ResponseEntity<List<Saving>> incomeHistory()
    {
        List<Saving> response = savingService.incomeHistory();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/income/id/{id}")
    public ResponseEntity<Saving>  singleResult(@PathVariable Long id)
    {
        Saving saving = savingService.searchById(id);
        return new ResponseEntity<>(saving, HttpStatus.OK);
    }

    @GetMapping("/income/date/{date}")
    public ResponseEntity<List<Saving>>  dateResult(@PathVariable LocalDate date)
    {
        List<Saving> saving = savingService.searchByDate(date);
        return new ResponseEntity<>(saving, HttpStatus.OK);
    }

    @GetMapping("/incomeType/{incomeType}")
    public ResponseEntity<List<Saving>>  typeResult(@PathVariable String incomeType)
    {
       List<Saving> saving = savingService.searchByType(incomeType);
        return new ResponseEntity<>(saving, HttpStatus.OK);
    }

    @GetMapping("/amount/{amount}")
    public ResponseEntity<List<Saving>> amountResult(@PathVariable Long amount)
    {
        List<Saving> saving = savingService.searchByAmount(amount);
        return new ResponseEntity<>(saving, HttpStatus.OK);
    }

    @PutMapping("/update/transaction/id/{id}")
    public ResponseEntity<SavingResponseDto> updateSaving(@PathVariable Long id, @RequestBody SavingRequestDto requestDto)
    {
        SavingResponseDto saving = savingService.updateSaving(id, requestDto);
        return new ResponseEntity<>(saving, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRecord(@PathVariable Long id)
    {
        String message = savingService.deleteById(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
