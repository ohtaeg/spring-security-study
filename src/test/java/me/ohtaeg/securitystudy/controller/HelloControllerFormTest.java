package me.ohtaeg.securitystudy.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import me.ohtaeg.securitystudy.entity.Account;
import me.ohtaeg.securitystudy.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class HelloControllerFormTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Test
    @Transactional // 데이터 변경사항을 디비에 반영하지 않는다. 즉 테스트시 롤백 트랜잭션으로 독립적인 테스트를 수행할 수 있도록 해준다.
    public void login_success() throws Exception {
        final String username = "ohtaeg";
        final String password = "123";
        createUser(username, password);

        mockMvc.perform(formLogin().user(username).password(password)) // 위 유저로 폼 로그인을 한다면
               .andExpect(authenticated()) // 인증이 되는지 기대한다.
               .andDo(print())
        ;
    }

    @Test
    @Transactional
    public void login_fail() throws Exception {
        final String username = "ohtaeg";
        final String password = "123";
        createUser(username, password);

        mockMvc.perform(formLogin().user("taetae").password("12345")) // 위 유저로 폼 로그인을 한다면
               .andExpect(unauthenticated()) // 인증이 되지 않는 것을 기대한다.
               .andDo(print())
        ;

        /**
         * unauthenticated()는 UnAuthenticatedMatcher를 반환하고
         * UnAuthenticatedMatcher의 내부 로직을 확인해보면 시큐리티 컨텍스트에서 인증된 로그인을 꺼내는 것을 확인할 수 있다.
         */
    }

    private Account createUser(final String username, final String password) {
        final Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole("USER");
        accountService.createNew(account);
        return account;
    }
}