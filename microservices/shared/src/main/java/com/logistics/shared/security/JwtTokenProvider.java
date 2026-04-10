package com.logistics.shared.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT token provider for creating and validating JWT tokens.
 * Per constitution requirement: Secure JWT-based authentication across microservices.
 */
@Component
public class JwtTokenProvider {

    private final JwtAuthenticationConfig config;
    private final Key signingKey;

    public JwtTokenProvider(JwtAuthenticationConfig config) {
        this.config = config;
        this.signingKey = Keys.hmacShaKeyFor(config.secretKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT access token from authentication.
     */
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, config.accessTokenExpiry(), "access");
    }

    /**
     * Generate JWT refresh token from authentication.
     */
    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, config.refreshTokenExpiry(), "refresh");
    }

    /**
     * Extract username from JWT token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Extract authorities from JWT token.
     */
    public List<String> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);
        return authorities != null ? authorities : List.of();
    }

    /**
     * Validate JWT token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Check if token is expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException ex) {
            return true;
        }
    }

    private String generateToken(Authentication authentication, java.time.Duration expiry, String tokenType) {
        Instant now = Instant.now();
        Instant expiryInstant = now.plus(expiry);

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuer(config.issuer())
                .setAudience(config.audience())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .claim("authorities", authorities)
                .claim("type", tokenType)
                .signWith(signingKey)
                .compact();
    }
}