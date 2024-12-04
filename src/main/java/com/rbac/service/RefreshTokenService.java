package com.rbac.service;

import com.rbac.model.dto.auth.LoginResponse;
import com.rbac.model.dto.auth.RefreshTokenRequest;
import com.rbac.model.entity.RefreshToken;
import com.rbac.util.http.response.SuccessResponse;

public interface RefreshTokenService {
    
    RefreshToken createRefreshToken(String username);

    RefreshToken verifyRefreshToken(String refreshToken);

    SuccessResponse<LoginResponse> refreshToken(RefreshTokenRequest tokenRequest);
}
