package com.LegalMeterology.Online_Verification.Mappers;

import org.springframework.stereotype.Component;

import com.LegalMeterology.Online_Verification.Entities.InstrumentDocument;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationEvidence;
import com.LegalMeterology.Online_Verification.Enums.Machine.DocumentCategory;
import com.LegalMeterology.Online_Verification.Enums.Machine.Evidence;
import com.LegalMeterology.Online_Verification.Responses.DocumentUpload.DocumentUploadResponse;

@Component
public class DocumentUploadMapper {

    public static DocumentUploadResponse<DocumentCategory> toResponse(
            InstrumentDocument document,
            String originalFileName
    ) {
        return new DocumentUploadResponse<DocumentCategory>(
                document.getCategory(),
                originalFileName,
                document.getCloudinaryPublicId(),
                document.getFileUrl(),
                document.getFileFormat(),
                false,
                document.getResourceType(),
                document.getFileSizeInBytes()
                
        );
    }

    public static InstrumentDocument toEntity(
            DocumentUploadResponse<DocumentCategory> response,
            String userId,
            String instrumentNumber,
            String fileFormat,
            Long fileSizeInBytes,
            boolean isVerified,
            String resourceType
    ) {
        return InstrumentDocument.builder()
                .userId(userId)
                .instrumentNumber(instrumentNumber)
                .category(response.getCategory())
                .fileUrl(response.getFileUrl())
                .cloudinaryPublicId(response.getCloudinaryPublicId())
                .fileFormat(fileFormat)
                .fileSizeInBytes(fileSizeInBytes)
                .isVerified(isVerified)
                .resourceType(resourceType)
                .build();
    }

    public static VerificationEvidence toVerificationEvidenceEntity(DocumentUploadResponse<Evidence> response,
                                                String verificationId,
                                                String verifiedById,
                                                String applicationNumber,
                                                String fileFormat,
                                                Long fileSizeInBytes,
                                                Boolean isVerified,
                                                String resourceType){

                
        return VerificationEvidence.builder()
                                   .applicationNumber(applicationNumber)
                                   .fileFormat(fileFormat)
                                   .fileSizeInBytes(fileSizeInBytes)
                                   .verificationId(verificationId)
                                   .verifiedById(verifiedById)
                                   .isVerified(isVerified)
                                   .cloudinaryPublicId(response.getCloudinaryPublicId())
                                   .fileUrl(response.getFileUrl())
                                   .evidenceCategory(response.getCategory())
                                   .resourceType(resourceType)
                                   .build();

    }
}
