package me.ohtaeg.securitystudy.config;

import me.ohtaeg.securitystudy.jwt.JwtFilter;
import me.ohtaeg.securitystudy.jwt.JwtProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JwtProvier, JwtFilter를 SecurityConfig에 적용할 때 사용할 Config 클래스
 * SecurityConfigurerAdapter를 통해 JwtProvider를 주입받아서 JwtFilter를 통해 Security 로직에 필터를 등록
 */
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtProvider jwtProvider;

    public JwtSecurityConfig(final JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    // configure 메소드를 오버라이드하여 만든 JwtFilter 를 Security 로직에 적용
    @Override
    public void configure(final HttpSecurity builder) {
        JwtFilter filter = new JwtFilter(jwtProvider);
        builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
