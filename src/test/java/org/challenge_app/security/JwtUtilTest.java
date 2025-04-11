package org.challenge_app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtUtil.generateToken("testuser");

        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // Tokens JWT suelen comenzar así
    }

    @Test
    void validateToken_shouldReturnTrue_forValidToken() {
        String token = jwtUtil.generateToken("testuser");

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void extractUsername_shouldReturnCorrectUser() {
        String token = jwtUtil.generateToken("admin");

        String username = jwtUtil.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    void validateToken_shouldReturnFalse_forInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_forExpiredToken() throws InterruptedException {
        // Token simulado con expiración manual (muy corto)
        JwtUtil shortLivedUtil = new JwtUtil() {
            @Override
            public String generateToken(String username) {
                return io.jsonwebtoken.Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new java.util.Date())
                        .setExpiration(new java.util.Date(System.currentTimeMillis() + 100)) // 100 ms
                        .signWith(this.key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                        .compact();
            }
        };

        String token = shortLivedUtil.generateToken("expireduser");

        // Esperamos que expire
        Thread.sleep(200);

        assertFalse(shortLivedUtil.validateToken(token));
    }
}