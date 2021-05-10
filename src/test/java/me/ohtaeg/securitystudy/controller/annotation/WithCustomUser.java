package me.ohtaeg.securitystudy.controller.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithMockUser;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "ohtaeg", roles = "ADMIN")
public @interface WithCustomUser {

}
