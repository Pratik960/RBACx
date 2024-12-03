package com.rbac.util.http.response;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;

/**
 * @param <T>
 * @author pratiksolanki
 */
@Getter
@Setter
@ControllerAdvice
public class PageResponse<T> {

    private List<T> data;
    private int page;
    private int limit;
    private long total;
    private int totalPage;
    
}

