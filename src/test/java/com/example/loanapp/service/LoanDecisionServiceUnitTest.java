package com.example.loanapp.service;

import com.example.loanapp.exception.EntityNotFoundException;
import com.example.loanapp.exception.LoanException;
import com.example.loanapp.external.CreditApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanDecisionServiceUnitTest {

    @Mock
    private CreditApi creditApi;

    @InjectMocks
    private LoanDecisionService loanService;

    @Test
    void onGivenValidParametersWithDebtReturnFalse() {

        when(creditApi.getCreditDetails(any())).thenReturn(
                CreditApi.CreditResponse.builder()
                        .hasDebt(true)
                        .build()
        );

        LoanDecisionService.Result actualResult = loanService.run(
                LoanDecisionService.Parameter.builder()
                        .personalCode("123456")
                        .loanAmount(BigDecimal.valueOf(2400))
                        .loanPeriod(24)
                        .build()
        );

        assertThat(actualResult.personalCode()).isEqualTo("123456");
        assertThat(actualResult.decision()).isFalse();
    }

    @Test
    void onGivenValidParametersWithNoDebtAndSegment1ReturnsLoanDecision() {

        when(creditApi.getCreditDetails(any())).thenReturn(CreditApi.CreditResponse.builder()
                .hasDebt(false)
                .segmentQualifier(CreditApi.SegmentQualifier.SEGMENT_1)
                .build()
        );

        LoanDecisionService.Result actualResult = loanService.run(
                LoanDecisionService.Parameter.builder()
                        .personalCode("123456")
                        .loanAmount(BigDecimal.valueOf(2400))
                        .loanPeriod(24)
                        .build()
        );

        assertThat(actualResult.personalCode()).isEqualTo("123456");
        assertThat(actualResult.approvedAmount()).isEqualTo(BigDecimal.valueOf(2400));
        assertThat(actualResult.approvedPeriod()).isEqualTo(24);
    }

    @Test
    void onGivenValidParametersWithNoDebtAndSegment1ReturnsWithDifferentPeriod() {

        when(creditApi.getCreditDetails(any())).thenReturn(
                CreditApi.CreditResponse.builder()
                        .hasDebt(false)
                        .segmentQualifier(CreditApi.SegmentQualifier.SEGMENT_1)
                        .build()
        );

        LoanDecisionService.Result actualResult = loanService.run(
                LoanDecisionService.Parameter.builder()
                        .personalCode("123456")
                        .loanAmount(BigDecimal.valueOf(4800))
                        .loanPeriod(12)
                        .build()
        );

        assertThat(actualResult.personalCode()).isEqualTo("123456");
        assertThat(actualResult.approvedAmount()).isEqualTo(BigDecimal.valueOf(4800));
        assertThat(actualResult.approvedPeriod()).isEqualTo(48);
        assertThat(actualResult.decision()).isTrue();
    }

    @Test
    void onGivenValidParametersWithNoDebtAndSegment1ReturnsFalseDecision() {

        when(creditApi.getCreditDetails(any())).thenReturn(
                CreditApi.CreditResponse.builder()
                        .hasDebt(false)
                        .segmentQualifier(CreditApi.SegmentQualifier.SEGMENT_1)
                        .build()
        );

        LoanDecisionService.Result actualResult = loanService.run(
                LoanDecisionService.Parameter.builder()
                        .personalCode("123456")
                        .loanAmount(BigDecimal.valueOf(9000))
                        .loanPeriod(12)
                        .build()
        );

        assertThat(actualResult.personalCode()).isEqualTo("123456");
        assertThat(actualResult.decision()).isFalse();
        assertThat(actualResult.approvedAmount()).isNull();
        assertThat(actualResult.approvedPeriod()).isNull();
    }

    @Test
    void onGivenValidParametersWithNonExistingPersonalCodeThrowsException() {

        when(creditApi.getCreditDetails(any())).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> loanService.run(
                LoanDecisionService.Parameter.builder()
                        .personalCode("123456")
                        .loanAmount(BigDecimal.valueOf(2400))
                        .loanPeriod(24)
                        .build()
        ));
    }
}
