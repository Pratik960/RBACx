package com.rbac.config.ratelimiter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterFallback {

    public ResponseEntity<String> rateLimitFallback(Exception ex) {
        long retryAfterSeconds = 60;

        // Set the Retry-After header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-After", String.valueOf(retryAfterSeconds));

        return new ResponseEntity<>("Rate limit exceeded. Please try again later.",headers, HttpStatus.TOO_MANY_REQUESTS);
    }
}

