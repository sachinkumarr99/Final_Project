package com.LegalMeterology.Online_Verification.Controllers.UserController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.VerificationDto;
import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.VerificationScheduleDto;
import com.LegalMeterology.Online_Verification.Dto.VerificationRelatedDto.VerificationStandardDto;
import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationStandard;
import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;
import com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo.VerificationStandardRepo;
import com.LegalMeterology.Online_Verification.Services.DocumentService;
import com.LegalMeterology.Online_Verification.Services.LMOService;
import com.LegalMeterology.Online_Verification.Services.VerificationStandardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/lmo")
@RequiredArgsConstructor
public class LMOController {

    private final LMOService lmoService;
    private final VerificationStandardService verificationStandardService;

    @GetMapping("/applications")
    public ResponseEntity<?> getAssignedApplications() {

        return ResponseEntity.ok(
                lmoService.getAssignedApplications()
        );
    }

    @GetMapping("/applications/{applicationNumber}")
    public ResponseEntity<?> getApplication(
            @PathVariable String applicationNumber) {

        return ResponseEntity.ok(
                lmoService.getApplication(applicationNumber)
        );
    }

    @PostMapping("/applications/{applicationNumber}/schedule")
    public ResponseEntity<?> schedule(
            @PathVariable String applicationNumber,
            @Valid @RequestBody VerificationScheduleDto dto) {

        return ResponseEntity.ok(
                lmoService.schedule(applicationNumber, dto)
        );
    }

    @PostMapping("/applications/{applicationNumber}/verify")
    public ResponseEntity<?> verify(
            @PathVariable String applicationNumber,
            @Valid @RequestBody VerificationDto dto) {

        return ResponseEntity.ok(
                lmoService.verify(applicationNumber, dto)
        );
    }

     @GetMapping("/verification-standards/{instrumentType}")
     @PreAuthorize ("hasAnyAuthority('LMO','GATC_OFFICER')")
    public ResponseEntity<?> getVerificationStandard(@PathVariable InstrumentType instrumentType) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(verificationStandardService.getVerificationStandard(instrumentType));
                        
    }


    @GetMapping("/instruments/{instrumentNumber}")
    public ResponseEntity<?> getInstrument(@PathVariable String instrumentNumber) {
        return ResponseEntity.ok(lmoService.getInstrument(instrumentNumber));
    }

    

   

   
}