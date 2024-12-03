package com.rbac.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.rbac.config.security.web.UserAuthorities;
import com.rbac.model.entity.Users;

public interface UsersDao extends JpaRepository<Users, Integer>, JpaSpecificationExecutor<Users> {
    
    Optional<Users> findByUsernameAndStatus(String username, Users.UserStatus status);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByIdAndStatus(Integer id, Users.UserStatus status);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Users u " +
           "WHERE u.email = :email " +
           "AND u.authorities = :role " +
           "AND u.status = :status")
    boolean existsByEmailAndRoleAndStatus(String email, UserAuthorities role, Users.UserStatus status);
}
