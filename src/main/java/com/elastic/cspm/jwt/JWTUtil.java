package com.elastic.cspm.jwt;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secretKey}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            // JWT 파싱 및 검증
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            // 만료 시간 확인
            Date expiration = claimsJws.getPayload().getExpiration();
            return expiration.before(new Date()); // 만료 여부 확인
        } catch (ExpiredJwtException e) {
            // 만료된 토큰일 경우
            return true; // 만료된 경우 true 반환
        } catch (JwtException | IllegalArgumentException e) {
            // 다른 오류 처리 (형식 오류 등)
            throw new RuntimeException("Invalid JWT token");
        }
    }

    public String createJwt(String username, String role, Long expiredMs) {

        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + expiredMs);

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String username, String role, Long expiredMs) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + expiredMs);

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
}
