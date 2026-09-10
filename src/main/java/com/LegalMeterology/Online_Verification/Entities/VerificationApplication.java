package com.LegalMeterology.Online_Verification.Entities;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;
import com.LegalMeterology.Online_Verification.Enums.ApplicationType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verification_applications")
public class VerificationApplication {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Indexed(unique = true)
    @Field("application_number")
    private String applicationNumber;

    @Field("user_id")
    private String userId;

    @Field("instrument_number")
    private String instrumentNumber;

    @Field("application_type")
    private ApplicationType applicationType;

    private ApplicationStatus status;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}