package com.LegalMeterology.Online_Verification.Filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.LegalMeterology.Online_Verification.Entities.UserPrincipal;
import com.LegalMeterology.Online_Verification.Services.CustomUserDetailsService;
import com.LegalMeterology.Online_Verification.Services.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService; // User Details Service Class

    public JwtFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService){
        this.jwtService=jwtService;
        this.customUserDetailsService=customUserDetailsService;
    }

    // Spring Exception Resolver inject karein
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Request Header se 'Authorization' nikalein
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String username;

        // 2. Check karein ki Header present hai aur "Bearer " se start ho raha hai
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " prefix hata kar actual Token aur Username nikalein
        jwtToken = authHeader.substring(7);
       try{

             username = jwtService.extractUsername(jwtToken);

        // 4. Validate karein ki Username mila hai aur user pehle se Authenticated nahi hai
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Database se User Details load karein
            UserPrincipal userDetails = (UserPrincipal) customUserDetailsService.loadUserByUsername(username);

            // Check karein ki Token valid hai ya nahi
            if (jwtService.isTokenValid(jwtToken)) {
                
                // Security Authentication Token create karein
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Request details set karein (IP, Session info, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Spring Security Context me User ko Authenticated set karein
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
       }
       catch(Exception e){
            log.error(e.getMessage());
            resolver.resolveException(request, response, null, e);
            return ;
       }

        // 5. Agle filter ke paas request pass karein
        filterChain.doFilter(request, response);
    }
    
}
