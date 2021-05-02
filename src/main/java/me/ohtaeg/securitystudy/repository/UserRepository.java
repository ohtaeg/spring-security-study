package me.ohtaeg.securitystudy.repository;

import me.ohtaeg.securitystudy.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities")
        // 해당 쿼리가 수행될때 Lazy 조회가 아닌 Eager 조회로 authorities 정보를 조인해서 가져오도록
    Optional<User> findByUsername(final String username); // user 정보를 조회할 때 권한 정보도 같이 가져오는 메서드
}
