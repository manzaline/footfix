package com.footfix.security.repository;

import com.footfix.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// CRUD함수들을 자동으로 지원
// JpaRepository인터페이스가 가지고있는 @Repository를 그대로 상속받아 IoC가 됨.
public interface UserRepository extends JpaRepository<User, Long> {

  // findBy규칙 => Username문법
  // select * from users where username=1?
  Optional<User> findByUsername(String username);
}
