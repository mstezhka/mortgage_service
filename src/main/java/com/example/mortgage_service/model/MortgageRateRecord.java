package com.example.mortgage_service.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record MortgageRateRecord (Integer maturityPeriod, BigDecimal interestRate, Timestamp lastUpdate) {}
