package com.assessment.shipment_tracking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey secretKey;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("security.jwt.secret must be at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public AuthenticatedTenant parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String tenantId = claims.get("tenant_id", String.class);
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("JWT missing tenant_id claim");
        }
        return new AuthenticatedTenant(tenantId, claims.getSubject());
    }

    public String createToken(String subject, String tenantId, Instant expiresAt) {
        return Jwts.builder()
                .subject(subject)
                .claim("tenant_id", tenantId)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }
}
