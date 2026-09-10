package com.LegalMeterology.Online_Verification.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.LegalMeterology.Online_Verification.Entities.User;
import com.LegalMeterology.Online_Verification.Entities.UserPrincipal;
import com.LegalMeterology.Online_Verification.Repositories.UserRepo;

@Component
public class CustomUserDetailsService implements UserDetailsService{

    private UserRepo userRepo;
    public CustomUserDetailsService(UserRepo userRepo){
        this.userRepo=userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {    

        User user=userRepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Email Not Exists"));
        return new UserPrincipal(user);
    
    }
    
}
