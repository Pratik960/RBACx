package com.rbac.config.security.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider{
    
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationProvider(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    
        String authToken = (String) authentication.getCredentials();

        // Validate JWT token
        if(jwtUtil.validateToken(authToken)){
            String username = jwtUtil.extractUsername(authToken);

            UserDetails userDetails = new User(username, authToken, authentication.getAuthorities());
            return new CustomAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid JWT token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    
}