package com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto;


import com.LegalMeterology.Online_Verification.Enums.Machine.Unit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardTestPointDto {

    @NotNull(message = "Test value is required")
    private Double testValue;

    @NotNull(message = "Expected value is required")
    private Double expectedValue;

    @NotNull(message = "Permissible error is required")
    private Double permissibleError;

    private Unit unit;
}