package com.rbac.model.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id", nullable = false, columnDefinition = "int(11) COMMENT 'Unique identifier for the refresh token RBACx system'")
    private Integer tokenId;

    @Column(name = "refresh_token", nullable = false, unique = true, columnDefinition = "VARCHAR(255) COMMENT 'Refresh token string'")
    private String refreshToken;

    @Column(name = "expiry", nullable = false, columnDefinition = "TIMESTAMP COMMENT 'Expiration time of the refresh token'")
    private Instant expiry;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "INT(11) COMMENT 'Foreign key to Users table'")
    private Users user;
}
