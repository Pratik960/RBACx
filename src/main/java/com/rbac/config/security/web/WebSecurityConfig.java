package com.rbac.config.security.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.rbac.util.AppProperties;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    
    private final String[] allowedPath = {"/app/**", "/signup", "/login", "/activation",  "/error-page", "/error-page-view", "/", "/api/auth/activate-account", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-resources", "/api/auth/authenticate", "/api/auth/refreshToken"};

    // If the admin panel is hosted with this project
    private final String[] allowedResource = {"/build/**", "/Images/**", "/css/**", "/js/**"};

    private final AdminUserDetailsService adminUserDetailsService;

    private final AuthenticationFilter authenticationFilter;

    private final AppProperties appProperties;

    @Autowired
    public WebSecurityConfig(AdminUserDetailsService adminUserDetailsService, AuthenticationFilter authenticationFilter, AppProperties appProperties) {
        this.adminUserDetailsService = adminUserDetailsService;
        this.authenticationFilter = authenticationFilter;
        this.appProperties = appProperties;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
            .requestMatchers(allowedResource);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider adminAuthProvider = new DaoAuthenticationProvider();
        adminAuthProvider.setUserDetailsService(adminUserDetailsService);
        adminAuthProvider.setPasswordEncoder(passwordEncoder());

        ProviderManager providerManager = new ProviderManager(adminAuthProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);
        return providerManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
            .cors(configure -> configure.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(http -> http.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(
                auth -> auth.requestMatchers(allowedPath).permitAll()
                            .requestMatchers(allowedResource).permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/admin/**").hasAnyAuthority("ROLE_ADMIN")
                            .requestMatchers("/api/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER", "ROLE_EMP").anyRequest().authenticated())
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedOrigins(appProperties.getAllowedOrigins());
        configuration.setAllowedMethods(List.of(
            HttpMethod.POST.name(),
            HttpMethod.GET.name(),
            HttpMethod.PUT.name()
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
