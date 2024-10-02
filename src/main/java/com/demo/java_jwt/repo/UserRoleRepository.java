package com.demo.java_jwt.repo;

import com.demo.java_jwt.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {

    List<UserRole> findByUserId(Integer id);

}

