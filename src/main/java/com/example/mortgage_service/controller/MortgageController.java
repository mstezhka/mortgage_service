package com.example.mortgage_service.controller;

import com.example.mortgage_service.model.MortgageCheckRequestDto;
import com.example.mortgage_service.model.MortgageCheckResponseDto;
import com.example.mortgage_service.model.MortgageRateRecord;
import com.example.mortgage_service.service.MortgageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MortgageController {
    private final MortgageService mortgageService;

    @GetMapping("/interest-rates")
    public ResponseEntity<List<MortgageRateRecord>> getCurrentMortgageRates() {
        return ResponseEntity.ok(mortgageService.getAllMortgageRates());
    }

    @PostMapping("/mortgage-check")
    public ResponseEntity<MortgageCheckResponseDto> calculateMortgage(@Valid @RequestBody MortgageCheckRequestDto request) {
        return ResponseEntity.ok(mortgageService.calculateMortgage(request));
    }
}
