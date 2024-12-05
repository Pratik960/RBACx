package com.rbac.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rbac.config.security.web.AdminUserDetailsService;
import com.rbac.config.security.web.JwtUtil;
import com.rbac.model.dao.UsersDao;
import com.rbac.model.dao.specification.UserSpecs;
import com.rbac.model.dto.auth.LoginResponse;
import com.rbac.model.dto.user.UserAuthenticateRequest;
import com.rbac.model.dto.user.UserListRequest;
import com.rbac.model.dto.user.UserRequest;
import com.rbac.model.dto.user.UserResponse;
import com.rbac.model.dto.user.UserStatusUpdateRequest;
import com.rbac.model.dto.user.UserUpdateRequest;
import com.rbac.model.entity.RefreshToken;
import com.rbac.model.entity.Users;
import com.rbac.model.entity.Users.UserStatus;
import com.rbac.service.RefreshTokenService;
import com.rbac.service.UserService;
import com.rbac.util.AppProperties;
import com.rbac.util.AppUtil;
import com.rbac.util.DefaultMessage;
import com.rbac.util.ObjectUtil;
import com.rbac.util.UsersUtil;
import com.rbac.util.http.exceptions.CustomException;
import com.rbac.util.http.exceptions.InternalServerErrorException;
import com.rbac.util.http.exceptions.ResourceNotFoundException;
import com.rbac.util.http.exceptions.UnauthorizedException;
import com.rbac.util.http.response.PageResponse;
import com.rbac.util.http.response.SuccessResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UsersDao userDao;

    private final BCryptPasswordEncoder passwordEncoder;

    private final AdminUserDetailsService adminUserDetailsService;

    private final JwtUtil jwtUtil;

    private final StringEncryptor encryptor;

    private final AppProperties appProperties;

    private final JavaMailSender emailSender;

    private final UsersUtil usersUtil;

    private final RefreshTokenService refreshTokenService;

    @Value("${app.email}")
    private String senderEmail;

    @Autowired
    public UserServiceImpl(UsersDao userDao, BCryptPasswordEncoder passwordEncoder,
            AdminUserDetailsService adminUserDetailsService, JwtUtil jwtUtil, StringEncryptor encryptor,
            AppProperties appProperties, JavaMailSender emailSender, UsersUtil usersUtil,
            RefreshTokenService refreshTokenService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.adminUserDetailsService = adminUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.encryptor = encryptor;
        this.appProperties = appProperties;
        this.emailSender = emailSender;
        this.usersUtil = usersUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    }

    /**
     * Find the user by its ID.
     *
     * @param id the ID of the user {@link Integer}
     * @return {@link SuccessResponse}<{@link UserResponse}>
     * @throws ResourceNotFoundException if the user is not found with id
     * @throws CustomException           if an error occurs while finding user
     */
    @Override
    public SuccessResponse<UserResponse> findUserById(Integer id) {

        try {

            Users user = userDao.findById(id)
                    .orElseThrow(
                            () -> new ResourceNotFoundException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("User")));

            UserResponse userResponse = parseUserToUserResponse(user);
            return new SuccessResponse<>(userResponse, HttpStatus.OK.value());

        } catch (ResourceNotFoundException | CustomException ex) {
            log.error("Resoure not found exception occured : {}", ex.getMessage());
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Exception Occurred while finding user {}", ex.getMessage(), ex);
            throw new InternalServerErrorException(DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("fetching User"));
        }

    }

    /**
     * Returns a list of all users present in database.
     *
     * @return {@link SuccessResponse}<{@link List}<{@link UserResponse}> >returning
     *         the list of users.
     * @throws ResourceNotFoundException if the users are not found
     * @throws CustomException           if an error occurs while fetching all users
     */
    @Override
    public SuccessResponse<List<UserResponse>> findAllUsers() {

        try {

            List<UserResponse> responses = new ArrayList<>();
            List<Users> usersList = userDao.findAll();

            for (Users users : usersList) {
                responses.add(parseUserToUserResponse(users));
            }

            return new SuccessResponse<>(responses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Exception occurred while fetching all users {}", e.getMessage(), e);
            throw new InternalServerErrorException(DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("fetching Users"));
        }
    }

    /**
     * Creates a new user for rbac
     *
     * @param userRequest the request containing information of new user to be
     *                    created {@link UserRequest}
     * @return {@link SuccessResponse}<{@link UserResponse}> returning the newly
     *         created user
     * @throws CustomException if an error occurs while creating the user
     */
    @Override
    public SuccessResponse<UserResponse> createUser(UserRequest request) {
        try {

            if (request.getRole().toString().equalsIgnoreCase("ROLE_ADMIN")) {
                throw new CustomException("Creating a user with ROLE_ADMIN is not allowed.");
            }

            String username = request.getUsername();

            Optional<Users> optUser = userDao.findByUsername(username);

            if (optUser.isPresent()) {
                throw new CustomException(DefaultMessage.ALREADY_PRESENT.getMessage("User"));
            }

            if (userDao.existsByEmailAndRoleAndStatus(request.getEmail(), request.getRole(), Users.UserStatus.ACTIVE)) {
                throw new CustomException("User with email already exists.");
            }
            Users users = parseUserRequestToUser(request);
            users = userDao.save(users);

            UserResponse response = parseUserToUserResponse(users);

            sendActivationEmail(users.getEmail(), encryptor.encrypt(users.getUsername()));
            return new SuccessResponse<>(response, HttpStatus.CREATED.value());

        } catch (CustomException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error(String.format(DefaultMessage.CREATE_ERROR.getMessage("User")), ex);
            throw new CustomException(DefaultMessage.CREATE_ERROR.getMessage("User"));
        }
    }

    /**
     * Authenticates the user with username, password, and authorities held by the
     * user.
     *
     * @param userRequest the request containing user authentication details
     *                    {@link UserAuthenticateRequest}
     * @param request     the HTTP servlet request {@link HttpServletRequest}
     * @return {@link SuccessResponse}<{@link String}> returning the generated
     *         AuthToken for the user to be used later for consecutive requests
     * @throws UnauthorizedException if the user with the username is not found
     * @throws CustomException       if any error occurs while authenticating the
     *                               user
     */
    @Override
    public SuccessResponse<LoginResponse> authenticateUser(UserAuthenticateRequest userRequest,
            HttpServletRequest request) {

        String authToken;
        try {
            String username = userRequest.getUsername();

            Optional<Users> optUser = userDao.findByUsernameAndStatus(username, Users.UserStatus.ACTIVE);
            if (optUser.isEmpty()) {
                throw new UnauthorizedException("Active user with username " + username + " not found.");
            }

            Users users = optUser.get();

            UserDetails userDetails = adminUserDetailsService.loadUserByUsers(users);

            AppUtil.setAuthentication(request, userDetails, userRequest.getPassword());

            Map<String, Object> claims = new HashMap<>();
            claims.put("authorities", getAuthorities(userDetails.getAuthorities()));

            authToken = jwtUtil.createToken(claims, String.valueOf(users.getId()), "rbac", username);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);
            LoginResponse response = new LoginResponse();
            response.setToken(authToken);
            response.setRefreshToken(refreshToken.getRefreshToken());
            response.setUserRole(users.getAuthorities().name());
            response.setUserId(users.getId());
            return new SuccessResponse<>(response, HttpStatus.OK.value());
        } catch (UnauthorizedException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception e) {
            log.error("Exception Occurred while authenticating user: {} ", e.getMessage(), e);
            throw new CustomException("Error while authenticating User. " + e.getMessage());
        }
    }

    /**
     * Activates the user account when the user verifies their email.
     *
     * @param params a map containing the parameters required for activation,
     *               including the token
     * @return the URL to redirect the user to, either the login page or a page to
     *         resend the link
     * @throws CustomException           if the token is missing, malformed, or the
     *                                   account is already activated
     * @throws ResourceNotFoundException if no inactive account is found for the
     *                                   given token
     */
    @Override
    public String activateAccount(Map<String, String> params) {

        try {

            String token = params.getOrDefault("token", null);
            if (AppUtil.isNullOrEmptyString(token)) {
                throw new CustomException("Verification code is incorrect or malfunctioned");
            }

            token = token.replaceAll(" ", "+");
            String descryptedToken = encryptor.decrypt(token);

            Users users = userDao.findByUsername(descryptedToken).orElseThrow(
                    () -> new ResourceNotFoundException("User does not have an account in the system"));

            if (users.getStatus() == Users.UserStatus.ACTIVE) {
                throw new CustomException("User account is already activated");
            }

            if (isLinkValid(users.getUpdatedAt())) {
                users.setStatus(Users.UserStatus.ACTIVE);
                userDao.save(users);

                log.info("User's account is verified and activated");
                return appProperties.getUrl().concat("/app/activation");
            } else {
                return "Link expired";
            }

        } catch (CustomException ex) {
            log.error("Custom exception occurred: {}", ex.getMessage());
            throw new CustomException(ex.getMessage(), ex);
        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            throw new CustomException(ex.getMessage(), ex);
        } catch (EncryptionOperationNotPossibleException ex) {
            log.error("Invalid token: {}", ex.getMessage());
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("An unexpected error occurred: {}", ex.getMessage());
            throw new CustomException(ex.getMessage(), ex);
        }
    }

    /**
     * Updates the authenticated user's profile details.
     *
     * @param updateRequest the request object containing new profile details
     * @return {@link SuccessResponse}<{@link UserResponse}> the updated user profile
     *               response
     * @throws CustomException              if the user is unauthorized or a general
     *                                      error occurs
     * @throws ResourceNotFoundException    if the user is not found or inactive
     * @throws InternalServerErrorException for unexpected server errors
     */
    @Override
    public SuccessResponse<UserResponse> updateUser(UserUpdateRequest updateRequest) {

        try {
            Users authUser = usersUtil.getAuthUser(Users.UserStatus.ACTIVE);

            Users user = userDao.findByIdAndStatus(updateRequest.getUserId(), Users.UserStatus.ACTIVE)
                    .orElseThrow(
                            () -> new ResourceNotFoundException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("User")));

            if (!Objects.equals(authUser.getId(), user.getId())) {
                throw new CustomException("You are not authorized to update another user's profile.");
            }

            user.setFirstName(updateRequest.getFirstName());
            user.setLastName(updateRequest.getLastName());
            user.setEmail(updateRequest.getEmail());

            user = userDao.save(user);
            UserResponse response = parseUserToUserResponse(user);
            return new SuccessResponse<>(response, HttpStatus.OK.value());
        } catch (ResourceNotFoundException | CustomException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(
                    DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("updating user profile"));
        }
    }

    /**
     * Marks a user as inactive (soft delete) by updating their status.
     *
     * @param id the ID of the user to be deleted
     * @return a success response confirming the user deletion
     * @throws ResourceNotFoundException    if the user is not found or inactive
     * @throws CustomException              for general business logic errors
     * @throws InternalServerErrorException for unexpected server errors
     */
    @Override
    public SuccessResponse<String> deleteUser(Integer id) {
        try {

            Users user = userDao.findByIdAndStatus(id, Users.UserStatus.ACTIVE)
                    .orElseThrow(
                            () -> new ResourceNotFoundException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("User")));

            user.setStatus(Users.UserStatus.INACTIVE);
            userDao.save(user);

            return new SuccessResponse<>(DefaultMessage.DELETE_SUCCESS.getMessage("User"), HttpStatus.OK.value());
        } catch (ResourceNotFoundException | CustomException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(
                    DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("deleting user account"));
        }
    }

    /**
     * Retrieves a paginated list of users based on the specified filter and
     * pagination criteria.
     *
     * @param listRequest the request containing filtering and pagination options
     * @return a paginated response containing user details
     * @throws CustomException if an error occurs while fetching user data
     */

    @Override
    public PageResponse<UserResponse> getAllUsers(UserListRequest listRequest) {
        try {

            Page<Users> rsUsers = getAllUsersByRequest(listRequest, listRequest.isPageable());

            List<UserResponse> userResponses = rsUsers.stream()
                    .map(this::parseUserToUserResponse)
                    .toList();

            PageResponse<UserResponse> response = new PageResponse<>();
            response.setLimit(rsUsers.getSize());
            response.setPage(rsUsers.getNumber() + 1);
            response.setTotal(rsUsers.getTotalElements());
            response.setTotalPage(rsUsers.getTotalPages());
            response.setData(userResponses);

            return response;

        } catch (Exception e) {
            log.error(DefaultMessage.INTERNAL_SERVER_ERROR.getMessage(e.getMessage()), e);
            throw new CustomException(DefaultMessage.FETCH_ERROR.getMessage("all users"));
        }
    }

    /**
     * Retrieves a paginated or unpaginated list of users based on the specified
     * filter criteria.
     *
     * @param userListRequest the request containing filtering and sorting options
     * @param isPageable      a flag indicating whether pagination should be applied
     * @return a paginated or complete list of users matching the criteria
     */
    @Override
    public Page<Users> getAllUsersByRequest(UserListRequest userListRequest, boolean isPageable) {
        Pageable pageable;
        if (isPageable) {
            pageable = ObjectUtil.getPageable(userListRequest.getPage(), userListRequest.getPerPage(),
                    userListRequest.getSortBy(), userListRequest.getSort());
        } else {
            pageable = PageRequest.of(0, Integer.MAX_VALUE);
        }
        Specification<Users> rsSpecification = UserSpecs.getUserList(userListRequest);
        return userDao.findAll(rsSpecification, pageable);
    }

    /**
     * Updates the status of a user.
     *
     * @param updateStatusRequest the request containing the user ID and new status
     * @return a success response indicating the status update was successful
     * @throws ResourceNotFoundException if the user is not found
     * @throws CustomException           for other application-specific errors
     */
    @Override
    public SuccessResponse<String> updateUserStatus(UserStatusUpdateRequest updateStatusRequest) {
        try {

            Users user = userDao.findById(updateStatusRequest.getUserId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException(DefaultMessage.RESOURCE_NOT_FOUND.getMessage("User")));

            user.setStatus(updateStatusRequest.getStatus());
            userDao.save(user);

            return new SuccessResponse<>(DefaultMessage.UPDATE_SUCCESS.getMessage("User"), HttpStatus.OK.value());
        } catch (ResourceNotFoundException | CustomException ex) {
            throw new CustomException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(
                    DefaultMessage.INTERNAL_SERVER_ERROR.getMessage("updating user status"));
        }
    }

    private UserResponse parseUserToUserResponse(Users users) {

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(users.getId());
        userResponse.setEmail(users.getEmail());
        userResponse.setUsername(users.getUsername());
        userResponse.setStatus(users.getStatus());
        userResponse.setFirstName(users.getFirstName());
        userResponse.setLastName(users.getLastName());
        userResponse.setUpdatedAt(users.getUpdatedAt());
        userResponse.setCreatedAt(users.getCreatedAt());

        return userResponse;
    }

    private Users parseUserRequestToUser(UserRequest userRequest) {

        Users user = new Users();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setStatus(UserStatus.INACTIVE);
        user.setAuthorities(userRequest.getRole());

        return user;
    }

    private String getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private boolean isLinkValid(Timestamp updatedAt) {
        Timestamp currentTime = Timestamp.from(Instant.now());

        long differenceInMillis = currentTime.getTime() - updatedAt.getTime();

        long differenceInYears = differenceInMillis / (1000L * 60 * 60 * 24 * 365);

        // Check if the difference is less than or equal to 1 year
        return differenceInYears <= 1;
    }

    private void sendActivationEmail(String email, String activationToken) {

        String activationLink = String.format("%s/api/auth/activate-account?token=%s", appProperties.getUrl(),
                activationToken);
        String subject = "Activate your account";
        String messageBody = "Activation link will be expired in 1 year. Please click the following link to activate your account: "
                + activationLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(messageBody);

        emailSender.send(message);
    }

}
