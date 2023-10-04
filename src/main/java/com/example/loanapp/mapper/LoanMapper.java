package com.example.loanapp.mapper;

import com.example.loanapp.model.LoanRequest;
import com.example.loanapp.model.LoanResponse;
import com.example.loanapp.service.LoanDecisionService;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanDecisionService.Parameter convertRequestToParameter(LoanRequest loanRequest) {

        return LoanDecisionService.Parameter.builder()
                .personalCode(loanRequest.personalCode())
                .loanAmount(loanRequest.loanAmount())
                .loanPeriod(loanRequest.loanPeriod()).build();
    }

    public LoanResponse convertDecisionResultToLoanResponse(LoanDecisionService.Result result) {

        return LoanResponse.builder()
                .personalCode(result.personalCode())
                .loanAmount(result.approvedAmount())
                .loanPeriod(result.approvedPeriod())
                .decision(result.decision())
                .build();
    }
}
