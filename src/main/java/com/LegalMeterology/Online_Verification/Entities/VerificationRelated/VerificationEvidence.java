package com.LegalMeterology.Online_Verification.Entities.VerificationRelated;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.Machine.Evidence;

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
@Document(collection = "verification_evidence")
public class VerificationEvidence {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    // isme readings jo le thi jis officer ne uska verification object ka id(contains officer id also)
    @Field("verification_id")
    private String verificationId;

    // means kon user inhe verified karega for generating certificate
    @Field("verified_by")
    private String verifiedById;

    private Boolean isVerified;
    

    @Field("application_number")
    private String applicationNumber;

    @Field("file_url")
    private String fileUrl;

    private Evidence evidenceCategory;

    @Field("cloudinary_public_id")
    private String cloudinaryPublicId;

    private String fileFormat;

    private Long fileSizeInBytes;

    @Field("resource_type")
    private String resourceType;

    @CreatedDate
    @Field("uploaded_at")
    private LocalDateTime uploadedAt;
}