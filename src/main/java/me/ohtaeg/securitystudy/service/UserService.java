package me.ohtaeg.securitystudy.service;

import java.util.Collections;
import java.util.Optional;
import me.ohtaeg.securitystudy.controller.dto.UserRequest;
import me.ohtaeg.securitystudy.entity.Authority;
import me.ohtaeg.securitystudy.entity.User;
import me.ohtaeg.securitystudy.jwt.util.SecurityUtil;
import me.ohtaeg.securitystudy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입, 유저 정보 조회 서비스
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(UserRequest userRequest) {
        // 이미 같은 username 으로 가입된 유저가 있는 지 확인
        if (findOneWithAuthoritiesByUsername(userRequest).isPresent()) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 유저 권한 생성
        Authority authority = Authority.builder()
            .authorityName("ROLE_USER")
            .build();

        // 유저 권한 주입 및 유저 객체 생성
        User user = User.builder()
            .username(userRequest.getUsername())
            .password(passwordEncoder.encode(userRequest.getPassword()))
            .nickname(userRequest.getNickname())
            .authorities(Collections.singleton(authority))
            .activated(true)
            .build();

        return userRepository.save(user);
    }

    /**
     * findOne / With
     * findOne 은  Returns a single entity 의 의미
     * With 는 @EntityGraph 어노테이션과 관계가 있다. authorities도 함께 Fetch 하라는 의미
     */
    private Optional<User> findOneWithAuthoritiesByUsername(final UserRequest userRequest) {
        return userRepository.findOneWithAuthoritiesByUsername(userRequest.getUsername());
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}
