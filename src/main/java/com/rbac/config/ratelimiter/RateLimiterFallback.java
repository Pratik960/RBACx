package com.rbac.config.ratelimiter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;

@Component
public class RateLimiterFallback {

    public ResponseEntity<String> rateLimitFallback(Exception ex) {
        if (ex instanceof RequestNotPermitted) {
            long retryAfterSeconds = 60;
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Retry-After", String.valueOf(retryAfterSeconds));

            return new ResponseEntity<>("Rate limit exceeded. Please try again later.", headers,
                    HttpStatus.TOO_MANY_REQUESTS);
        }
        throw (RuntimeException) ex;
    }
}
