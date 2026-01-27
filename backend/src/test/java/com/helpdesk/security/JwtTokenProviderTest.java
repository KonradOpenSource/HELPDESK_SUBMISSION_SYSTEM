package com.helpdesk.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String secretKey = "mySecretKeyForTesting12345678901234567890";
    private int expirationInMs = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secretKey);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", expirationInMs);
    }

    @Test
    void generateTokenFromUsername_Success() {
        String username = "testuser";
        String token = jwtTokenProvider.generateTokenFromUsername(username);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_Success() {
        String username = "testuser";
        String token = jwtTokenProvider.generateTokenFromUsername(username);

        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String username = "testuser";
        String token = jwtTokenProvider.generateTokenFromUsername(username);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        String username = "testuser";
        
        // Create expired token by setting expiration in the past
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        String expiredToken = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis() - 1000))
                .expiration(new Date(System.currentTimeMillis() - 500))
                .signWith(key)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void getUsernameFromToken_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> jwtTokenProvider.getUsernameFromToken(invalidToken));
    }

    @Test
    void generateTokenFromUsername_CreatesTokenWithCorrectSubject() {
        String username = "testuser";
        String token = jwtTokenProvider.generateTokenFromUsername(username);

        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void generateTokenFromUsername_CreatesTokenWithCorrectExpiration() {
        String username = "testuser";
        long beforeGeneration = System.currentTimeMillis();
        String token = jwtTokenProvider.generateTokenFromUsername(username);
        long afterGeneration = System.currentTimeMillis();

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Date expiration = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        long expectedMinExpiration = beforeGeneration + expirationInMs;
        long expectedMaxExpiration = afterGeneration + expirationInMs;

        assertTrue(expiration.getTime() >= expectedMinExpiration);
        assertTrue(expiration.getTime() <= expectedMaxExpiration);
    }
}
