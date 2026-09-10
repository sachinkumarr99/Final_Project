package com.LegalMeterology.Online_Verification.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.LegalMeterology.Online_Verification.Entities.Assignment;
import com.LegalMeterology.Online_Verification.Entities.Certificate;
import com.LegalMeterology.Online_Verification.Entities.Instrument;
import com.LegalMeterology.Online_Verification.Entities.InstrumentDocument;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.Verification;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationEvidence;
import com.LegalMeterology.Online_Verification.Enums.Role;
import com.LegalMeterology.Online_Verification.Enums.Machine.DocumentCategory;
import com.LegalMeterology.Online_Verification.Enums.Machine.Evidence;
import com.LegalMeterology.Online_Verification.Exceptions.DuplicateResourceException;
import com.LegalMeterology.Online_Verification.Mappers.DocumentUploadMapper;
import com.LegalMeterology.Online_Verification.Repositories.AssignmentRepo;
import com.LegalMeterology.Online_Verification.Repositories.CertificateRepo;
import com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo.InstrumentDocumentRepo;
import com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo.InstrumentRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationAppRepo.VerificationApplicationRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationEvidenceRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationRepo;
import com.LegalMeterology.Online_Verification.Responses.DocumentUpload.DocumentUploadResponse;
import com.LegalMeterology.Online_Verification.Security.SecurityUtils;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final Cloudinary cloudinary;
    private final SecurityUtils securityUtils;
    private final InstrumentDocumentRepo instrumentDocumentRepo;
    private final InstrumentRepo instrumentRepo;
    private final VerificationEvidenceRepo verificationEvidenceRepo;
    private final AssignmentRepo assignmentRepo;
    private final VerificationApplicationRepo verificationApplicationRepo;
    private final VerificationRepo verificationRepo;
    private final CertificateRepo certificateRepo;
    private final CloudinaryService cloudinaryService;
    private final VerificationEvidenceRepo evidenceRepo;
    
