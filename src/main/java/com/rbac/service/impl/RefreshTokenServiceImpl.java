package com.rbac.service.impl;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.rbac.config.security.web.AdminUserDetailsService;
import com.rbac.config.security.web.JwtUtil;
import com.rbac.model.dao.RefreshTokenDao;
import com.rbac.model.dao.UsersDao;
import com.rbac.model.dto.auth.LoginResponse;
import com.rbac.model.dto.auth.RefreshTokenRequest;
import com.rbac.model.entity.RefreshToken;
import com.rbac.model.entity.Users;
import com.rbac.service.RefreshTokenService;
import com.rbac.util.AppUtil;
import com.rbac.util.DefaultMessage;
import com.rbac.util.http.exceptions.CustomException;
import com.rbac.util.http.exceptions.InternalServerErrorException;
import com.rbac.util.http.exceptions.ResourceNotFoundException;
import com.rbac.util.http.response.SuccessResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${refreshToken.expiration}")
    private long refreshTokenExpiry;

    private final RefreshTokenDao refreshTokenDao;

    private final UsersDao usersDao;

    private final AdminUserDetailsService adminUserDetailsService;

    private final JwtUtil jwtUtil;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenDao refreshTokenDao, UsersDao usersDao,
            AdminUserDetailsService adminUserDetailsService, JwtUtil jwtUtil) {
        this.refreshTokenDao = refreshTokenDao;
        this.usersDao = usersDao;
        this.adminUserDetailsService = adminUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates or updates a refresh token for a user, extending its expiry if it
     * already exists.
     *
     * @param username the username of the user for whom the refresh token will be
     *                 created
     * @return {@link RefreshToken} the created or updated refresh token
     * @throws ResourceNotFoundException    if the user with the given username is
     *                                      not found or is inactive
     * @throws CustomException              if an error occurs during the process of
     *                                      creating or updating the refresh token
     * @throws InternalServerErrorException if an unexpected error occurs while
     *                                      creating the refresh token
     */
    @Override
    public RefreshToken createRefreshToken(String username) {

        try {

            RefreshToken refreshToken;
            Users user = usersDao.findByUsernameAndStatus(username, Users.UserStatus.ACTIVE)
                    .orElseThrow(
                            () -> new ResourceNotFoundException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("User")));

            Optional<RefreshToken> optRefreshToken = refreshTokenDao.findByUser(user);

            if (optRefreshToken.isPresent()) {
                refreshToken = optRefreshToken.get();
                refreshToken.setExpiry(Instant.now().plusMillis(refreshTokenExpiry));
            } else {
                refreshToken = new RefreshToken();
                refreshToken.setRefreshToken(AppUtil.generateUUID());
                refreshToken.setExpiry(Instant.now().plusMillis(refreshTokenExpiry));
                refreshToken.setUser(user);
            }

            refreshTokenDao.save(refreshToken);
            return refreshToken;

        } catch (ResourceNotFoundException | CustomException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(
                    DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("creating refreshtoken"));
        }
    }

    /**
     * Verifies the provided refresh token by checking its existence and expiry.
     * If the token is expired, it is deleted and an exception is thrown.
     *
     * @param refreshToken the refresh token to be verified
     * @return {@link RefreshToken} the valid refresh token
     * @throws CustomException              if the refresh token is not found or has
     *                                      expired
     * @throws InternalServerErrorException if an unexpected error occurs while
     *                                      verifying the refresh token
     */
    @Override
    public RefreshToken verifyRefreshToken(String refreshToken) {
        try {
            RefreshToken optRefreshToken = refreshTokenDao.findByRefreshToken(refreshToken)
                    .orElseThrow(
                            () -> new CustomException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("Refresh Token")));

            if (optRefreshToken.getExpiry().compareTo(Instant.now()) < 0) {
                refreshTokenDao.delete(optRefreshToken);
                throw new CustomException("Refresh Token Expired !!");
            }

            return optRefreshToken;
        } catch (ResourceNotFoundException | CustomException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(
                    DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("validating refreshtoken"));
        }
    }

    /**
     * Generates a new authentication token using a valid refresh token.
     *
     * @param tokenRequest the request containing the refresh token to be verified
     * @return {@link SuccessResponse}<{@link LoginResponse}> the response
     *         containing the new authentication token,
     *         the original refresh token, user role, and user ID
     * @throws CustomException              if the refresh token is invalid,
     *                                      expired, or if there is any
     *                                      application-specific error
     * @throws InternalServerErrorException if an unexpected error occurs while
     *                                      generating the new token
     */
    @Override
    public SuccessResponse<LoginResponse> refreshToken(RefreshTokenRequest tokenRequest) {
        try {
            RefreshToken refreshToken = verifyRefreshToken(tokenRequest.getRefreshToken());
            Users user = refreshToken.getUser();
            UserDetails userDetails = adminUserDetailsService.loadUserByUsers(user);

            Map<String, Object> claims = new HashMap<>();
            claims.put("authorities", getAuthorities(userDetails.getAuthorities()));

            String newAuthToken = jwtUtil.createToken(claims, String.valueOf(user.getId()), "rbac", user.getUsername());

            LoginResponse response = new LoginResponse();
            response.setToken(newAuthToken);
            response.setRefreshToken(refreshToken.getRefreshToken());
            response.setUserRole(user.getAuthorities().name());
            response.setUserId(user.getId());
            return new SuccessResponse<>(response, HttpStatus.OK.value());
        } catch (ResourceNotFoundException | CustomException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(
                    DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("generating new token"));
        }
    }

    private String getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
