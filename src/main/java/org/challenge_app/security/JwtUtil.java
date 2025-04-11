package org.challenge_app.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;



@Component
public class JwtUtil {

    private final String SECRET_KEY = "mi_super_clave_hiper_segura_12345678";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    private final JwtParser jwtParser = Jwts.parser()  // en 0.12.x se construye así
            .verifyWith((SecretKey) key)
            .build();

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return jwtParser.parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token); // Si falla, lanza excepción
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}