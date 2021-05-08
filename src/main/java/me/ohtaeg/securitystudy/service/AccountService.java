package me.ohtaeg.securitystudy.service;

import java.util.Collections;
import java.util.Optional;
import me.ohtaeg.securitystudy.controller.dto.AccountRequest;
import me.ohtaeg.securitystudy.entity.Authority;
import me.ohtaeg.securitystudy.entity.Account;
import me.ohtaeg.securitystudy.jwt.util.SecurityUtil;
import me.ohtaeg.securitystudy.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입, 유저 정보 조회 서비스
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(final AccountRepository accountRepository, final PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Account signup(AccountRequest accountRequest) {
        // 이미 같은 username 으로 가입된 유저가 있는 지 확인
        if (findOneWithAuthoritiesByUsername(accountRequest).isPresent()) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 유저 권한 생성
        Authority authority = Authority.builder()
            .authorityName("ROLE_USER")
            .build();

        // 유저 권한 주입 및 유저 객체 생성
        Account user = Account.builder()
                              .username(accountRequest.getUsername())
                              .password(passwordEncoder.encode(accountRequest.getPassword()))
                              .authorities(Collections.singleton(authority))
                              .activated(true)
                              .build();

        return accountRepository.save(user);
    }

    /**
     * findOne / With
     * findOne 은  Returns a single entity 의 의미
     * With 는 @EntityGraph 어노테이션과 관계가 있다. authorities도 함께 Fetch 하라는 의미
     */
    private Optional<Account> findOneWithAuthoritiesByUsername(final AccountRequest accountRequest) {
        return accountRepository.findOneWithAuthoritiesByUsername(accountRequest.getUsername());
    }

    @Transactional(readOnly = true)
    public Optional<Account> getUserWithAuthorities(String username) {
        return accountRepository.findOneWithAuthoritiesByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Account> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(accountRepository::findOneWithAuthoritiesByUsername);
    }
}
