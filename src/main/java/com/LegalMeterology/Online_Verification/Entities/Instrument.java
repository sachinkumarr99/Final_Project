package com.LegalMeterology.Online_Verification.Entities;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.Machine.AccuracyClass;
import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;
import com.LegalMeterology.Online_Verification.Enums.Machine.Status;
import com.LegalMeterology.Online_Verification.Enums.Machine.Unit;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Builder
@Data
public class Instrument {
    
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id; 

    @Indexed(unique = true)
    @Field("instrument_number")
    private String instrumentNumber;

    @Field("instrument_type")
    private InstrumentType instrumentType;
    
    private String manufacturer;

    private String model;

    @Indexed
    @Field("serial_number")
    private String serialNumber;

    @Field("capacity")
    private Double capacity;

    @Field("accuracy_class")
    private AccuracyClass accuracyClass;

    private Unit unit;

    @Field("installation_location")
    private String installationLocation;

    @Field("status")
    private Status status;

    @Field("owner_id")
    @Indexed(unique = true)
    private String ownerId; 

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    

}
