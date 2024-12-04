package com.rbac.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rbac.model.entity.RefreshToken;
import com.rbac.model.entity.Users;

public interface RefreshTokenDao extends JpaRepository<RefreshToken, String> {
 
    Optional<RefreshToken> findByUser(Users users);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
