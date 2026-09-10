package com.LegalMeterology.Online_Verification.Services;



import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.StandardTestPointDto;
import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.VerificationStandardDto;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.StandardTestPoint;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationStandard;
import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationStandardRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationStandardService {

    private final VerificationStandardRepo verificationStandardRepo;

    public VerificationStandard createStandard(
            VerificationStandardDto dto) {

        if (verificationStandardRepo
                .existsByInstrumentType(dto.getInstrumentType())) {

            throw new RuntimeException(
                    "Standard already exists for "
                    + dto.getInstrumentType()
            );
        }

        VerificationStandard standard =
                VerificationStandard.builder()
                        
                        .instrumentType(dto.getInstrumentType())
                        .testPoints(
                                dto.getTestPoints()
                                        .stream()
                                        .map(this::toEntity)
                                        .toList()
                        )
                        .unit(dto.getUnit())
                        .build();

        return verificationStandardRepo.save(standard);
    }

    private StandardTestPoint toEntity(
            StandardTestPointDto dto) {

        return StandardTestPoint.builder()
                .testValue(dto.getTestValue())
                .expectedValue(dto.getExpectedValue())
                .permissibleError(dto.getPermissibleError())
                .unit(dto.getUnit())
                .build();
    }

    public VerificationStandard getVerificationStandard(InstrumentType instrumentCategory){
        return verificationStandardRepo.findByInstrumentType(instrumentCategory).orElseThrow(()->new ResourceNotFoundException("Standards not Exist"));
    }
}