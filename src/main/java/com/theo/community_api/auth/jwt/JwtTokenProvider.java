package com.theo.community_api.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider { // 토큰 검증

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    // JwtToken 생성자
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtProperties.secret())
        );
    }

    public String createAccessToken(Long userId) {
        return createToken(
                userId,
                ACCESS_TOKEN_TYPE,
                jwtProperties.accessTokenExpiration()
        );
    }

    public String createRefreshToken(Long userId) {
        return createToken(
                userId,
                REFRESH_TOKEN_TYPE,
                jwtProperties.refreshTokenExpiration()
        );
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public boolean isAccessToken(Claims claims) {
        return ACCESS_TOKEN_TYPE.equals(
                claims.get(TOKEN_TYPE_CLAIM, String.class)
        );
    }

    public boolean isRefreshToken(Claims claims) {
        return REFRESH_TOKEN_TYPE.equals(
                claims.get(TOKEN_TYPE_CLAIM, String.class)
        );
    }

    private String createToken(
            Long userId,
            String tokenType,
            long expirationMillis
    ) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(expirationMillis);

        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // 겹치지않는 토큰 발급 목적
                .issuer(jwtProperties.issuer()) // 토큰 발급자 (서버명)
                .subject(userId.toString()) // 사용자 ID
                .claim(TOKEN_TYPE_CLAIM, tokenType) // access or refresh
                .issuedAt(Date.from(issuedAt)) // 발급 시각
                .expiration(Date.from(expiresAt)) // 만료 시각
                .signWith(secretKey, Jwts.SIG.HS256) // 서버 비밀키로 서명
                .compact(); // 최종 JWT 문자열 생성 과정
    }
}
