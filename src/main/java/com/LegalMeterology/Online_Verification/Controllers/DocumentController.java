package com.LegalMeterology.Online_Verification.Controllers;

import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.LegalMeterology.Online_Verification.Enums.Machine.DocumentCategory;
import com.LegalMeterology.Online_Verification.Enums.Machine.Evidence;
import com.LegalMeterology.Online_Verification.Responses.DocumentUpload.DocumentUploadResponse;
import com.LegalMeterology.Online_Verification.Services.DocumentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("documents")
public class DocumentController {

    
    private final DocumentService documentService;

    @PostMapping("/instrument/upload_with_categories")
    public ResponseEntity<?> uploadWithCategories(
            @RequestParam("instrumentNumber") String instrumentNumber,
            @RequestParam("categories") List<DocumentCategory> categories,
            @RequestParam("files") MultipartFile[] files) {

        try {
            List<DocumentUploadResponse<DocumentCategory>> result = documentService.uploadInstrumentDocuments( categories, files,instrumentNumber);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    // documents for verification
    @PreAuthorize ("hasAnyAuthority('LMO','GATC_OFFICER')")
    @GetMapping ("verificationApplication/{applicationNumber}")
    public ResponseEntity<?> getInstrumentApplicationDocuments(@PathVariable String applicationNumber){
        return ResponseEntity.ok(documentService.getInstrumentDocument(applicationNumber));
    }


      // uploaded documents by officer for verification
    @PreAuthorize ("hasAnyAuthority('LMO','GATC_OFFICER')")
    @GetMapping("evidence/{applicationNumber}")
    public ResponseEntity<?> getUploadedDocuments(@PathVariable String applicationNumber){

        return ResponseEntity.ok(
                documentService.getVerificationEvidence(applicationNumber)
        );
    }

    @GetMapping ("instrument/document/verify/{documentId}")
    @PreAuthorize ("hasAnyAuthority('LMO','GATC_OFFICER')")
    public ResponseEntity<?> verifyInstrumentDocument(@PathVariable String documentId){

        return ResponseEntity.ok(
            documentService.verifyInstrumentDocument(documentId)
        );
    }

    @GetMapping ("evidence/verify/{documentId}")
    @PreAuthorize ("hasAnyAuthority('GATC_ADMIN','ADMIN')")
    public ResponseEntity<?> verifyInstrumentEvidence(@PathVariable String documentId ){
        
          return ResponseEntity.ok(
            documentService.verifyVerificationEvidence(documentId)
        );
    }

    // get certificate

    @GetMapping("certificate/{instrumentNumber}")
    @PreAuthorize("hasAnyAuthority('BUSINESS_USER')")
    public ResponseEntity<?> getCertificateData(@PathVariable String instrumentNumber){
        
        return ResponseEntity.ok(
            documentService.getCertificate(instrumentNumber)
        );
    }


    // upload verification evidence
    @PostMapping("/evidence/upload")
    @PreAuthorize("hasAnyAuthority('LMO','GATC_OFFICER')")
    public ResponseEntity<?> uploadVerificationEvidence(
        @RequestParam("applicationNumber") String applicationNumber,
        @RequestParam("categories") List<Evidence> categories,
        @RequestParam("files") MultipartFile[] files) {

    try {
        List<DocumentUploadResponse<Evidence>> result =
                documentService.uploadVerificationEvidence(
                        categories,
                        files,
                        applicationNumber
                );

        return ResponseEntity.ok(result);

    } catch (IOException e) {
        return ResponseEntity
                .status(500)
                .body("Error uploading verification evidence: " + e.getMessage());
    }
}

    @GetMapping("/instrument/document/access/{documentId}")
    @PreAuthorize("""
            hasAnyAuthority(
                'LMO',
                'GATC_OFFICER',
                'BUSINESS_USER',
                'GATC_ADMIN',
                'ADMIN'
            )
            """)
    public ResponseEntity<?> getInstrumentDocumentAccess(
            @PathVariable String documentId) {

        return ResponseEntity.ok(
                documentService.getInstrumentDocumentAccess(documentId)
        );
    }

    @GetMapping("/certificate/access/{instrumentNumber}")
    @PreAuthorize("hasAnyAuthority('BUSINESS_USER','LMO','GATC_OFFICER','GATC_ADMIN','ADMIN')")
    public ResponseEntity<?> getCertificateAccess(
            @PathVariable String instrumentNumber) {

        return ResponseEntity.ok(
                documentService.generateCertificateAccessUrl(
                    instrumentNumber
                )
        );
    }

    @GetMapping("/evidence/access/{documentId}")
    @PreAuthorize("""
        hasAnyAuthority(
            'LMO',
            'GATC_OFFICER',
            'BUSINESS_USER',
            'GATC_ADMIN',
            'ADMIN'
        )
        """)
    public ResponseEntity<?> accessEvidence(
            @PathVariable String documentId) {

        return ResponseEntity.ok(
                documentService.accessEvidence(documentId)
        );
}



  
}