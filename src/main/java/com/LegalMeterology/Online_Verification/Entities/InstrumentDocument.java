package com.LegalMeterology.Online_Verification.Entities;

import java.time.LocalDateTime;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.LegalMeterology.Online_Verification.Enums.Machine.DocumentCategory;
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
@Document(collection = "documents")
public class InstrumentDocument {


    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Field("user_id")
    private String userId;

    @Field("instrument_number")
    private String instrumentNumber;

    private DocumentCategory category;

    private boolean isVerified;

    @Field("file_url")
    private String fileUrl;

    @CreatedDate
    @Field("uploaded_at")
    private LocalDateTime uploadedAt;

    @Field("cloudinary_public_id")
    private String cloudinaryPublicId; 

    @Field("resource_type")
    private String resourceType;

    private String fileFormat;       
    private Long fileSizeInBytes;    
    
}
