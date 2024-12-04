package com.rbac.model.dto.user;

import java.sql.Timestamp;
import com.rbac.model.entity.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Users.UserStatus status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
}
