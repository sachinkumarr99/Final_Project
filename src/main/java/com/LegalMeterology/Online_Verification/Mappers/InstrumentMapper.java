package com.LegalMeterology.Online_Verification.Mappers;

import org.springframework.stereotype.Component;
import com.LegalMeterology.Online_Verification.Dto.InstrumentDto;
import com.LegalMeterology.Online_Verification.Entities.Instrument;

@Component
public class InstrumentMapper {
    

    public Instrument toEntity(InstrumentDto dto) {
        
        if (dto == null) {
            return null;
        }
        

        return Instrument.builder()
                // .instrumentNumber(dto.getInstrumentNumber())
                .instrumentType(dto.getInstrumentType())
                .manufacturer(dto.getManufacturer())
                .model(dto.getModel())
                .serialNumber(dto.getSerialNumber())
                .capacity(dto.getCapacity())
                .accuracyClass(dto.getAccuracyClass())
                .unit(dto.getUnit())
                .installationLocation(dto.getInstallationLocation())
                // .status() // Default Status
                // .ownerId(ownerId)
                .build();
    }

    // Entity to DTO Mapping (Using Builder Pattern)
    public InstrumentDto toDto(Instrument entity) {
        if (entity == null) {
            return null;
        }

        return InstrumentDto.builder()
                // .instrumentNumber(entity.getInstrumentNumber())
                .instrumentType(entity.getInstrumentType())
                .manufacturer(entity.getManufacturer())
                .model(entity.getModel())
                .serialNumber(entity.getSerialNumber())
                .capacity(entity.getCapacity())
                .accuracyClass(entity.getAccuracyClass())
                .unit(entity.getUnit())
                .installationLocation(entity.getInstallationLocation())
                .build();
    }

}
