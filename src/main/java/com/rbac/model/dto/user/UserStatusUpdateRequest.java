package com.rbac.model.dto.user;

import com.rbac.model.entity.Users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusUpdateRequest {
    
    @Schema(example = "1")
    private Integer userId;

    @Schema(example = "ACTIVE")
    private Users.UserStatus status;
}
