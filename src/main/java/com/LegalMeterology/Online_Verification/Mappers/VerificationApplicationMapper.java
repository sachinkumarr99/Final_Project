package com.LegalMeterology.Online_Verification.Mappers;
import org.springframework.stereotype.Component;

import com.LegalMeterology.Online_Verification.Dto.VerificationApplicationDto;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;
import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;

@Component
public class VerificationApplicationMapper {

    public VerificationApplication toEntity(
            VerificationApplicationDto dto,
            String userId,
            String applicationNumber) {

        return VerificationApplication.builder()
                .applicationNumber(applicationNumber)
                .userId(userId)
                .instrumentNumber(dto.getInstrumentNumber())
                .applicationType(dto.getApplicationType())
                .status(ApplicationStatus.SUBMITTED)
                .build();
    }
}