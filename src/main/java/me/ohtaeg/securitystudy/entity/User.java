package me.ohtaeg.securitystudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity // 데이터베이스의 테이블과 1:1 매핑되는 객체
@Table(name = "user") // 테이블 명을 user로 지정
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @JsonIgnore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 10,  unique = true)
    private String userName;

    @JsonIgnore
    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 10)
    private String nickname;

    @JsonIgnore
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
    private Set<Authority> authorities;
}
