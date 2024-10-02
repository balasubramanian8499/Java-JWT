package com.demo.java_jwt.repo;

import com.demo.java_jwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String username);

    Optional<User> findByUserNameAndIsActiveTrue(String email);

}
