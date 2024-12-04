package com.rbac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.rbac.config.ratelimiter.RateLimiterFallback;
import com.rbac.model.dto.task.TaskListRequest;
import com.rbac.model.dto.task.TaskResponse;
import com.rbac.model.dto.task.TaskStatusUpdateRequest;
import com.rbac.model.dto.user.UserListRequest;
import com.rbac.model.dto.user.UserResponse;
import com.rbac.model.dto.user.UserUpdateRequest;
import com.rbac.service.TaskService;
import com.rbac.service.UserService;
import com.rbac.util.http.response.PageResponse;
import com.rbac.util.http.response.SuccessResponse;

@Tag(name = "Users", description = "Users API")
@RestController
@Slf4j
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5001")
public class UserController {

        private final UserService userService;
        private final TaskService taskService;
        private final RateLimiterFallback fallback;

        @Autowired
        public UserController(UserService userService, TaskService taskService, RateLimiterFallback fallback) {
                this.userService = userService;
                this.taskService = taskService;
                this.fallback = fallback;
        }

        @Operation(summary = "Fetch one user based on given Id", description = "fetches one user based on provided Id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User found and returned"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<SuccessResponse<UserResponse>> findUserById(@PathVariable Integer id) {
                SuccessResponse<UserResponse> response = userService.findUserById(id);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Fetch all users", description = "fetches all users entities")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Users successfully retrieved"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMP')")
        public ResponseEntity<SuccessResponse<List<UserResponse>>> findAllUsers() {
                SuccessResponse<List<UserResponse>> response = userService.findAllUsers();
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Update user profile", description = "update profile of user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PreAuthorize("hasAuthority('ROLE_USER')")
        @PutMapping()
        public ResponseEntity<SuccessResponse<UserResponse>> updateUserProfile(
                        @Valid @RequestBody UserUpdateRequest updateRequest) {
                SuccessResponse<UserResponse> response = userService.updateUser(updateRequest);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Fetch all tasks", description = "fetches all tasks with filter, search and sort")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tasks successfully retrieved"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/allTask")
        public ResponseEntity<PageResponse<TaskResponse>> getAllTasks(@Valid TaskListRequest listRequest) {
                PageResponse<TaskResponse> response = taskService.getAllTasks(listRequest);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Fetch all users", description = "fetches all users with filter, search and sort")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Users successfully retrieved"),
                        @ApiResponse(responseCode = "500", description = "Internal server error"),
                        @ApiResponse(responseCode = "429", description = "Too many requests (rate limit exceeded)")
        })
        @GetMapping("/allUsers")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMP')")
        @RateLimiter(name = "default", fallbackMethod = "rateLimitFallback")
        public ResponseEntity<PageResponse<UserResponse>> getAllUsers(@Valid UserListRequest listRequest) {
                PageResponse<UserResponse> response = userService.getAllUsers(listRequest);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Update task status", description = "update status of task")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid task status update request"),
                        @ApiResponse(responseCode = "404", description = "Task not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PutMapping("/update/status")
        public ResponseEntity<SuccessResponse<TaskResponse>> updateTaskStatus(
                        @Valid @RequestBody TaskStatusUpdateRequest updateRequest) {
                SuccessResponse<TaskResponse> response = taskService.updateTaskStatus(updateRequest);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        public ResponseEntity<String> rateLimitFallback(Exception ex) {
                return fallback.rateLimitFallback(ex);
        }

}
