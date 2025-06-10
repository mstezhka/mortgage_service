package com.example.mortgage_service.model;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class MortgageRateDto {
    private Integer maturityPeriod;
    private BigDecimal interestRate;
    private Timestamp lastUpdate;
}
