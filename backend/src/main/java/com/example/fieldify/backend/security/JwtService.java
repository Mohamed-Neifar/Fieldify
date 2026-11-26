package com.example.fieldify.backend.security;

import com.example.fieldify.backend.config.AppConfig;
import com.example.fieldify.backend.model.Role;
import com.example.fieldify.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtService {
    private final SecretKey secretKey;
    private final int expirationMinutes;

    public JwtService(AppConfig config) {
        this.secretKey = Keys.hmacShaKeyFor(config.getJwtSecret().getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = config.getJwtExpirationMinutes();
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (expirationMinutes * 60L * 1000L));

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        if (token == null || token.trim().isEmpty()) return null;
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        if (claims == null) return null;
        return UUID.fromString(claims.getSubject());
    }

    public Role getUserRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        if (claims == null) return null;
        String roleStr = claims.get("role", String.class);
        return roleStr != null ? Role.valueOf(roleStr) : null;
    }
}
