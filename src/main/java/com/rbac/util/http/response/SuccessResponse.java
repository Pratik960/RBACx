package com.rbac.util.http.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author pratiksolanki
 * @param <T>
 */
@Data
public class SuccessResponse<T> {
    private T data;

    private Integer status = HttpStatus.OK.value();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public SuccessResponse(T data, int status) {
        this.data = data;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponse() {
        this.timestamp = LocalDateTime.now();
    }
}