private <T> List<DocumentUploadResponse<T>> uploadCategorizedDocuments(
        List<T> categories,
        MultipartFile[] files) throws IOException {

    List<DocumentUploadResponse<T>> responseList = new ArrayList<>();

    for (int i = 0; i < files.length; i++) {

        MultipartFile file = files[i];

        if (file == null || file.isEmpty()) {
            continue;
        }

        T category = categories.get(i);

        // ----------------------------------------------------
        // 1. Original filename
        // ----------------------------------------------------
        String originalFileName = file.getOriginalFilename();

        // ----------------------------------------------------
        // 2. Unique public ID
        // ----------------------------------------------------
        String uniqueString =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8);

        String cleanPublicId =
                category + "_"
                        + System.currentTimeMillis()
                        + "_"
                        + uniqueString;

        // ----------------------------------------------------
        // 3. Current user
        // ----------------------------------------------------
        String userId = securityUtils.getCurrentUserId();

        // ----------------------------------------------------
        // 4. Folder structure
        // ----------------------------------------------------
        String folderPath = String.format(
                "legal_meterology/users/usr_%s/%s",
                userId,
                category
        );

        // ----------------------------------------------------
        // 5. Detect resource type
        // ----------------------------------------------------
        String contentType = file.getContentType();

        String resourceType;

        if (contentType != null
                && contentType.startsWith("image/")) {

            resourceType = "image";

        } else {

            // PDF and other non-image files
            resourceType = "raw";
        }

                if ("raw".equals(resourceType)) {
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            }
            cleanPublicId = cleanPublicId + extension;
        }

        // ----------------------------------------------------
        // 6. Cloudinary upload parameters
        // ----------------------------------------------------
        Map uploadParams = ObjectUtils.asMap(
                "folder", folderPath,
                "public_id", cleanPublicId,
                "resource_type", resourceType,
                "overwrite", false
        );

        // ----------------------------------------------------
        // 7. Upload to Cloudinary
        // ----------------------------------------------------
        Map uploadResult =
                cloudinary.uploader()
                        .upload(
                                file.getBytes(),
                                uploadParams
                        );

        // ----------------------------------------------------
        // 8. Cloudinary response
        // ----------------------------------------------------
        String secureUrl =
                uploadResult.get("secure_url").toString();

        String returnedPublicId =
                uploadResult.get("public_id").toString();

        String returnedResourceType =
                uploadResult.get("resource_type").toString();

        // ----------------------------------------------------
        // 9. File information
        // ----------------------------------------------------
        String fileFormat = file.getContentType();

        Long fileSizeInBytes = file.getSize();

        // ----------------------------------------------------
        // 10. Response object
        // ----------------------------------------------------
        responseList.add(
                new DocumentUploadResponse<T>(
                        category,
                        originalFileName,
                        returnedPublicId,
                        secureUrl,
                        fileFormat,
                        false,
                        returnedResourceType,
                        fileSizeInBytes
                )
        );
    }

    return responseList;
}

    public List<DocumentUploadResponse<DocumentCategory>> uploadInstrumentDocuments(
            List<DocumentCategory> categories,
            MultipartFile[] files,
            String instrumentNumber) throws IOException {

        if (!instrumentRepo.existsByInstrumentNumber(instrumentNumber)) {
            throw new UsernameNotFoundException("Instrument Not exist");
        }

        if (files == null || files.length == 0 || categories == null || categories.size() == 0
                || files.length != categories.size()) {
            return new ArrayList<>();
        }

        for (int i = 0; i < categories.size(); i++) {
            if(instrumentDocumentRepo.existsByInstrumentNumberAndCategory(instrumentNumber,categories.get(i))){
                throw new DuplicateResourceException(categories.get(i)+" already uploaded , first remove it for uploading documents");
            }
        }

        List<DocumentUploadResponse<DocumentCategory>> uploadedDocuments = uploadCategorizedDocuments(categories, files);
        for (DocumentUploadResponse<DocumentCategory> uploadedDocument : uploadedDocuments) {
// instrument documents not verified but uploaded
            instrumentDocumentRepo
                    .save(DocumentUploadMapper.toEntity(uploadedDocument, securityUtils.getCurrentUserId(),
                            instrumentNumber, uploadedDocument.getFileFormat(), uploadedDocument.getFileSizeInByte(),false,uploadedDocument.getResourceType()));

        }
        return uploadedDocuments;

    }


    public List<DocumentUploadResponse<Evidence>> uploadVerificationEvidence(List<Evidence> evidenceCategory,MultipartFile[] files,String applicationNumber) throws IOException{

        // application exist or not
        VerificationApplication application=verificationApplicationRepo.findByApplicationNumber(applicationNumber).orElseThrow(()->new UsernameNotFoundException("Application Not Exist"));

        
        if (files == null || files.length == 0 ||
        evidenceCategory == null || evidenceCategory.isEmpty() ||
        files.length != evidenceCategory.size()) {

        return new ArrayList<>();
         }

                   // verification done or not if not then do it
    Verification verification=verificationRepo.findByApplicationNumber(applicationNumber).orElseThrow(()->new UsernameNotFoundException("Verification Not Done Yet"));

         // upload documents on cloudinary
    List<DocumentUploadResponse<Evidence>> uploadedDocuments =
            uploadCategorizedDocuments(
                    evidenceCategory,
                    files
            );

  
    for(int i=0;i<files.length;i++){
        verificationEvidenceRepo.save(DocumentUploadMapper.toVerificationEvidenceEntity(uploadedDocuments.get(i),verification.getId().toString(),null,applicationNumber,uploadedDocuments.get(i).getFileFormat(),uploadedDocuments.get(i).getFileSizeInByte(),false,uploadedDocuments.get(i).getResourceType()));
    }

    return uploadedDocuments;

    }


    public List<VerificationEvidence> getVerificationEvidence(String applicationNumber){

        String currentUserId = securityUtils.getCurrentUserId();
        Assignment assignmentDetails= assignmentRepo.findByApplicationNumber(applicationNumber).orElseThrow(()->new UsernameNotFoundException("Not Assigned"));

       boolean isAdmin = securityUtils.getCurrentUserRole().contains(Role.ADMIN.name());
        if (!assignmentDetails.getAssignedById().equals(currentUserId) && !isAdmin) {
                throw new AccessDeniedException("Only the officer or admin who assigned this task can get documents");
        }
     return verificationEvidenceRepo.findAllByApplicationNumber(applicationNumber);
    }

    public List<InstrumentDocument> getInstrumentDocument(String applicationNumber){

    VerificationApplication verificationApplication=verificationApplicationRepo.findByApplicationNumber(applicationNumber).orElseThrow(()->new UsernameNotFoundException("Application Not Exist"));
    List<InstrumentDocument> instrumentDocuments=instrumentDocumentRepo.findAllByInstrumentNumber(verificationApplication.getInstrumentNumber());
    return instrumentDocuments;
    }


    public boolean isUploadedAllInstrumentDocument(String applicationNumber){

        VerificationApplication verificationApplication=verificationApplicationRepo.findByApplicationNumber(applicationNumber).orElseThrow(()->new UsernameNotFoundException("Application not exist"));
        List<InstrumentDocument> instrumentDocuments=instrumentDocumentRepo.findAllByInstrumentNumber(verificationApplication.getInstrumentNumber());

          Set<DocumentCategory> requiredDocuments = Set.of(
            DocumentCategory.INSTRUMENT_PHOTO,
            DocumentCategory.PURCHASE_INVOICE,
            DocumentCategory.MANUFACTURER_DOCUMENT
    );

    Set<DocumentCategory> uploadedDocuments = instrumentDocuments.stream()
            .map(InstrumentDocument::getCategory)
            .collect(Collectors.toSet());

    return uploadedDocuments.containsAll(requiredDocuments);
    }

