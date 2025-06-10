package com.example.mortgage_service.repository;

import com.example.mortgage_service.model.MortgageRateRecord;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MortgageRatesRepository {
    private final List<MortgageRateRecord> mortgageRates = new ArrayList<>();
    private final Map<Integer, BigDecimal> periodToMortgageRateMap = new HashMap<>();

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/source_data/mortgage_rates.csv")))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) continue; //ignore header

                String[] parts = line.split(";");
                Integer period = Integer.parseInt(parts[0].trim());
                BigDecimal rate = new BigDecimal(parts[1].trim());
                Timestamp lastUpdate = Timestamp.valueOf(parts[2].trim());
                mortgageRates.add(new MortgageRateRecord(period, rate, lastUpdate));
                periodToMortgageRateMap.put(period, rate);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load maturity rates", e);
        }
    }

    public List<MortgageRateRecord> getAll() {
        return mortgageRates;
    }

    public BigDecimal getRateByMaturityPeriod(Integer maturityPeriod) {
        return periodToMortgageRateMap.get(maturityPeriod);
    }
}
