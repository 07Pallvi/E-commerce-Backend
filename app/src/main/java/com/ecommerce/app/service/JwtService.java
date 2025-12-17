package com.ecommerce.app.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtService {

    private static final Logger LOGGER = LogManager.getLogger(JwtService.class);

    @Value("${jwt.access.secret}")
    private String accessTokenSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;


    // Generate access token for user
    public String generateAccessToken(UUID userId, String mobile, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("mobile", mobile);
        claims.put("role", role);
        claims.put("tokenType", "access");
        
        return createToken(claims, mobile, accessTokenExpiration, getAccessSignKey());
    }

    // Generate refresh token for user
    public String generateRefreshToken(UUID userId, String mobile) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("mobile", mobile);
        claims.put("tokenType", "refresh");
        
        return createToken(claims, mobile, refreshTokenExpiration, getRefreshSignKey());
    }

    // Create JWT token
    private String createToken(Map<String, Object> claims, String subject, long expiration, Key signKey) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signKey)
                .compact();
    }

    // Get access token signing key
    public SecretKey getAccessSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(accessTokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Get refresh token signing key
    public SecretKey getRefreshSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(refreshTokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Validate JWT token
    public boolean validateToken(String token, SecretKey signKey) {
        try {
            Jwts.parser().verifyWith(signKey).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // Extract user ID from token
    public String extractUserId(String token, SecretKey signKey) {
        return extractClaim(token, signKey, claims -> claims.get("userId", String.class));
    }

    // Extract phone number from token
    public String extractPhoneNumber(String token, SecretKey signKey) {
        return extractClaim(token, signKey, claims -> claims.get("mobile", String.class));
    }

    // Extract role from token
    public String extractRole(String token, SecretKey signKey) {
        return extractClaim(token, signKey, claims -> claims.get("role", String.class));
    }

    // Extract token type from token
    public String extractTokenType(String token, SecretKey signKey) {
        return extractClaim(token, signKey, claims -> claims.get("tokenType", String.class));
    }

    // Extract expiration date from token
    public Date extractExpiration(String token, SecretKey signKey) {
        return extractClaim(token, signKey, Claims::getExpiration);
    }

    // Extract a specific claim from token
    public <T> T extractClaim(String token, SecretKey signKey, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, signKey);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token, SecretKey signKey) {
        return Jwts.parser()
                .verifyWith(signKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check if token is expired
    public boolean isTokenExpired(String token, SecretKey signKey) {
        try {
            return extractExpiration(token, signKey).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

