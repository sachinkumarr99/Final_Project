package com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto;


import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDto {

    @NotEmpty(message = "At least one reading is required")
    @Valid
    private List<VerificationReadingDto> readings;
}