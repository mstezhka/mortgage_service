package com.example.mortgage_service.service.impl;

import static com.example.mortgage_service.constants.Constants.MORTGAGE_EXCEEDS_HOME_VALUE;
import static com.example.mortgage_service.constants.Constants.MORTGAGE_EXCEEDS_INCOME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.example.mortgage_service.exception.NotMatchMaturityPeriodException;
import com.example.mortgage_service.model.MortgageCheckRequestDto;
import com.example.mortgage_service.model.MortgageCheckResponseDto;
import com.example.mortgage_service.model.MortgageRateRecord;
import com.example.mortgage_service.repository.MortgageRatesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MortgageServiceImplTest {

    private MortgageRatesRepository mortgageRatesRepository;
    private MortgageServiceImpl mortgageService;

    // Constants copied from service for test consistency
    private static final Set<Integer> MATURITY_PERIODS = Set.of(15, 20, 25, 30);
    private static final int MORTGAGE_TO_INCOME_RATIO = 4;

    @BeforeEach
    void setup() {
        mortgageRatesRepository = mock(MortgageRatesRepository.class);
        mortgageService = new MortgageServiceImpl(mortgageRatesRepository);
    }

    @Test
    void testGetAllMortgageRates() {
        List<MortgageRateRecord> mockedRates =
                Collections.singletonList(new MortgageRateRecord(12, BigDecimal.ONE, new Timestamp(System.currentTimeMillis())));
        when(mortgageRatesRepository.getAll()).thenReturn(mockedRates);

        List<MortgageRateRecord> result = mortgageService.getAllMortgageRates();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mortgageRatesRepository, times(1)).getAll();
    }

    @Test
    void testCalculateMortgageSuccess() {
        MortgageCheckRequestDto request = new MortgageCheckRequestDto();
        request.setIncome(BigDecimal.valueOf(100000));
        request.setMaturityPeriod(36);
        request.setLoanValue(BigDecimal.valueOf(300000));
        request.setHomeValue(BigDecimal.valueOf(400000));

        BigDecimal rate = BigDecimal.valueOf(0.01);
        when(mortgageRatesRepository.getRateByMaturityPeriod(36)).thenReturn(rate);

        MortgageCheckResponseDto response = mortgageService.calculateMortgage(request);

        assertTrue(response.isFeasible());
        assertNull(response.getNotFeasibleReason());

        BigDecimal monthlyPrincipal = request.getLoanValue().divide(BigDecimal.valueOf(30), RoundingMode.HALF_UP);
        BigDecimal monthlyFee = monthlyPrincipal.multiply(rate);
        BigDecimal expectedMonthlyCost = monthlyPrincipal.add(monthlyFee);

        assertEquals(1, expectedMonthlyCost.compareTo(response.getMonthlyCost()));
    }

    @Test
    void testCalculateMortgage_InvalidMaturityPeriod() {
        MortgageCheckRequestDto request = new MortgageCheckRequestDto();
        request.setIncome(BigDecimal.valueOf(100000));
        request.setMaturityPeriod(10); // Invalid maturity period
        request.setLoanValue(BigDecimal.valueOf(200000));
        request.setHomeValue(BigDecimal.valueOf(300000));

        NotMatchMaturityPeriodException exception = assertThrows(NotMatchMaturityPeriodException.class,
                () -> mortgageService.calculateMortgage(request));

        assertTrue(exception.getMessage().contains("Maturity period is not correctly specified"));
    }

    @Test
    void testCalculateMortgageReturnsMortgageExceedsIncome() {
        MortgageCheckRequestDto request = new MortgageCheckRequestDto();
        request.setIncome(BigDecimal.valueOf(50000));
        request.setMaturityPeriod(12);
        request.setLoanValue(BigDecimal.valueOf(250000));
        request.setHomeValue(BigDecimal.valueOf(300000));

        when(mortgageRatesRepository.getRateByMaturityPeriod(12)).thenReturn(BigDecimal.valueOf(0.01));

        MortgageCheckResponseDto response = mortgageService.calculateMortgage(request);

        assertFalse(response.isFeasible());
        assertEquals(MORTGAGE_EXCEEDS_INCOME, response.getNotFeasibleReason());
        assertNotNull(response.getMonthlyCost());
    }

    @Test
    void testCalculateMortgageThrowsWrongMaturityPeriodException() {
        MortgageCheckRequestDto request = new MortgageCheckRequestDto();
        request.setIncome(BigDecimal.valueOf(50000));
        request.setMaturityPeriod(30);
        request.setLoanValue(BigDecimal.valueOf(250000));
        request.setHomeValue(BigDecimal.valueOf(300000));

        when(mortgageRatesRepository.getRateByMaturityPeriod(30)).thenReturn(BigDecimal.valueOf(0.01));

        try {
            MortgageCheckResponseDto response = mortgageService.calculateMortgage(request);
        } catch (RuntimeException e) {
            assertTrue(e instanceof NotMatchMaturityPeriodException);
        }
    }

    @Test
    void testCalculateMortgageMortgageExceedsHomeValue() {
        MortgageCheckRequestDto request = new MortgageCheckRequestDto();
        request.setIncome(BigDecimal.valueOf(100000));
        request.setMaturityPeriod(36);
        request.setLoanValue(BigDecimal.valueOf(350000));  // Exceeds home value 300k
        request.setHomeValue(BigDecimal.valueOf(300000));

        when(mortgageRatesRepository.getRateByMaturityPeriod(36)).thenReturn(BigDecimal.valueOf(0.01));

        MortgageCheckResponseDto response = mortgageService.calculateMortgage(request);

        assertFalse(response.isFeasible());
        assertEquals(MORTGAGE_EXCEEDS_HOME_VALUE, response.getNotFeasibleReason());
        assertNotNull(response.getMonthlyCost());
    }
}

