package com.rbac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rbac.config.ratelimiter.RateLimiterFallback;
import com.rbac.model.dto.task.TaskRequest;
import com.rbac.model.dto.task.TaskResponse;
import com.rbac.model.dto.user.UserStatusUpdateRequest;
import com.rbac.service.TaskService;
import com.rbac.service.UserService;
import com.rbac.util.http.response.SuccessResponse;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin", description = "Admin API")
@RestController
@Slf4j
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5001")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final TaskService taskService;
    private final RateLimiterFallback fallback;

    @Autowired
    public AdminController(UserService userService, TaskService taskService, RateLimiterFallback fallback) {
        this.userService = userService;
        this.taskService = taskService;
        this.fallback = fallback;
    }

    @Operation(summary = "Create new task", description = "Creates a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid task request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add/task")
    public ResponseEntity<SuccessResponse<TaskResponse>> addTask(@Valid @RequestBody TaskRequest taskRequest) {
        SuccessResponse<TaskResponse> response = taskService.addTask(taskRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete user profile", description = "Delete profile of user by updating its status to inactive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "429", description = "Too many requests (rate limit exceeded)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/deleteAccount/{id}")
    @RateLimiter(name = "highPriorityEndpoint", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<SuccessResponse<String>> deleteUserProfile(@PathVariable Integer id) {
        SuccessResponse<String> response = userService.deleteUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update user status", description = "update status of user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update/status")
    public ResponseEntity<SuccessResponse<String>> updateUserStatus(
            @Valid @RequestBody UserStatusUpdateRequest updateRequest) {
        SuccessResponse<String> response = userService.updateUserStatus(updateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<String> rateLimitFallback(Exception ex) {
        return fallback.rateLimitFallback(ex);
    }
}
