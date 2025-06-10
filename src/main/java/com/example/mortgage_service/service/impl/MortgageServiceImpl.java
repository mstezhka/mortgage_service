package com.example.mortgage_service.service.impl;

import com.example.mortgage_service.exception.NotMatchMaturityPeriodException;
import com.example.mortgage_service.model.MortgageCheckRequestDto;
import com.example.mortgage_service.model.MortgageCheckResponseDto;
import com.example.mortgage_service.model.MortgageRateRecord;
import com.example.mortgage_service.repository.MortgageRatesRepository;
import com.example.mortgage_service.service.MortgageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.example.mortgage_service.constants.Constants.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class MortgageServiceImpl implements MortgageService {
    private final MortgageRatesRepository mortgageRatesRepository;

    @Override
    public List<MortgageRateRecord> getAllMortgageRates() {
        return mortgageRatesRepository.getAll();
    }

    @Override
    public MortgageCheckResponseDto calculateMortgage(MortgageCheckRequestDto mortgageCheckRequestDto) {
        MortgageCheckResponseDto.MortgageCheckResponseDtoBuilder mortgageCheckResponseDto = MortgageCheckResponseDto.builder();
        mortgageCheckResponseDto.isFeasible(true);

        // check if maturity period correctly requested
        if (!MATURITY_PERIODS.contains(mortgageCheckRequestDto.getMaturityPeriod()) || mortgageCheckRequestDto.getMaturityPeriod() == null) {
            throw new NotMatchMaturityPeriodException("Maturity period is not correctly specified. Should be one of: " + MATURITY_PERIODS);
        }

        // check if mortgage is not exceed 4 times the income value
        if (mortgageCheckRequestDto.getIncome().multiply(BigDecimal.valueOf(MORTGAGE_TO_INCOME_RATIO))
                .compareTo(mortgageCheckRequestDto.getLoanValue()) < 0) {
            mortgageCheckResponseDto.isFeasible(false);
            mortgageCheckResponseDto.notFeasibleReason(MORTGAGE_EXCEEDS_INCOME);
            log.warn("The mortgage exceeds 4 times the income value");
        }

        //check if mortgage is not exceed the home value
        if (mortgageCheckRequestDto.getLoanValue().compareTo(mortgageCheckRequestDto.getHomeValue()) > 0) {
            mortgageCheckResponseDto.isFeasible(false);
            mortgageCheckResponseDto.notFeasibleReason(MORTGAGE_EXCEEDS_HOME_VALUE);
            log.warn("The mortgage exceeds the home value");
        }

        BigDecimal monthlyMortgageRate = mortgageRatesRepository.getRateByMaturityPeriod(mortgageCheckRequestDto.getMaturityPeriod());
        BigDecimal mortgageMonthlyValue = mortgageCheckRequestDto.getLoanValue()
                .divide(BigDecimal.valueOf(mortgageCheckRequestDto.getMaturityPeriod()), RoundingMode.HALF_UP);
        BigDecimal mortgageMonthlyFee = mortgageCheckRequestDto.getLoanValue()
                .divide(BigDecimal.valueOf(mortgageCheckRequestDto.getMaturityPeriod()), RoundingMode.HALF_UP)
                .multiply(monthlyMortgageRate);
        BigDecimal mortgageMonthlyCost = mortgageMonthlyValue.add(mortgageMonthlyFee);

        mortgageCheckResponseDto.monthlyCost(mortgageMonthlyCost);

        return mortgageCheckResponseDto.build();
    }
}
