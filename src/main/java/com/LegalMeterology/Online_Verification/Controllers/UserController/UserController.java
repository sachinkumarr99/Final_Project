package com.LegalMeterology.Online_Verification.Controllers.UserController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LegalMeterology.Online_Verification.Dto.InstrumentDto;
import com.LegalMeterology.Online_Verification.Dto.VerificationApplicationDto;
import com.LegalMeterology.Online_Verification.Entities.Instrument;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;
import com.LegalMeterology.Online_Verification.Responses.ApiResponse;
import com.LegalMeterology.Online_Verification.Responses.RegistersResponse.InstrumentResponse;
import com.LegalMeterology.Online_Verification.Responses.RegistersResponse.VerificationApplicationResponse;
import com.LegalMeterology.Online_Verification.Services.InstrumentService;
import com.LegalMeterology.Online_Verification.Services.UserService;
import com.LegalMeterology.Online_Verification.Services.VerificationApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final VerificationApplicationService verificationApplicationService;
    private final InstrumentService instrumentService;
   

    @PostMapping("/register_instrument")
    public ResponseEntity<?> registerInstrument(@Valid @RequestBody InstrumentDto instrumentDto){
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<InstrumentResponse>builder()
                       .success(true)
                       .message("Instrument has been registerd successfully")
                       .data(userService.registerInstrument(instrumentDto))
                       .build()
        );
    }

    @PostMapping("/apply_verification_instrument")
    public ResponseEntity<?> verifyInstrument(@Valid @RequestBody VerificationApplicationDto verificationApplicationDto){
            
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.<VerificationApplicationResponse>builder()
                        .success(true)
                        .message("Verification Application Successfully Submitted")
                        .data(verificationApplicationService.submitApplication(verificationApplicationDto))
                        .build()
        );
    }

    @GetMapping("instruments")
    public ResponseEntity<?> getAllUserInstruments(){
        return ResponseEntity.status(HttpStatus.OK).body(
             ApiResponse.<List<Instrument>>builder()
                          .message("Instrument getted Successfully")
                          .success(true)
                          .data(instrumentService.getAllUserInstruments())
                          .build()
        );
                          
    }

    @GetMapping("applications")
    public ResponseEntity<?> getAllUserApplication(){
        return ResponseEntity.ok(
            ApiResponse.<List<VerificationApplication>>builder()
                        .message("Application getted Successfully")
                        .success(true)
                        .data(verificationApplicationService.getAllUserApplication())
                        .build()

        );
    }

    

    

}


