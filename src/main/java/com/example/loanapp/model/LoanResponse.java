package com.example.loanapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoanResponse(
        @Schema(example = "49002010965")
        String personalCode,
        @Min(2000)
        @Max(10000)
        @Schema(example = "2000")
        BigDecimal loanAmount,
        @Min(12)
        @Max(60)
        @Schema(example = "12")
        Integer loanPeriod,
        @Schema(example = "true")
        boolean decision) {
}
