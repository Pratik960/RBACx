package com.rbac.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String refreshToken;
    private String userRole;
    private Integer userId;
}
