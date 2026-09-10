package com.LegalMeterology.Online_Verification.Responses.RegistersResponse;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;
import com.LegalMeterology.Online_Verification.Enums.ApplicationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class VerificationApplicationResponse {

    private String applicationNumber;

    private String instrumentNumber;

    private ApplicationStatus status;
}
