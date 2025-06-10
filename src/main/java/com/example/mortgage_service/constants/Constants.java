package com.example.mortgage_service.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {
    public static final List<Integer> MATURITY_PERIODS = Collections.unmodifiableList(Arrays.asList(3, 6, 9, 12, 18, 24, 36, 48, 60, 72));
    public static final Integer MORTGAGE_TO_INCOME_RATIO = 4;
    public static final String MORTGAGE_EXCEEDS_INCOME = "Mortgage exceeds 4 times the income value";
    public static final String MORTGAGE_EXCEEDS_HOME_VALUE = "Mortgage exceeds the cost of a home";
}
