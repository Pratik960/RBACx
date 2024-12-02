package com.rbac.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserAuthenticateRequest {
    
    @NotBlank(message = "Username must not be blank.")
    @Size(min = 6, max = 20, message = "Username length should be between 6 to 20")
    @Schema(example = "testuser")
    private String username;
    
    @Size(min = 8, max = 20, message = "Minimum 8 characters are required")
    @Schema(example = "testPass@123")
    private String password;
}
