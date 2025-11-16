package com.example.demo.security.services;

import com.example.demo.security.entities.CredentialEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWTService {
    @Value("${jwt_secret}")
    private String jwtSecretKey;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        if(userDetails instanceof CredentialEntity credential) {
            if(credential.getUser() != null) {
                claims.put("userId", credential.getUser().getId());
            }
            if(credential.getCompany() != null){
                claims.put("companyId", credential.getCompany().getId());
            }
        }

        return buildToken(claims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken (UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return buildToken(claims, userDetails, refreshTokenExpiration);
    }

    public boolean validateRefreshToken (String refreshToken, UserDetails userDetails){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(refreshToken);

            final String username = extractUsername(refreshToken);
            return (username.equals(userDetails.getUsername())) &&
                    !isTokenExpired(refreshToken);
        }catch (JwtException e){
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T>
            claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean isAdmin(String token){
        return "ROLE_ADMIN".equals(extractRoles(token));
    }

    public boolean isCompany(String token){
        return "ROLE_COMPANY".equals(extractRoles(token));
    }

    public boolean isUser(String token){
        return "ROLE_USER".equals(extractRoles(token));
    }

    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()))
                && !isTokenExpired(token)
                && userDetails.isAccountNonLocked()
                && userDetails.isEnabled();
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +
                        expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
