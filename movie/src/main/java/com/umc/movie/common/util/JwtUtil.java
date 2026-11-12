package com.umc.movie.common.util;

import com.umc.movie.common.exception.ApiException;
import com.umc.movie.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Duration accessExpriation;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") Long accessExpiration
    ){
        // 문자열 비밀 키를 서명용 키 객체로 바꿈
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpriation = Duration.ofSeconds(accessExpiration);
    }

    /*
    * 로그인에 성공한 회원에게 발급할 Access Token을 만든다
    * */
    public String createAccessToken(Long memberId, String email){
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessExpriation)))
                .signWith(secretKey)
                .compact();
    }

    /*
    * 토큰을 검증하고 그 안에 담긴 emberId를 꺼냄
    * 서명이 맞지 않거나 만료된 토큰이면 예외를 던지므로 이 메서드가 값을 돌려줬다면
    * "검증까지 통과한 토큰"임
    * */
    public Long getMemberId(String token){
        try{
            return Long.valueOf(getClaims(token).getPayload().getSubject());
        } catch (ExpiredJwtException e){
            //만료는 사용자가 다시 로그인하면 되니까 따로 구분
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            //만료를 제외한 모든 예외 = 유효하지 않은 토큰
            throw new ApiException(ErrorCode.TOKEN_INVALID);
        }
    }

    /*
    * JWT 인증
    * */
    private Jws<Claims> getClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token);
    }
}
