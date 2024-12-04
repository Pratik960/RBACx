package com.rbac.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    @NotBlank(message = "refresh token must not be blank.")
    @Schema(example = "testrefreshtoken", description = "refresh token of user")
    private String refreshToken;
}
