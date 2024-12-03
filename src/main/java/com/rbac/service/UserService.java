package com.rbac.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.rbac.model.dto.auth.LoginResponse;
import com.rbac.model.dto.user.UserAuthenticateRequest;
import com.rbac.model.dto.user.UserListRequest;
import com.rbac.model.dto.user.UserRequest;
import com.rbac.model.dto.user.UserResponse;
import com.rbac.model.dto.user.UserStatusUpdateRequest;
import com.rbac.model.dto.user.UserUpdateRequest;
import com.rbac.model.entity.Users;
import com.rbac.util.http.response.PageResponse;
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

    SuccessResponse<String> updateUserStatus(UserStatusUpdateRequest updateStatusRequest);

    PageResponse<UserResponse> getAllUsers(UserListRequest listRequest);

    Page<Users> getAllUsersByRequest (UserListRequest userListRequest, boolean isPageable);
}
