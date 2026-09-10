package com.LegalMeterology.Online_Verification.Controllers.UserController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LegalMeterology.Online_Verification.Dto.AssignmentDto;
import com.LegalMeterology.Online_Verification.Dto.UserDto;
import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.VerificationStandardDto;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationEvidence;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationStandard;
import com.LegalMeterology.Online_Verification.Enums.Role;
import com.LegalMeterology.Online_Verification.Repositories.VerificationAppRepo.VerificationApplicationRepo;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationEvidenceRepo;
import com.LegalMeterology.Online_Verification.Services.AssignmentService;
import com.LegalMeterology.Online_Verification.Services.CertificateService;
import com.LegalMeterology.Online_Verification.Services.DocumentService;
import com.LegalMeterology.Online_Verification.Services.UserService;
import com.LegalMeterology.Online_Verification.Services.VerificationStandardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AssignmentService assignmentService;
    private final VerificationApplicationRepo verificationApplicationRepo;
    private final VerificationStandardService verificationStandardService;
    private final CertificateService certificateService;
    private final VerificationEvidenceRepo verificationEvidenceRepo;
    
    @PostMapping("register_LMO")
    public ResponseEntity<?> registerLMO(@Valid @RequestBody UserDto lmo) {

        userService.registerUser(lmo, Role.LMO);
        return ResponseEntity.status(HttpStatus.CREATED).body("LMO Registerd Successfully");
    }

    @PostMapping("assigned_application")
    public ResponseEntity<?> assignedApplication(@Valid @RequestBody AssignmentDto assignmentDto) {
        return ResponseEntity.ok(assignmentService.assigned(assignmentDto));
    }

    @PostMapping("register_Organization")
    public ResponseEntity<?> registerOrganization(@Valid @RequestBody UserDto organization) {

        userService.registerUser(organization, Role.GATC_ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body("LMO Registerd Successfully");
    }

    @PostMapping("/verification-standards")
    public ResponseEntity<?> createVerificationStandard(
            @Valid @RequestBody VerificationStandardDto dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        verificationStandardService.createStandard(dto));
    }

    @GetMapping("applications")
    public ResponseEntity<?> getAllVerificationApplication() {
        return ResponseEntity.ok(
                verificationApplicationRepo.findAll());
    }

    // give all pending certificates
    @GetMapping("/certificate-pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getCertificatePendingApplications() {

        return ResponseEntity.ok(
                certificateService.getCertificatePendingApplications()
        );
    }

    @GetMapping("all_user")
    public ResponseEntity<?> getAllLMO(@RequestParam Role role) {
        return ResponseEntity.ok(
                userService.getAllUser(role));
    }

    @GetMapping("/generateCertificate/{applicationNumber}")
    public ResponseEntity<?> generateCertificate(@PathVariable String applicationNumber) throws Exception{

        return ResponseEntity.ok(certificateService.generateCertificate(applicationNumber));
    }

   @GetMapping("/evidences/{applicationNumber}")
public ResponseEntity<?> getEvidences(
        @PathVariable String applicationNumber) {

    List<VerificationEvidence> evidences =
            verificationEvidenceRepo
                    .findAllByApplicationNumber(applicationNumber);

    evidences.forEach(evidence ->
            System.out.println(
                    "Evidence ID = " + evidence.getId()
            )
    );

    return ResponseEntity.ok(evidences);
}




    


    
}
