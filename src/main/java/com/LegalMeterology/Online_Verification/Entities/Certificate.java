package com.LegalMeterology.Online_Verification.Entities;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;
import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;
import com.LegalMeterology.Online_Verification.Enums.Machine.Unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;


@Document(collection = "certificates")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Certificate {
    
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Field("certificate_number")
    @Indexed(unique = true)
    private String certificateNumber;

    @Field("instrument_number")
    @Indexed(unique = true)
    private String instrumentNumber;

    @Field("instrument_type")
    private InstrumentType instrumentType;

    @Field("application_number")
    @Indexed(unique = true)
    private String applicationNumber;

    private String manufacturer;
    private String model;
    private double capacity;
    private Unit capacityUnit;

    @Field("accuracy_class")
    private String accuracyClass;

    private String ownerId;

    @Field("installation_location")
    private String installationLocation;

    @Field("verification_date")
    private LocalDateTime verificationDate;

    @Field("verification_token")
    @Indexed(unique = true)
    private String verificationToken;

    @Field("valid_upto")
    private LocalDateTime validUpto;

    private String verifiedById;

    private ApplicationStatus status;

    @Field("certificate_url")
    private String certificateUrl;

    @Field("public_id")
    private String publicId;

    @Field("format_type")
    private String formatType;

    @Field("resource_type")
    private String resourceType;



}
