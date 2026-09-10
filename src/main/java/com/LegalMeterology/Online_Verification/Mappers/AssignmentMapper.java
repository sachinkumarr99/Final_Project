package com.LegalMeterology.Online_Verification.Mappers;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.LegalMeterology.Online_Verification.Dto.AssignmentDto;
import com.LegalMeterology.Online_Verification.Entities.Assignment;
import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;

@Component
public class AssignmentMapper {
 
    public static Assignment toEntity(
            AssignmentDto dto,
            String assignedById
    ) {

        return Assignment.builder()
                .applicationNumber(dto.getApplicationNumber())
                .assignedToType(dto.getAssignedToType())
                .assignedToId(dto.getAssignedToId())
                .assignedById(assignedById)
                .status(ApplicationStatus.ASSIGNED)
                .remarks(dto.getRemarks())
                .assignedAt(LocalDateTime.now())
                .build();
    }

    public static AssignmentDto toResponse(
            Assignment entity
    ) {

        return AssignmentDto.builder()
                .applicationNumber(entity.getApplicationNumber())
                .assignedToType(entity.getAssignedToType())
                .assignedToId(entity.getAssignedToId())
                .remarks(entity.getRemarks())
                .build();
    }
}

