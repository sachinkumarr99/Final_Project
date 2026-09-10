package com.LegalMeterology.Online_Verification.Security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.LegalMeterology.Online_Verification.Entities.UserPrincipal;
import com.LegalMeterology.Online_Verification.Enums.Role;

@Component
public class SecurityUtils {
    
    public String getCurrentUserId() {

        UserPrincipal userDetails =
                (UserPrincipal) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        return userDetails.getId();
    }

     public List<String> getCurrentUserRole() {

        UserPrincipal userDetails =
                (UserPrincipal) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    }
}
