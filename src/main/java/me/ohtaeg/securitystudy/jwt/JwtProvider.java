package me.ohtaeg.securitystudy.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
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
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    public static final String SPLIT_DELIMIT = ",";
    private static final String AUTHORITIES_KEY = "auth";

    private final String secretKey;
    private final long expireSeconds;
    private final Key key;

    public JwtProvider(@Value("${jwt.secret-key}") final String secretKey, @Value("${jwt.expire-seconds}") final long expireSeconds) {
        this.secretKey = secretKey;
        this.expireSeconds = expireSeconds * 1000;
        this.key = Keys.hmacShaKeyFor(decode(secretKey));
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        final byte[] bytes = Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(bytes);
//    }

    private byte[] decode(final String key) {
        return Decoders.BASE64.decode(key);
    }

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

    /**
     * JWT 에 담겨있는 권한 정보들을 이용해 Authentication 객체를 리턴
     * @param token - jwt
     * @return
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // User 객체를 만들어주기 위해 클레임에서 권한정보들을 가져온다.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(SPLIT_DELIMIT))
                      .map(SimpleGrantedAuthority::new)
                      .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰을 이용해서 클레임으로 만든다.
     * @param token - jwt
     * @return
     */
    private Claims getClaims(final String token) {
        validateToken(token);
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    /**
     * 토큰의 유효성 검증을 수행하는 validateToken 메서드
     */
    private boolean validateToken(String token) {
        try {
            this.getClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT");
        } catch (UnsupportedJwtException e) {
            logger.info("지원하지 않는 JWT");
        } catch (IllegalArgumentException e) {
            logger.info("잘못된 JWT");
        }
        return false;
    }
}
