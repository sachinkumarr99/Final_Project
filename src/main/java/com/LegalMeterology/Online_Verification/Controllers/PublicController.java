package com.LegalMeterology.Online_Verification.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LegalMeterology.Online_Verification.Dto.SignUpDto;
import com.LegalMeterology.Online_Verification.Dto.UserDto;
import com.LegalMeterology.Online_Verification.Enums.Role;
import com.LegalMeterology.Online_Verification.Services.CertificateService;
import com.LegalMeterology.Online_Verification.Services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final UserService userService;
    private final CertificateService certificateService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto user){

        userService.registerUser(user,Role.BUSINESS_USER);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Registerd Successfully");
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignUpDto signUpDto){

        String token=userService.generateToken(signUpDto);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @GetMapping("/certificates/verify/{verificationToken}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String verificationToken){
        
        return ResponseEntity.ok(certificateService.verifyCertificate(verificationToken));
    }
    
}
