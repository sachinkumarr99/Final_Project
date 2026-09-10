package com.LegalMeterology.Online_Verification.Entities.VerificationRelated;


import java.time.LocalDate;
import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verification_schedules")
public class VerificationSchedule {

    @Id
    private ObjectId id;

    @Field("application_number")
    private String applicationNumber;

    @Field("scheduled_by_id")
    private String scheduledById;

    @Field("scheduled_date")
    private LocalDate scheduledDate;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}