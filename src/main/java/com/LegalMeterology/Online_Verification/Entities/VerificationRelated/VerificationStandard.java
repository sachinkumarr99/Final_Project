package com.LegalMeterology.Online_Verification.Entities.VerificationRelated;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;
import com.LegalMeterology.Online_Verification.Enums.Machine.Unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verification_standards")
public class VerificationStandard {

    @Id
    private ObjectId id;

    @Field("instrument_type")
    @Indexed (unique=true)
    private InstrumentType instrumentType;

    private List<StandardTestPoint> testPoints;

     private Unit unit;
}