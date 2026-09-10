package com.LegalMeterology.Online_Verification.Entities.VerificationRelated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationReading {

    private Double testValue;

    private Double expectedValue;

    private Double actualValue;

    private Double error;

    private Double permissibleError;

    private boolean passed;
}