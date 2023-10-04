package com.example.loanapp.controller;


import com.example.loanapp.mapper.LoanMapper;
import com.example.loanapp.model.LoanRequest;
import com.example.loanapp.model.LoanResponse;
import com.example.loanapp.service.LoanDecisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/loan")
@RequiredArgsConstructor
@Validated
public class LoanController {

    private final LoanDecisionService loanDecisionService;
    private final LoanMapper loanMapper;

    @PostMapping("/decision")
    public ResponseEntity<LoanResponse> decision(@Valid @RequestBody LoanRequest loanRequest) {

        var decisionResult = loanDecisionService.run(loanMapper.convertRequestToParameter(loanRequest));
        return new ResponseEntity<>(loanMapper.convertDecisionResultToLoanResponse(decisionResult), HttpStatus.OK);
    }
}
