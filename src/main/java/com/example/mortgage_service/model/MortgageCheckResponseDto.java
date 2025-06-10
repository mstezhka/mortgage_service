package com.example.mortgage_service.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MortgageCheckResponseDto {
    private boolean isFeasible;
    private BigDecimal monthlyCost;
    private String notFeasibleReason;
}
