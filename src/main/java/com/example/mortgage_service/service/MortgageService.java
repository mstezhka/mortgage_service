package com.example.mortgage_service.service;

import com.example.mortgage_service.model.MortgageCheckRequestDto;
import com.example.mortgage_service.model.MortgageCheckResponseDto;
import com.example.mortgage_service.model.MortgageRateRecord;

import java.util.List;

public interface MortgageService {

    List<MortgageRateRecord> getAllMortgageRates();

    MortgageCheckResponseDto calculateMortgage(MortgageCheckRequestDto mortgageCheckRequestDto);
}
