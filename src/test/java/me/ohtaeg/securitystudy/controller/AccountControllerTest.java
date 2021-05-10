package me.ohtaeg.securitystudy.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc // MockMvc 가능
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("인덱스 페이지를 누구나 접근이 가능하다.")
    @Test
    public void index_anonymous() throws Exception {
        mockMvc.perform(get("/").with(anonymous()))
               .andExpect(status().isOk())
               .andDo(print());
    }

    @DisplayName("인덱스 페이지를 누구나 접근이 가능하다. / 어노테이션 테스트")
    @WithAnonymousUser
    @Test
    public void index_anonymous_annotation() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andDo(print());
    }

    @DisplayName("특정한 유저도 접근이 가능하다.")
    @Test
    public void index_user() throws Exception {
        mockMvc.perform(get("/").with(user("taeg").roles("USER"))) // 가짜 유저가 로그인 한 상태라는 것, 로그인 한것 처럼 목킹된 것
               .andExpect(status().isOk())
               .andDo(print());
    }

    @DisplayName("특정한 유저도 접근이 가능하다. / 어노테이션 테스트")
    @WithMockUser(username = "taeg", roles = "USER")
    @Test
    public void index_user_annotation() throws Exception {
        mockMvc.perform(get("/")) // 가짜 유저가 로그인 한 상태라는 것, 로그인 한것 처럼 목킹된 것
               .andExpect(status().isOk())
               .andDo(print());
    }

    @DisplayName("특정 유저가 어드민 페이지에 접근이 불가능하다.")
    @Test
    public void admin_user() throws Exception {
        mockMvc.perform(get("/admin").with(user("taeg").roles("USER"))) // 가짜 유저가 로그인 한 상태라는 것, 로그인 한것 처럼 목킹된 것
               .andExpect(status().isForbidden())
               .andDo(print());
    }

    @DisplayName("특정 유저가 어드민 페이지에 접근이 불가능하다. / 어노테이션 테스트")
    @WithMockUser(username = "ohtaeg", roles = "USER")
    @Test
    public void admin_user_annotation() throws Exception {
        mockMvc.perform(get("/admin"))
               .andExpect(status().isForbidden())
               .andDo(print());
    }

    @DisplayName("어드민이 어드민 페이지에 접근이 불가능하다.")
    @Test
    public void admin_admin() throws Exception {
        mockMvc.perform(get("/admin").with(user("taeg").roles("ADMIN"))) // 가짜 유저가 로그인 한 상태라는 것, 로그인 한것 처럼 목킹된 것
               .andExpect(status().isOk())
               .andDo(print());
    }

    @DisplayName("어드민이 어드민 페이지에 접근이 불가능하다. / 어노테이션")
    @WithMockUser(username = "ohtaeg", roles = "ADMIN")
    @Test
    public void admin_admin_annotation() throws Exception {
        mockMvc.perform(get("/admin"))
               .andExpect(status().isOk())
               .andDo(print());
    }

    @DisplayName("어드민이 어드민 페이지에 접근이 불가능하다. / 커스텀 어노테이션")
    @WithCustomUser
    @Test
    public void admin_admin_custom_annotation() throws Exception {
        mockMvc.perform(get("/admin"))
               .andExpect(status().isOk())
               .andDo(print());
    }
}