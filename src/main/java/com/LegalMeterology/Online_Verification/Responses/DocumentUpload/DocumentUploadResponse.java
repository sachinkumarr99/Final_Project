package com.LegalMeterology.Online_Verification.Responses.DocumentUpload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentUploadResponse<T> {
    
    private T category;
    private String originalFileName;
    private String cloudinaryPublicId;
    private String fileUrl;
    private String fileFormat;
    private Boolean isVerified;
    private String resourceType;
    private Long fileSizeInByte;
}
