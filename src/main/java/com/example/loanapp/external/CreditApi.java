package com.example.loanapp.external;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class CreditApi {
    Map<String, CreditResponse> creditsByPersonalCode = new HashMap<>();

    @PostConstruct
    void init() {
        creditsByPersonalCode.put("49002010965",
                CreditResponse.builder()
                        .personalCode("49002010965")
                        .hasDebt(true)
                        .build());
        creditsByPersonalCode.put("49002010976",
                CreditResponse.builder()
                        .personalCode("49002010976")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_1)
                        .build());
        creditsByPersonalCode.put("49002010987",
                CreditResponse.builder()
                        .personalCode("49002010987")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_2)
                        .build());
        creditsByPersonalCode.put("49002010998",
                CreditResponse.builder()
                        .personalCode("49002010998")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_3)
                        .build());
        creditsByPersonalCode.put("49002010999",
                CreditResponse.builder()
                        .personalCode("49002010998")
                        .hasDebt(false)
                        .segmentQualifier(null)
                        .build());


    }

    public CreditResponse getCreditDetails(String id) {

        return creditsByPersonalCode.get(id);
    }

    @Builder
    @Getter
    public static class CreditResponse {

        @NotBlank
        private String personalCode;

        @NotNull
        private boolean hasDebt;

        private SegmentQualifier segmentQualifier;

    }

    @Getter
    @RequiredArgsConstructor
    public enum SegmentQualifier {

        SEGMENT_1(BigDecimal.valueOf(100)),
        SEGMENT_2(BigDecimal.valueOf(300)),
        SEGMENT_3(BigDecimal.valueOf(1000));

        @NotNull
        public final BigDecimal creditModifier;
    }
}
