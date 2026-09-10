package com.LegalMeterology.Online_Verification.Services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import com.LegalMeterology.Online_Verification.Entities.UserPrincipal;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    // Secret Key for Token
    @Value("${myApp.jwt.secretKey}")
    private String SECRET_KEY ;
    
    // Token validity:(in milliseconds)
    @Value("#{${myApp.jwt.expirationTime}}")
    private long EXPIRATION_TIME;

    private final CustomUserDetailsService customUserDetailsService;

    
    // 1. Secret Key ko Sign karne ke liye decode karna
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

  

    // 3. Custom Claims ke sath Token Generate karna
    public String generateToken( String email) {

        UserPrincipal userDetails=(UserPrincipal)customUserDetailsService.loadUserByUsername(email);
        Instant now=Instant.now();

        Map<String, Object> extraClaims=new HashMap<>();  // custom claims

        extraClaims.put("id",userDetails.getId());
        extraClaims.put("role",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }

    // 4. Token se email (Subject) nikalna
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 5. Generic Claim Extractor
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 6. Token se sabhi Claims Parse karna
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 7. Token Expire hua hai ya nahi check karna
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 8. Token Valid hai ya nahi verify karna
    public boolean isTokenValid(String token) {
        return  !isTokenExpired(token);
    }

    
}