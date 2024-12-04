package com.rbac.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rbac.config.ratelimiter.RateLimiterFallback;
import com.rbac.model.dto.auth.LoginResponse;
import com.rbac.model.dto.auth.RefreshTokenRequest;
import com.rbac.model.dto.user.UserAuthenticateRequest;
import com.rbac.model.dto.user.UserRequest;
import com.rbac.model.dto.user.UserResponse;
import com.rbac.model.entity.RefreshToken;
import com.rbac.service.RefreshTokenService;
import com.rbac.service.UserService;
import com.rbac.util.AppUtil;
import com.rbac.util.http.exceptions.CustomException;
import com.rbac.util.http.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@Tag(name = "Auth", description = "Auth API")
@RestController
@Slf4j
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5001")
public class AuthController {

    private final UserService userService;

    private final RateLimiterFallback fallback;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(UserService userService, RateLimiterFallback fallback,
            RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.fallback = fallback;
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(summary = "Activate User's Account", description = "It activates users' account by verifying verification code provided to registered email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account activated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid verification code"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/activate-account")
    public ResponseEntity<HttpStatus> activateAccount(@RequestParam Map<String, String> params,
            HttpServletResponse response) {
        try {
            String redirectUrl = userService.activateAccount(params);
            if (!AppUtil.isNullOrEmptyString(redirectUrl)) {
                response.sendRedirect(redirectUrl);
            }
        } catch (IOException ex) {
            log.error("Error while verifying user account : ", ex);
            throw new CustomException("Something went wring while activating user account.");
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Create new user", description = "Creates a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "429", description = "Too many requests (rate limit exceeded)")
    })
    @PostMapping("/signup")
    @RateLimiter(name = "default", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<SuccessResponse<UserResponse>> signUpUser(@Valid @RequestBody UserRequest userRequest) {
        SuccessResponse<UserResponse> response = userService.createUser(userRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Authenticate user for sign in", description = "Authenticate an user for sign in operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "429", description = "Too many requests (rate limit exceeded)")
    })
    @PostMapping("/authenticate")
    @RateLimiter(name = "highPriorityEndpoint", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<SuccessResponse<LoginResponse>> loginUser(@RequestBody UserAuthenticateRequest userRequest,
            HttpServletRequest request) {
        SuccessResponse<LoginResponse> response = userService.authenticateUser(userRequest, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Refresh token", description = "Generate a new access token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "429", description = "Too many requests (rate limit exceeded)")
    })
    @PostMapping("/refreshToken")
    @RateLimiter(name = "default", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<SuccessResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest tokenRequest) {
        SuccessResponse<LoginResponse> response = refreshTokenService.refreshToken(tokenRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<String> rateLimitFallback(Exception ex) {
        return fallback.rateLimitFallback(ex);
    }

}
