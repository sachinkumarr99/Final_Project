package com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto;

import java.util.List;

import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;
import com.LegalMeterology.Online_Verification.Enums.Machine.Unit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationStandardDto {

    @NotNull(message = "Instrument type is required")
    private InstrumentType instrumentType;

    @NotEmpty(message = "Test points are required")
    private List<StandardTestPointDto> testPoints;

    @NotNull (message = "unit cannot be null")
    private Unit unit;
}
