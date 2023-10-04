package com.example.loanapp.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LoanRequest(

        @NotBlank
        @Schema(example = "49002010965")
        String personalCode,

        @NotNull
        @Min(2000)
        @Max(10000)
        @Schema(example = "2000")
        BigDecimal loanAmount,

        @NotNull
        @Min(12)
        @Max(60)
        @Schema(example = "24")
        int loanPeriod) {
}
