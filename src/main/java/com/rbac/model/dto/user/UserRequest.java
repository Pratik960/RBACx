package com.rbac.model.dto.user;

import com.rbac.config.security.web.UserAuthorities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Username must not be blank.")
    @Size(min = 6, max = 20, message = "Username length should be between 6 to 20 characters.")
    @Pattern(
        regexp = "^[a-zA-Z0-9_](?!.*?[._]{2})[a-zA-Z0-9._]{4,18}[a-zA-Z0-9_.]$",
        message = "Invalid username"
    )
    @Schema(example = "testuser@12")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-zA-Z]).{8,}$", message = "Minimum 8 characters are required with special character, number")
    @Size(min = 8, max = 20, message = "Minimum 8 characters are required")
    @Schema(example = "testPass@123")
    private String password;

    @NotBlank(message = "User's first name is required.")
    @Size(min = 3, message = "Minimum 3 characters are required")
    @Schema(example = "john")
    private String firstName;

    @NotBlank(message = "User's last name is required.")
    @Size(min = 3, message = "Minimum 3 characters are required")
    @Schema(example = "doe")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]{4,}+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid Email-ID")
    @Schema(example = "user123@gmail.com")
    private String email;
    
    @Schema(example = "ROLE_USER", description = "User's role in the system. ex. ROLE_USER, ROLE_EMP")
    private UserAuthorities role;


    public void setUsername(String username) {
        if (username != null) {
            this.username = username.toLowerCase();
        }
    }

    
}
