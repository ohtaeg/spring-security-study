package me.ohtaeg.securitystudy.controller;

import me.ohtaeg.securitystudy.controller.dto.LoginDto;
import me.ohtaeg.securitystudy.controller.dto.TokenResponseDto;
import me.ohtaeg.securitystudy.jwt.JwtFilter;
import me.ohtaeg.securitystudy.jwt.JwtProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/apis/authenticate")
public class AuthController {
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(final JwtProvider jwtProvider, final AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.jwtProvider = jwtProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping
    public ResponseEntity<TokenResponseDto> authorize(@RequestBody @Valid LoginDto loginDto) {
        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        final Authentication authentication = authenticationManagerBuilder.getObject()
                                                                          // usernamePasswordAuthenticationToken 을 이용해서 Authentication 객체를 생성하기 위해 .authenticate() 메서드가 실행될 때
                                                                          // CustomUserDetailsService 의 loadUserByUsername 메서드가 실행된다.
                                                                          // 유저 정보를 조회해서 인증 정보(Authentication)를 생성하게 된다.
                                                                          .authenticate(usernamePasswordAuthenticationToken);

        // 해당 인증 정보를 JwtFilter 클래스의 doFilter 메소드와 유사하게 현재 실행중인 스레드 ( Security Context ) 에 저장
        SecurityContextHolder.getContext()
                             .setAuthentication(authentication);

        // 인증 정보를 기준으로 토큰 생성
        String jwt = jwtProvider.createToken(authentication);

        // 토큰을 Response Header 에 넣어주고 Response Body 에도 넣어서 리턴
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenResponseDto(jwt), headers, HttpStatus.OK);
    }
}
