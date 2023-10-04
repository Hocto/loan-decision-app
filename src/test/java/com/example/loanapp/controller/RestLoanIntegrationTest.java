package com.example.loanapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RestLoanIntegrationTest {

    private static final String LOAN_DECISION_URL = "/api/v1/loan/decision";

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:request/domain/loan/valid_check_decision_body_1.json")
    private Resource checkLoanDecisionBody1;

    @Value("classpath:request/domain/loan/valid_check_decision_body_2.json")
    private Resource checkLoanDecisionBody2;

    @Value("classpath:request/domain/loan/valid_check_decision_body_3.json")
    private Resource checkLoanDecisionBody3;

    @Value("classpath:request/domain/loan/non_existing_personal_code_body.json")
    private Resource nonExistingPersonalCodeBody;

    @Value("classpath:request/domain/loan/invalid_check_decision_body_1.json")
    private Resource invalidCheckDecisionBody1;

    @Value("classpath:request/domain/loan/invalid_check_decision_body_2.json")
    private Resource invalidCheckDecisionBody2;

    @Value("classpath:request/domain/loan/invalid_check_decision_body_3.json")
    private Resource invalidCheckDecisionBody3;

    @Value("classpath:request/domain/loan/invalid_check_decision_body_4.json")
    private Resource invalidCheckDecisionBody4;

    @Value("classpath:request/domain/loan/personal_code_with_null_segment_qualifier_body.json")
    private Resource personalCodeWithNullSegmentQualifierBody;

    @Test
    void onValidRequestBodyWithRetrieveMethodReturnsItem1() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(checkLoanDecisionBody1))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalCode", is("49002010965")))
                .andExpect(jsonPath("$.decision", is(false)));
    }

    @Test
    void onValidRequestBodyWithRetrieveMethodReturnsItem2() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(checkLoanDecisionBody2))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalCode", is("49002010987")))
                .andExpect(jsonPath("$.loanAmount", is(3000)))
                .andExpect(jsonPath("$.loanPeriod", is(36)))
                .andExpect(jsonPath("$.decision", is(true)));
    }

    @Test
    void onValidRequestBodyWithRetrieveMethodReturnsItem3() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(checkLoanDecisionBody3))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalCode", is("49002010998")))
                .andExpect(jsonPath("$.loanAmount", is(4000)))
                .andExpect(jsonPath("$.loanPeriod", is(36)))
                .andExpect(jsonPath("$.decision", is(true)));
    }

    @Test
    void onValidRequestBodyWithNonExistingPersonalCodeReturnsNotFound() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(nonExistingPersonalCodeBody))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.entity", is("CreditResponse")))
                .andExpect(jsonPath("$.message", is("Id 10101100 does not exist")));
    }

    @Test
    void onInvalidRequestBodyWithMinPeriodConstraintsReturnsBadRequest() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(invalidCheckDecisionBody1))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].field", is("loanPeriod")))
                .andExpect(jsonPath("$[0].reason", is("Min")))
                .andExpect(jsonPath("$[0].message", is("must be greater than or equal to 12")));
    }

    @Test
    void onInvalidRequestBodyWithMinAmountConstraintsReturnsBadRequest() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(invalidCheckDecisionBody2))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].field", is("loanAmount")))
                .andExpect(jsonPath("$[0].reason", is("Min")))
                .andExpect(jsonPath("$[0].message", is("must be greater than or equal to 2000")));
    }

    @Test
    void onInvalidRequestBodyWithMaxAmountConstraintsReturnsBadRequest() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(invalidCheckDecisionBody3))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].field", is("loanAmount")))
                .andExpect(jsonPath("$[0].reason", is("Max")))
                .andExpect(jsonPath("$[0].message", is("must be less than or equal to 10000")));
    }

    @Test
    void onInvalidRequestBodyWithMaxPeriodConstraintsReturnsBadRequest() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(invalidCheckDecisionBody4))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].field", is("loanPeriod")))
                .andExpect(jsonPath("$[0].reason", is("Max")))
                .andExpect(jsonPath("$[0].message", is("must be less than or equal to 60")));
    }

    @Test
    void onInvalidRequestBodyWithNullSegmentQualifierReturnsInternalServerError() throws Exception {

        MockHttpServletRequestBuilder builder = post(LOAN_DECISION_URL)
                .content(resourceToString(personalCodeWithNullSegmentQualifierBody))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type", is("LoanException")))
                .andExpect(jsonPath("$.message", is("Unsupported Operation. Segment qualifier could not be null")));
    }

    private String resourceToString(Resource resource) throws IOException {

        InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }
}
