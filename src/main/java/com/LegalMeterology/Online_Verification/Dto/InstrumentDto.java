package com.LegalMeterology.Online_Verification.Dto;
import com.LegalMeterology.Online_Verification.Entities.Instrument;
import com.LegalMeterology.Online_Verification.Enums.Machine.AccuracyClass;
import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;
import com.LegalMeterology.Online_Verification.Enums.Machine.Unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentDto {


    @NotNull(message = "Instrument type is required")
    private InstrumentType instrumentType;

    @NotBlank(message = "Manufacturer name is required")
    private String manufacturer;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than zero")
    private Double capacity;

    @NotNull(message = "Accuracy class is required")
    private AccuracyClass accuracyClass;

    @NotNull(message = "Unit is required")
    private Unit unit;

    @NotBlank(message = "Installation location is required")
    private String installationLocation;

}
