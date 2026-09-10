package com.LegalMeterology.Online_Verification.Dto;

import com.LegalMeterology.Online_Verification.Enums.ApplicationType;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationApplicationDto {

    @NotNull(message = "Instrument Number is required")
    private String instrumentNumber;

    @NotNull(message = "Application type is required")
    private ApplicationType applicationType;
}