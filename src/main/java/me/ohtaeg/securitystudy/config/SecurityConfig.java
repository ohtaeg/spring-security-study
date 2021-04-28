package me.ohtaeg.securitystudy.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity // 기본적인 Web 보안 설정을 활성화 하겠다는 어노테이션
/**
 * 추가적인 설정을 위해 WebSecurityConfigurer 를 implement 하거나
 * WebSecurityConfigurerAdapter 를 extends 하는 방법이 있다.
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests() // HttpServletRequest 를 사용하는 요청들에 대해 접근제한을 설정하겠다는 의미
            .antMatchers("/apis/hello").permitAll() // "path" 에 대한 요청은 인증없이 접근을 허용하겠다는 의미
            .anyRequest().authenticated() // 나머지 요청들은 모두 인증하겠다는 의미
        ;
    }

    /**
     * h2 콘솔 하위 모든 요청들과 파비콘 관련 요청은 Spring Security 인증을 수행하지 않도록 오버라이드
     * h2-console/**, favicon 에 대한 요청은 Security filter chain 을 적용할 필요가 전혀 없는 요청이여서
     * Spring Security 로직을 수행하지 않고 직접 접근이 가능하도록 ignoring
     */
    @Override
    public void configure(final WebSecurity web) throws Exception {
        web
           .ignoring()
           .antMatchers(
                   "/h2-console/**"
                   , "favicon.ico")
        ;
    }
}