// verify instrument uploaded documents before scheduling
    public InstrumentDocument verifyInstrumentDocument(String instrumentDocumentId){

        InstrumentDocument uploadedInstrumentDocument=instrumentDocumentRepo.findById(new ObjectId(instrumentDocumentId)).orElseThrow(()->new UsernameNotFoundException("Instrument Document Not found"));

        VerificationApplication verificationApplication=verificationApplicationRepo.findByInstrumentNumber(uploadedInstrumentDocument.getInstrumentNumber()).orElseThrow(()->new UsernameNotFoundException("Verification Application Not Exist"));
        
         String currentUserId = securityUtils.getCurrentUserId();
         Assignment assignmentDetails= assignmentRepo.findByApplicationNumber(verificationApplication.getApplicationNumber()).orElseThrow(()->new UsernameNotFoundException("Not Assigned"));

        boolean isAdmin = securityUtils.getCurrentUserRole().contains(Role.ADMIN.name());
        if (!assignmentDetails.getAssignedToId().equals(currentUserId) && !isAdmin) {
                throw new AccessDeniedException("Only the officer or admin who assigned this task can verify documents");
        }

        uploadedInstrumentDocument.setVerified(true);
        return instrumentDocumentRepo.save(uploadedInstrumentDocument);
    }

// verify verification evidence of instrument

    
    public VerificationEvidence verifyVerificationEvidence(
        String verificationEvidenceId) {

    VerificationEvidence evidence =
            verificationEvidenceRepo
                    .findById(new ObjectId(verificationEvidenceId))
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Verification Evidence not found"
                            ));

    VerificationApplication application =
            verificationApplicationRepo
                    .findByApplicationNumber(
                            evidence.getApplicationNumber()
                    )
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Verification Application not exist"
                            ));

    String currentUserId = securityUtils.getCurrentUserId();

    Assignment assignment =
            assignmentRepo
                    .findByApplicationNumber(
                            application.getApplicationNumber()
                    )
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Application not assigned"
                            ));

    boolean isAdmin = securityUtils
            .getCurrentUserRole()
            .contains(Role.ADMIN.name());

    if (!assignment.getAssignedById().equals(currentUserId)
            && !isAdmin) {

        throw new AccessDeniedException(
                "Only the officer or admin  who assigned you task can verify verification evidence"
        );
    }

    evidence.setIsVerified(true);
    evidence.setVerifiedById(currentUserId);
    return verificationEvidenceRepo.save(evidence);
}



    public boolean isAllInstrumentDocumentVerify(String applicationNumber){

          VerificationApplication verificationApplication=verificationApplicationRepo.findByApplicationNumber(applicationNumber).orElseThrow(()->new UsernameNotFoundException("Application not exist"));
        List<InstrumentDocument> instrumentDocuments=instrumentDocumentRepo.findAllByInstrumentNumber(verificationApplication.getInstrumentNumber());

        for(InstrumentDocument instrumentDocument: instrumentDocuments){
            if(!instrumentDocument.isVerified()) return false;
        }

        return true;

    }

    public Certificate getCertificate(String instrumentNumber){

        // check instrument exist or not
        Instrument instrument=instrumentRepo.findByInstrumentNumber(instrumentNumber).orElseThrow(()->new UsernameNotFoundException("Instrument Not Exist"));
        
        return certificateRepo.findByInstrumentNumber(instrumentNumber).orElseThrow(()->new ResourceNotFoundException("Certifcate Not be Issued"));
    }


    // get Access url for document

   public String getInstrumentDocumentAccess(String documentId) {

    InstrumentDocument document =
            instrumentDocumentRepo.findById(new ObjectId(documentId))
                    .orElseThrow(() ->
                            new RuntimeException("Document not found"));

    return cloudinaryService.generateSignedUrl(
            document.getCloudinaryPublicId(),
            document.getResourceType()
    );
    }

    public String getEvidenceAccess(String evidenceId) {

    VerificationEvidence evidence =
            evidenceRepo.findById(new ObjectId(evidenceId))
                    .orElseThrow(() ->
                            new RuntimeException("Evidence not found"));

    return cloudinaryService.generateSignedUrl(
            evidence.getCloudinaryPublicId(),
            evidence.getResourceType()
    );

       
}
// Generate certificate access URL
public String generateCertificateAccessUrl(String instrumentNumber) {

    Certificate certificate =
            certificateRepo.findByInstrumentNumber(instrumentNumber)
                    .orElseThrow(() ->
                            new RuntimeException("Certificate not found"));

    return cloudinaryService.generateSignedUrl(
           certificate.getPublicId(),
           certificate.getResourceType()
   );
}

public String accessEvidence(String documentId) {

    VerificationEvidence evidence =
            verificationEvidenceRepo
                    .findById(new ObjectId(documentId))
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Verification Evidence not found"
                            ));

    return evidence.getFileUrl();
}

}
