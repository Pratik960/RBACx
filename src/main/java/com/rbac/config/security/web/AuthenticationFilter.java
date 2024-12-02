package com.rbac.config.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbac.util.http.exceptions.UnauthorizedException;
import com.rbac.util.http.response.ExceptionResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final AdminUserDetailsService adminUserDetailsService;

    private final ObjectMapper objectMapper;

    private final JwtUtil jwtUtil;

    private final Set<String> skipUrls = new HashSet<>(List.of(
            "/api/auth/activate-account/**",
            "/api/auth/authenticate",
            "/api/auth/signup"
    ));

    @Autowired
    public AuthenticationFilter(JwtAuthenticationProvider jwtAuthenticationProvider, AdminUserDetailsService adminUserDetailsService, ObjectMapper objectMapper, JwtUtil jwtUtil) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.adminUserDetailsService = adminUserDetailsService;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {

        String path = request.getRequestURI();
        log.info("Request Path : {}", request.getRequestURI());

        try {

            if (path.matches("/api/.*")) {

                final String authorizationHeader = request.getHeader("Authorization");

                if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                    throw new UnauthorizedException("You are not authorized to access the API");
                }

                String jwtToken = authorizationHeader.substring(7);
                String username = jwtUtil.extractUsername(jwtToken);
                log.info("Spring username : {}", username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = adminUserDetailsService.loadUserByUsername(username);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, jwtToken, userDetails.getAuthorities());
                    Authentication authenticated = jwtAuthenticationProvider.authenticate(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authenticated);
                }
            }

            chain.doFilter(request, response);

        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnauthorizedException |
                 UsernameNotFoundException e) {
            logger.error("UnauthorizedException ", e);
            handleException(e, response, HttpStatus.UNAUTHORIZED);
//            response.sendRedirect(appProperties.getReactUrl().concat(appProperties.getReactLoginPath()));
        } catch (IOException | ServletException e) {
            logger.error("Error in admin filter ", e);
            handleException(e, response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Internal server error in filter request ", e);
            handleException(e, response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleException(Exception ex, HttpServletResponse response, HttpStatus httpStatus) throws IOException {

        ExceptionResponse errorResponse = new ExceptionResponse();
        errorResponse.setStatus(httpStatus.value());
        errorResponse.setError(httpStatus.name());
        if (ex instanceof ExpiredJwtException || ex instanceof SignatureException) {
            errorResponse.setMessage("Invalid or Malfunctioned Security Token. Please Login or SignUp");
        } else {
            errorResponse.setMessage(ex.getMessage());
        }
        errorResponse.setErrors(List.of(ex.getMessage()));

        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return skipUrls.stream().filter(path -> !path.contains("/api/auth/signup") || ("POST".equalsIgnoreCase(request.getMethod()) && path.contains("/api/auth/signup")))
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }

}
