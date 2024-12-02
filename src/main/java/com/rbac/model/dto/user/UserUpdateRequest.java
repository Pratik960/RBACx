package com.rbac.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotNull(message = "User ID is required.")
    @Schema(description = "ID of the user", example = "123")
    private Integer userId;

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

}
