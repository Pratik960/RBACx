package com.rbac.util.http.exceptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rbac.util.http.response.ExceptionResponse;

import jakarta.validation.ValidationException;

/**
 * @author pratiksolanki
*/

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> customException(CustomException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<>();

        if (e.getMessage() != null) {
            errors.add(e.getMessage());
        }

        ExceptionResponse response = new ExceptionResponse();
        response.setMessage(errors.isEmpty() ? null : errors.get(0));
        response.setError(status.name());
        errors.addAll(e.getErrors());
        response.setErrors(errors);
        response.setStatus(status.value());

        response.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFoundException(ResourceNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse();
        response.setError(status.name());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        response.setErrors(errors);
        response.setMessage(e.getMessage());
        response.setStatus(status.value());
        response.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ResourceAlreadyExists.class)
    public ResponseEntity<ExceptionResponse> resourceAlreadyExists(ResourceAlreadyExists ex) {
        ExceptionResponse response = new ExceptionResponse();
        HttpStatus status = HttpStatus.CONFLICT;
        response.setError(status.name());
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        response.setErrors(errors);
        response.setMessage(ex.getMessage());
        response.setStatus(status.value());
        response.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(response, status);

    }


    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ExceptionResponse> internalServerError(InternalServerErrorException ex) {
        ExceptionResponse response = new ExceptionResponse();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        response.setError(status.name());
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        response.setErrors(errors);
        response.setMessage(ex.getMessage());
        response.setStatus(status.value());
        response.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedError(AccessDeniedException ex) {
        ExceptionResponse response = new ExceptionResponse();
        HttpStatus status = HttpStatus.FORBIDDEN;
        response.setError(status.name());
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        response.setErrors(errors);
        response.setMessage(ex.getMessage());
        response.setStatus(status.value());
        response.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<ExceptionResponse> handleUnAuthorizeException(UnauthorizedException ex) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ExceptionResponse response = new ExceptionResponse();

        String errorMessage = Optional.of(ex.getMessage())
                .orElse("You are not authorized to perform this operation. Please check your authority.");

        response.setMessage(errorMessage);
        response.setError(status.name());

        List<String> errorList = new ArrayList<>();
        errorList.add(errorMessage);

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status.value());
        response.setErrors(errorList);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage("Validation Failed");
        exceptionResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        exceptionResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        exceptionResponse.setErrors(errors);
        exceptionResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(ValidationException ex) {


        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(ex.getMessage());
        exceptionResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        exceptionResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        exceptionResponse.setErrors(List.of(ex.getMessage()));
        exceptionResponse.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
