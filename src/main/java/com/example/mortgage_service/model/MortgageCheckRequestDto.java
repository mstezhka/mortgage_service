package com.example.mortgage_service.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MortgageCheckRequestDto {
    private BigDecimal income;
    private Integer maturityPeriod;
    private BigDecimal loanValue;
    private BigDecimal homeValue;
}
