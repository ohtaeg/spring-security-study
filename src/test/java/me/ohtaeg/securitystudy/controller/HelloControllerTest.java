package me.ohtaeg.securitystudy.controller;

import me.ohtaeg.securitystudy.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(HelloController.class)
// class HelloControllerTest {

    // TODO 시큐리티 설정이 필요없는 api는 어떻게 테스트 할 수 있는지 찾아볼 것
//    @Autowired
//    MockMvc mockMvc;
//
//    @Test
//    public void hello() throws Exception {
//        mockMvc.perform(get("/apis/hello"))
//               .andExpect(status().isOk())
//               .andDo(print());
//    }
// }
