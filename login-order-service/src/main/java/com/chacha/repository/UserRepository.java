package com.chacha.repository;

import com.chacha.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // DB에 회원정보를 요청할 때 쿼리로 바꿔서 생성.
    Optional<User> findByEmail(String email);
}
