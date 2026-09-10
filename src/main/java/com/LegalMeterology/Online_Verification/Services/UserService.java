package com.LegalMeterology.Online_Verification.Services;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.LegalMeterology.Online_Verification.Dto.InstrumentDto;
import com.LegalMeterology.Online_Verification.Dto.SignUpDto;
import com.LegalMeterology.Online_Verification.Dto.UserDto;
import com.LegalMeterology.Online_Verification.Entities.User;
import com.LegalMeterology.Online_Verification.Enums.Role;
import com.LegalMeterology.Online_Verification.Exceptions.DuplicateResourceException;
import com.LegalMeterology.Online_Verification.Mappers.UserMapper;
import com.LegalMeterology.Online_Verification.Repositories.UserRepo;
import com.LegalMeterology.Online_Verification.Responses.RegistersResponse.InstrumentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final InstrumentService instrumentService;
    private final PasswordEncoder passwordEncoder;

    

    public boolean registerUser(UserDto userDto,Role userRole){
        if(userRepo.existsByEmail(userDto.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }
        User newUser=userMapper.toEntity(userDto);
        newUser.setRole(userRole);
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepo.save(newUser);
        return true;
    }

    public boolean isValidCredentials(SignUpDto signUpDto){

        Authentication authObject=authManager.authenticate(new UsernamePasswordAuthenticationToken(signUpDto.getEmail(), signUpDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authObject);
        return true;

    }

    public String generateToken(SignUpDto signUpDto){

        isValidCredentials(signUpDto);
        return jwtService.generateToken(signUpDto.getEmail());

    }

    public InstrumentResponse registerInstrument(InstrumentDto instrumentDto){
        return instrumentService.registerInstrument(instrumentDto);
    }

    public List<User> getAllUser(Role role){
        return userRepo.findAllByRole(role);
    }

}
