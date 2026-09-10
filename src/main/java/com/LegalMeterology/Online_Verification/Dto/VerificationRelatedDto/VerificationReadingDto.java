package com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto;



import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationReadingDto {

    @NotNull
    private Double testValue;

    @NotNull
    private Double actualValue;
}