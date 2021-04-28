package me.ohtaeg.securitystudy.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 토큰의 생성, 토큰의 유효성 검증등을 담당할 Token Provider
 * JwtProvider 빈은 application.properties 에서 정의한 jwt.secret-key, jwt.expire-seconds 값을 주입 받도록 한다.
 *
 * InitializingBean을 구현하고 afterPropertiesSet()을 재정의한 이유
 * -> 빈이 생성되고 의존성 주입까지 끝낸 이후에 주입받은 Secret-key 값을 decode 하여 key 변수에 할당히가 위함
 */
@Component
// public class JwtProvider implements InitializingBean {
public class JwtProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secretKey;
    private final long expireSeconds;
    private final Key key;

    public JwtProvider(@Value("${jwt.secret-key}") final String secretKey, @Value("${jwt.expire-seconds}") final long expireSeconds) {
        this.secretKey = secretKey;
        this.expireSeconds = expireSeconds * 1000;
        this.key = Keys.hmacShaKeyFor(decode(secretKey));
    }

    private byte[] decode(final String key) {
        return Decoders.BASE64.decode(secretKey);
    }
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        final byte[] bytes = Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(bytes);
//    }

    /**
     * Authentication 객체에 포함되어 있는 권한 정보들(authorities)을 토큰에 담고 현재 시간에 맞게 만료시간 설정 후 토큰 생성
     */
    public String createToken(final Authentication authentication) {
        final String authorities = getAuthorities(authentication);
        final Date expiration = new Date(getCurrentTimeMilliSeconds() + this.expireSeconds);
        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .claim(AUTHORITIES_KEY, authorities)
                   .signWith(key, SignatureAlgorithm.HS512)
                   .setExpiration(expiration)
                   .compact();
    }

    private String getAuthorities(final Authentication authentication) {
        return authentication.getAuthorities().stream()
                             .map(GrantedAuthority::getAuthority)
                             .collect(Collectors.joining(","));
    }

    private long getCurrentTimeMilliSeconds() {
        return LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
    }

}
