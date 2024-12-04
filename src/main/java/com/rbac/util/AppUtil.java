package com.rbac.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import com.rbac.config.security.web.CustomAuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppUtil {
    
    private static AuthenticationManager authenticationManager;
    
    @Autowired
    private AppUtil(AuthenticationManager authenticationManager) {
        AppUtil.authenticationManager = authenticationManager;
    }

     /**
     * Creates an authentication token of type {@link UsernamePasswordAuthenticationToken} based on {@link UserDetails} and set it to {@link SecurityContextHolder}
     *
     * @param request     {@link HttpServletRequest}
     * @param userDetails {@link UserDetails}
     */
    public static void setAuthentication(HttpServletRequest request, UserDetails userDetails, String password) {
        CustomAuthenticationToken usernamePasswordAuthenticationToken = new CustomAuthenticationToken(
                userDetails, password, userDetails.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }


    public static boolean isNullOrEmptyString(Object str) {
        return str == null || str.toString().isBlank();
    }

    public static Date parseDate(String date, String format) {
        try {
            return isNullOrEmptyString(date) ? null : new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            log.error("ParseException occurred while parsing Date: " + e.getMessage(), e);
            return null;
        }
    }

    public static String generateUUID() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static Date parseDate(String date) {
        return parseDate(date, "yyyy-MM-dd");
    }

    private AppUtil() {

    }
    
}
