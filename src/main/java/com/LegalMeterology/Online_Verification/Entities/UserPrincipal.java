package com.LegalMeterology.Online_Verification.Entities;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails{

    private User user;
    public UserPrincipal(User user){
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public @Nullable String getPassword() {
       return user.getPassword();
    }

    @Override
    public String getUsername() {
       return user.getEmail();
    }

    public String getId(){
        return user.getId().toString();
    }
    
}
