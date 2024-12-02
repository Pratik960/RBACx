package com.rbac.service;

import java.util.List;
import java.util.Map;

import com.rbac.model.dto.user.UserAuthenticateRequest;
import com.rbac.model.dto.user.UserRequest;
import com.rbac.model.dto.user.UserResponse;
import com.rbac.model.dto.user.UserUpdateRequest;
import com.rbac.model.dto.user.Auth.LoginResponse;
import com.rbac.util.http.response.SuccessResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    
    SuccessResponse<UserResponse> findUserById(Integer id);

    SuccessResponse<List<UserResponse>> findAllUsers();

    SuccessResponse<UserResponse> createUser(UserRequest request);

    SuccessResponse<UserResponse> updateUser(UserUpdateRequest updateRequest);

    SuccessResponse<LoginResponse> authenticateUser(UserAuthenticateRequest userRequest, HttpServletRequest request);

    String activateAccount(Map<String, String> params);

    SuccessResponse<String> deleteUser(Integer id);
}
