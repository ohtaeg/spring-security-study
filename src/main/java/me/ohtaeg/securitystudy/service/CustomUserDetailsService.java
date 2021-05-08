package me.ohtaeg.securitystudy.service;

import me.ohtaeg.securitystudy.entity.Account;
import me.ohtaeg.securitystudy.repository.AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
// Data Access Object 를 사용해서 저장소에 들어있는 유저 정보를 가지고 인증할때 사용하는 인터페이스
// 우리가 사용하는 유저가 저장소에 저장될 것이기에 해당 UserDetailsService 인터페이스를 구현한다.
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 로그인시 DB에서 findByUsername() 통해 유저 정보와 권한 정보를 가져오고,
     * 해당 정보들을 기반으로 UserDetails.User 객체를 생성해서 리턴한다.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username)
                                .map(account -> createUser(username, account))
                                .orElseThrow(() -> new UsernameNotFoundException(username + "를 찾을 수 없습니다."));
    }

    private User createUser(final String username, final Account account) {
        if (!account.isActivated()) {
            throw new RuntimeException(username + "이 활성화되어 있지 않습니다.");
        }

        List<GrantedAuthority> grantedAuthorities = account.getAuthorities()
                                                           .stream()
                                                           .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                                                           .collect(Collectors.toList());

        return new User(account.getUsername(), account.getPassword(), grantedAuthorities);
//        return User.builder()
//                   .username(account.getUsername())
//                   .password(account.getPassword())
//                   .roles(account.getRole())
//                   .authorities(grantedAuthorities)
//                   .build();
    }
}
