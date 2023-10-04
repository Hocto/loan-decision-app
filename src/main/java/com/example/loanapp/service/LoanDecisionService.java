package com.example.loanapp.service;

import com.example.loanapp.exception.EntityNotFoundException;
import com.example.loanapp.exception.LoanException;
import com.example.loanapp.external.CreditApi;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanDecisionService implements
        LoanAbstractService<LoanDecisionService.Parameter, LoanDecisionService.Result> {

    private static final BigDecimal MIN_VALUE = BigDecimal.valueOf(2000);
    private static final int MAX_PERIOD = 60;

    private final CreditApi creditApi;

    @Override
    public Result run(Parameter parameter) {

        CreditApi.CreditResponse creditResponse = creditApi.getCreditDetails(
                parameter.personalCode());

        if (creditResponse == null) {
            throw new EntityNotFoundException(
                    String.format("Id %s does not exist", parameter.personalCode()),
                    CreditApi.CreditResponse.class.getSimpleName()
            );
        }

        if (creditResponse.isHasDebt()) {
            return Result.builder().personalCode(parameter.personalCode()).decision(false).build();
        }

        Optional<BigDecimal> segmentQualifierOptional = Optional.ofNullable(creditResponse.getSegmentQualifier())
                .map(CreditApi.SegmentQualifier::getCreditModifier);

        if (segmentQualifierOptional.isEmpty()) {
            throw new LoanException("Unsupported Operation. Segment qualifier could not be null");
        }

        BigDecimal creditScore = segmentQualifierOptional.get()
                .divide(parameter.loanAmount(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(parameter.loanPeriod()));

        if (creditScore.compareTo(BigDecimal.ONE) < 0) {
            BigDecimal approvedAmount = findBestAmountForCredit(parameter.loanPeriod(), creditResponse.getSegmentQualifier().getCreditModifier());

            if (approvedAmount.compareTo(MIN_VALUE) < 0) {
                int longestPeriodForCredit = findLongestPeriodForCredit(parameter.loanAmount(), creditResponse.getSegmentQualifier().getCreditModifier());

                if (longestPeriodForCredit > MAX_PERIOD) {
                    return Result.builder().personalCode(parameter.personalCode()).decision(false).build();
                }

                return Result.builder().personalCode(parameter.personalCode()).approvedAmount(parameter.loanAmount()).approvedPeriod(longestPeriodForCredit).decision(true).build();
            }

            return Result.builder().personalCode(parameter.personalCode()).approvedAmount(approvedAmount).approvedPeriod(parameter.loanPeriod()).decision(true).build();
        }

        return Result.builder().personalCode(parameter.personalCode()).approvedAmount(parameter.loanAmount()).approvedPeriod(parameter.loanPeriod()).decision(true).build();
    }

    private BigDecimal findBestAmountForCredit(int loanPeriod, BigDecimal creditModifier) {
        return creditModifier.multiply(BigDecimal.valueOf(loanPeriod));

    }

    private int findLongestPeriodForCredit(BigDecimal loanAmount, BigDecimal creditModifier) {
        return loanAmount.divide(creditModifier, 2, RoundingMode.HALF_UP).intValue();
    }

    @Builder
    public record Parameter(String personalCode, BigDecimal loanAmount, int loanPeriod) {
    }

    @Builder
    public record Result(String personalCode, BigDecimal approvedAmount, Integer approvedPeriod, boolean decision) {
    }
}
