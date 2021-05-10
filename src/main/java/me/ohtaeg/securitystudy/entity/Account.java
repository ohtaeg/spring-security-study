package me.ohtaeg.securitystudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashSet;
import lombok.*;

import javax.persistence.*;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity // 데이터베이스의 테이블과 1:1 매핑되는 객체
@Table(name = "account") // 테이블 명을 user로 지정
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @JsonIgnore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 10, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 100)
    private String password;

    private String role;

    @JsonIgnore // 서버에서 Json 응답을 생성할때 해당 필드는 ignore 하겠다는 의미
    @Column(name = "activated")
    private boolean activated;

    /**
     * User 객체와 권한 객체의 다대다 관계를
     * 일대다, 다대일 관계의 조인 테이블로 정의하겠다는 뜻
     */
    @ManyToMany
    @JoinTable(
            name = "user_authority"
            , joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")}
            , inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities = new HashSet<>();

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
