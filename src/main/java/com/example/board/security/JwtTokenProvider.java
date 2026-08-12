package com.example.board.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 발급과 검증을 전담하는 클래스.
 * - generateToken: 로그인 성공 시 토큰 발급
 * - validateToken / getUsername: 요청이 들어올 때마다 토큰 검사
 */
@Slf4j
@Component //기본 컴포넌트(controller,service,mapper 등)
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey; //설정에서 비밀키를 가져온다.

    @Value("${jwt.expiration}")
    private long expirationMs; //설정에서 토큰의 만료시간을 가져온다.

    private SecretKey key;

    @PostConstruct
    protected void init() {
        // application.yml의 jwt.secret 문자열을 HS256용 키로 변환
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    // 토큰을 생성한다. (유저네임, 권한)
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    // 토큰에서 유저네임(id)를 리턴
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }
    // 토큰에서 권한(role)를 리턴
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * 토큰이 유효한지 검사한다. (서명 위조, 만료, 형식 오류 등을 모두 걸러냄)
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.info("형식이 올바르지 않은 JWT 토큰입니다.");
        } catch (SignatureException e) {
            log.info("JWT 서명이 유효하지 않습니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 비어있습니다.");
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
