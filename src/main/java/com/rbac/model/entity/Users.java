package com.rbac.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.rbac.config.security.web.UserAuthorities;

import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "UniqueUsername", columnNames = "username"),
        @UniqueConstraint(name = "UniqueUsernameEmailStatus", columnNames = {"username", "email", "status"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int(11) COMMENT 'Unique identifier for the user within RBACx system'")
    private Integer id;

    @Column(name = "username", nullable = false, length = 255, columnDefinition = "varchar(255) COMMENT 'Users''s chosen username or identifier for logging in'")
    private String username;

    @Column(name = "password", nullable = false, length = 255, columnDefinition = "varchar(255) COMMENT 'Users''s chosen password for logging in'")
    private String password;

    @Column(name = "first_name", length = 255, columnDefinition = "varchar(255) COMMENT 'Users''s first name'")
    private String firstName;

    @Column(name = "last_name", length = 255, columnDefinition = "varchar(255) COMMENT 'Users''s last name'")
    private String lastName;

    @Column(name = "email", nullable = false, length = 255, columnDefinition = "varchar(255) COMMENT 'Users''s email address, used for communication.'")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('ACTIVE','INACTIVE','SUSPENDED') DEFAULT 'INACTIVE' COMMENT 'Current status of the user account (e.g., Active, Inactive, Suspended)'")
    private UserStatus status;

    @Column(name = "role", nullable = false, columnDefinition = "ENUM('ROLE_ADMIN', 'ROLE_USER', 'ROLE_EMP') DEFAULT 'ROLE_USER' COMMENT 'Represents authorities given to the user'")
    @Enumerated(EnumType.STRING)
    private UserAuthorities authorities; 

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date and time the user was created'")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Date and time the user was last updated'")
    private Timestamp updatedAt;

    public enum UserStatus{
        ACTIVE,
        INACTIVE,
        SUSPENDED
    } 
    
}
