package com.LegalMeterology.Online_Verification.Services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

import com.LegalMeterology.Online_Verification.Dto.InstrumentDto;
import com.LegalMeterology.Online_Verification.Entities.Instrument;
import com.LegalMeterology.Online_Verification.Enums.Machine.Status;
import com.LegalMeterology.Online_Verification.Exceptions.DuplicateResourceException;
import com.LegalMeterology.Online_Verification.Mappers.InstrumentMapper;
import com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo.InstrumentRepo;
import com.LegalMeterology.Online_Verification.Responses.RegistersResponse.InstrumentResponse;
import com.LegalMeterology.Online_Verification.Security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepo instrumentRepo;
    private final InstrumentMapper instrumentMapper;
    private final SecurityUtils securityUtils;
    private final SequenceService sequenceService;

    public boolean isInstrumentExist(String manufacturer, String serialNumber) {
        return instrumentRepo.existsByManufacturerAndSerialNumber(manufacturer, serialNumber);
    }

    public boolean isInstrumentExist(String instrumentNumber) {
        return instrumentRepo.existsByInstrumentNumber(instrumentNumber);
    }

    public InstrumentResponse registerInstrument(InstrumentDto instrumentDto) {

        if (isInstrumentExist(instrumentDto.getManufacturer(), instrumentDto.getSerialNumber())) {
            throw new DuplicateResourceException("Instrument Already Registered");
        }

        String ownerId = securityUtils.getCurrentUserId();

        Instrument newInstrument = instrumentMapper.toEntity(instrumentDto);
        newInstrument.setStatus(Status.UNVERIFIED);
        newInstrument.setOwnerId(ownerId);
        newInstrument.setInstrumentNumber(generateInstrumentNumber());
        Instrument registeredInstrument = instrumentRepo.save(newInstrument);
        return InstrumentResponse.builder()
                .instrumentNumber(registeredInstrument.getInstrumentNumber())
                .instrumentType(registeredInstrument.getInstrumentType())
                .status(registeredInstrument.getStatus())
                .build();
    }

    public String generateInstrumentNumber() {

        Long sequence = sequenceService.getNextSequence("instrument");

        return String.format(
                "LM-%d-%09d",
                LocalDateTime.now().getYear(),
                sequence);
    }

    public List<Instrument> getAllUserInstruments() {

        return instrumentRepo.findAllByOwnerId(securityUtils.getCurrentUserId());
    }

    
}
