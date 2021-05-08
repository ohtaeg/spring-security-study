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
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // 기본적인 Web 보안 설정을 활성화 하겠다는 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메소드 단위로 @PreAuthorize 검증 어노테이션을 사용하기 위해 추가
/**
 * 추가적인 설정을 위해 WebSecurityConfigurer 를 implement 하거나
 * WebSecurityConfigurerAdapter 를 extends 하는 방법이 있다.
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(final JwtProvider jwtProvider, final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, final JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtProvider = jwtProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * h2 콘솔 하위 모든 요청들과 파비콘 관련 요청은 Spring Security 인증을 수행하지 않도록 오버라이드
     * h2-console/**, favicon 에 대한 요청은 Security filter chain 을 적용할 필요가 전혀 없는 요청이여서
     * Spring Security 로직을 수행하지 않고 직접 접근이 가능하도록 ignoring
     */
    @Override
    public void configure(final WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        , "favicon.ico")
        ;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 토큰을 사용하기 때문에 csrf 는 disable 로 설정

                // Exception 을 핸들링할 때 만들었던 jwt 에러 핸들링을 추가
                // JwtAuthenticationEntryPoint - 401, JwtAccessDeniedHandler - 403
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 데이터 확인을 위해 사용하고 있는 h2 - console을 위한 설정 추가
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세선을 사용하지 않기에 세션 설정을 Stateless로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests() // HttpServletRequest 를 사용하는 요청들에 대해 접근제한을 설정하겠다는 의미
                .antMatchers("/hello", "/info").permitAll() // "path" 에 대한 요청은 인증없이 접근을 허용하겠다는 의미
                .antMatchers("/admin").hasRole("ADMIN")
                // 로그인 api 와 회원가입 api 는 토큰이 없는 상태에서 요청이 들어오기 때문에 토큰 없이 접근을 허용
                .antMatchers("/apis/authenticate").permitAll() // 로그인 api
                .antMatchers(HttpMethod.POST, "/apis/users").permitAll() // 회원가입 api
                .anyRequest().authenticated() // 나머지 요청들은 모두 인증만 하면 접근을 가능하게 해준다는 의미

                // 폼 로그인을 사용할 것이다.
                .and()
                .formLogin()

                .and()
                .httpBasic()

                .and()
                .apply(new JwtSecurityConfig(jwtProvider)) // 커스텀 필터 등록
        ;
    }

    /**
     * 유저 정보를 여러개를 임의로 설정하고 싶은 경우
     */
//    @Override
//    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//            .withUser("ohtaeg").password("{noop}123").roles("USER") // 시큐리티 5 버전부터 제공되는 기본 패스워드 인코더가 있는데
//                                                                             // 해당 문자열 양식으로 비밀번호가 어떤 인코딩 방식인지 알려주면
//                                                                             // prefix에 해당하는 방법에 맞게 암호화 및 인코딩을 해준다.
//                                                                             // {noop}은 암호화를 하지 않았다는 뜻
//            .and()
//            .withUser("admin").password("{noop}1234").roles("ADMIN")
//        ;
//    }
}

