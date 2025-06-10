package com.example.mortgage_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.mortgage_service.exception.GlobalExceptionHandler;
import com.example.mortgage_service.exception.NotMatchMaturityPeriodException;
import com.example.mortgage_service.model.MortgageCheckRequestDto;
import com.example.mortgage_service.model.MortgageCheckResponseDto;
import com.example.mortgage_service.model.MortgageRateRecord;
import com.example.mortgage_service.service.MortgageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class MortgageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MortgageService mortgageService;

    @InjectMocks
    private MortgageController mortgageController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() throws Exception {
        try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(mortgageController)
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .build();
        }
    }

    @Test
    public void testGetCurrentMortgageRates() throws Exception {
        MortgageRateRecord record = new MortgageRateRecord(36, BigDecimal.valueOf(3.5), new Timestamp(System.currentTimeMillis()));

        when(mortgageService.getAllMortgageRates()).thenReturn(List.of(record));

        mockMvc.perform(get("/api/interest-rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].interestRate").value(3.5))
                .andExpect(jsonPath("$[0].maturityPeriod").value(36));

        verify(mortgageService, times(1)).getAllMortgageRates();
    }

    @Test
    public void testMortgageCheckSuccess() throws Exception {
        MortgageCheckRequestDto requestDto = new MortgageCheckRequestDto();
        requestDto.setIncome(BigDecimal.valueOf(75000));
        requestDto.setMaturityPeriod(36);
        requestDto.setLoanValue(BigDecimal.valueOf(250000));
        requestDto.setHomeValue(BigDecimal.valueOf(300000));

        MortgageCheckResponseDto responseDto = MortgageCheckResponseDto.builder()
                .isFeasible(true)
                .monthlyCost(BigDecimal.valueOf(1200))
                .build();

        when(mortgageService.calculateMortgage(any(MortgageCheckRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyCost").value(1200));

        verify(mortgageService, times(1)).calculateMortgage(any(MortgageCheckRequestDto.class));
    }

    @Test
    public void testMortgageCheckMaturityPeriodError() throws Exception {
        MortgageCheckRequestDto requestDto = new MortgageCheckRequestDto();
        requestDto.setIncome(BigDecimal.valueOf(75000));
        requestDto.setLoanValue(BigDecimal.valueOf(250000));
        requestDto.setHomeValue(BigDecimal.valueOf(300000));

        when(mortgageService.calculateMortgage(any(MortgageCheckRequestDto.class)))
                .thenThrow(new NotMatchMaturityPeriodException("no period specified"));

        mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}