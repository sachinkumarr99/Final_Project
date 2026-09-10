package com.LegalMeterology.Online_Verification.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;
import com.LegalMeterology.Online_Verification.Enums.AssignmentType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "assignments")
public class Assignment {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Indexed(unique = true)
    private String applicationNumber;

    // LMO or GATC
    private AssignmentType assignedToType;

    // LMO userId or GATC organizationId
    private String assignedToId;

    // Admin userId
    private String assignedById;

    private ApplicationStatus status;

    private String remarks;

    private LocalDateTime assignedAt;
}