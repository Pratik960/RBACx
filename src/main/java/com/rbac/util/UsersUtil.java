package com.rbac.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.rbac.config.security.web.CustomAuthenticationToken;
import com.rbac.model.dao.UsersDao;
import com.rbac.model.entity.Users;
import com.rbac.util.http.exceptions.UnauthorizedException;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author pratiksolanki
 */
@Service
@Slf4j
@SessionScope
public class UsersUtil {
    
    @Autowired
    private UsersDao userDao;

    private Users authUser;


    /**
     * Give active and auth user details
     *
     * @return Users
     */
    public Users getAuthUser(Users.UserStatus status){
        try{
            CustomAuthenticationToken authenticationToken = getAuthToken().orElseThrow(
                () ->  new UnauthorizedException("User is not authorized")
            );
            Object principal = authenticationToken.getPrincipal();
            String username;
        
            // Extract username based on principal type
            if (principal instanceof String) {
                username = (String) principal;
            } else if (principal instanceof User) {
                username = ((User) principal).getUsername();
            } else {
                throw new UnauthorizedException("Unexpected principal type: " + principal.getClass());
            }
        
            log.info("Auth user username : {}", username);
            if(authUser == null || !authUser.getUsername().equals(username)){
                authUser = userDao.findByUsernameAndStatus(username, status).orElseThrow(
                    () -> new UnauthorizedException("User is not authenticated")
                );
            }
            return authUser;
        } catch (Exception ex){
            log.error("Invalid Auth User", ex);
        }

        return null;
    }


     /**
     * Get auth username
     *
     * @return String USERNAME
     */
    private Optional<CustomAuthenticationToken> getAuthToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof CustomAuthenticationToken) {
            return Optional.of((CustomAuthenticationToken) auth);
        }
        return Optional.empty();
    }
}
