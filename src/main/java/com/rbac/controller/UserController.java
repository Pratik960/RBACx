package com.rbac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.rbac.model.dto.user.UserResponse;
import com.rbac.model.dto.user.UserUpdateRequest;
import com.rbac.service.UserService;
import com.rbac.util.http.response.SuccessResponse;

@Tag(name = "Users", description = "Users API")
@RestController
@Slf4j
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    
    @Operation(
            summary = "Update user profile",
            description = "update profile of user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EMP')")
    @PutMapping()
    public ResponseEntity<SuccessResponse<UserResponse>> updateUserProfile(@Valid @RequestBody UserUpdateRequest updateRequest){
        SuccessResponse<UserResponse> response = userService.updateUser(updateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user profile",
            description = "Delete profile of user by updating its status to inactive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/deleteAccount/{id}")
    public ResponseEntity<SuccessResponse<String>> deleteUserProfile(@PathVariable Integer id){
        SuccessResponse<String> response = userService.deleteUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
