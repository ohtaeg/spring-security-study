package me.ohtaeg.securitystudy.service;

import me.ohtaeg.securitystudy.entity.User;
import me.ohtaeg.securitystudy.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 로그인시 DB에서 findByUsername() 통해 유저 정보와 권한 정보를 가져오고,
     * 해당 정보들을 기반으로 UserDetails.User 객체를 생성해서 리턴한다.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                             .map(user -> createUser(username, user))
                             .orElseThrow(() -> new UsernameNotFoundException(username + "를 찾을 수 없습니다."));
    }

    private org.springframework.security.core.userdetails.User createUser(final String username, final User user) {
        if (!user.isActivated()) {
            throw new RuntimeException(username + "이 활성화되어 있지 않습니다.");
        }

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities()
                                                        .stream()
                                                        .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                                                        .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
