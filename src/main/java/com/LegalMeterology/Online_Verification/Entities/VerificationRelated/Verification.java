package com.LegalMeterology.Online_Verification.Entities.VerificationRelated;


import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verifications")
// jab lmo verification kar lega to reading ke liye ek object create karega
public class Verification {

    @Id
    private ObjectId id;

    @Field("application_number")
    private String applicationNumber;

    @Field("instrument_number")
    private String instrumentNumber;

    @Field("lmo_id")
    private String lmoId;

    private List<VerificationReading> readings;

    private ApplicationStatus result;

    @Field("valid_upto")
    private LocalDateTime validUpto;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}