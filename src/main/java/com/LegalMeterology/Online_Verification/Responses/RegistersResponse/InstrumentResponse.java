package com.LegalMeterology.Online_Verification.Responses.RegistersResponse;

import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.Machine.AccuracyClass;
import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;
import com.LegalMeterology.Online_Verification.Enums.Machine.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;




@Builder
@Getter
@AllArgsConstructor
public class InstrumentResponse {
    
    private String instrumentNumber;

    private InstrumentType instrumentType;

    private Status status;
}
