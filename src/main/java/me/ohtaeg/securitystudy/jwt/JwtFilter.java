package me.ohtaeg.securitystudy.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtProvider jwtProvider;

    public JwtFilter(final JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * 필터링 역할 수행
     * JWT 토큰의 인증 정보를 현재 실행중인 SecurityContext에 저장 로직 수행
     */
    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(request);
        String uri = request.getRequestURI();

        // 정상 토큰이면 SecurityContext 에 저장
        if (StringUtils.hasText(jwt)) {
            final Authentication authentication = jwtProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("input Security Context Authentication : {}, uri : {}", authentication.getName(), uri);
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    /**
     * HttpServletRequest 객체의 Header에서 token을 꺼낸다.
     * @param request - 요청
     * @return
     */
    private String resolveToken(final HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); //Bearer 를 제외한 나머지 값들을 추출
        }

        // throw new IllegalArgumentException("잘못된 토큰");
        return null;
    }
}
