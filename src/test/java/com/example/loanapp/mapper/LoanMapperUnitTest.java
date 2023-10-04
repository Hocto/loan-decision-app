package com.example.loanapp.mapper;

import com.example.loanapp.model.LoanRequest;
import com.example.loanapp.service.LoanDecisionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoanMapperUnitTest {

    private final LoanMapper loanMapper = new LoanMapper();

    @Test
    void onGivenValidParametersWithDebtReturnFalse() {

        LoanRequest loanRequest = LoanRequest.builder().personalCode("123456").loanAmount(BigDecimal.valueOf(2000)).loanPeriod(12).build();

        LoanDecisionService.Parameter actualResult = loanMapper.convertRequestToParameter(loanRequest);

        assertThat(actualResult.personalCode()).isEqualTo(loanRequest.personalCode());
        assertThat(actualResult.loanAmount()).isEqualTo(loanRequest.loanAmount());
        assertThat(actualResult.loanPeriod()).isEqualTo(loanRequest.loanPeriod());
    }
}
