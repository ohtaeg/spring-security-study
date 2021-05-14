package me.ohtaeg.securitystudy.config;

import me.ohtaeg.securitystudy.jwt.exception.JwtAccessDeniedHandler;
import me.ohtaeg.securitystudy.jwt.exception.JwtAuthenticationEntryPoint;
import me.ohtaeg.securitystudy.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // 기본적인 Web 보안 설정을 활성화 하겠다는 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메소드 단위로 @PreAuthorize 검증 어노테이션을 사용하기 위해 추가
/**
 * 추가적인 설정을 위해 WebSecurityConfigurer 를 implement 하거나
 * WebSecurityConfigurerAdapter 를 extends 하는 방법이 있다.
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    /**
     * h2 콘솔 하위 모든 요청들과 파비콘 관련 요청은 Spring Security 인증을 수행하지 않도록 오버라이드
     * Spring Security 로직을 수행하지 않고 직접 접근이 가능하도록 ignoring
     */
    @Override
    public void configure(final WebSecurity web) {
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .mvcMatchers("/", "/info", "/apis/users/account/**")
            .permitAll()
            .mvcMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated();

        http.formLogin();
        http.httpBasic(); // 헤더에 특정한 값을 넣어서 요청을 보내면 그 값을 서버가 받아 ( 헤더에 이런 값이 들어올거라는 약속이 되어있기에 )
                          // 서버가 유저의 이름과 패스워드를 알아낼 수 있다.
                          // 해당 방법은 위험하기때문에 HTTPS에서만 사용해야한다.
                          // formLogin 도 사실 마찬가지
    }

    /**
     * 유저 정보를 여러개를 임의로 설정하고 싶은 경우
     */
//    @Override
//    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//            .withUser("ohtaeg").password("{noop}123").roles("USER") // 시큐리티 5 버전부터 제공되는 기본 패스워드 인코더가 있는데
//                                                                    // 해당 문자열 양식 {encoderId}password으로 비밀번호가 어떤 인코딩 방식인지 알려주면
//                                                                    // prefix에 해당하는 방법에 맞게 암호화 및 인코딩을 해준다.
//                                                                    // {noop}은 암호화를 하지 않았다는 뜻
//                                                                    // 이전에는 noop이 기본 전략이였는데 BCrypt로 바뀜
//            .and()
//            .withUser("admin").password("{noop}1234").roles("ADMIN")
//        ;
//    }
}

