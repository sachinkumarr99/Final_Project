package com.LegalMeterology.Online_Verification.Entities.VerificationRelated;

import com.LegalMeterology.Online_Verification.Enums.Machine.Unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardTestPoint {

    private Double testValue;

    private Double expectedValue;

    private Unit unit;

    private Double permissibleError;
}
