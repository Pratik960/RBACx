package com.rbac.util.http.exceptions;

import java.util.ArrayList;
import java.util.List;

public class CustomException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<>();

    public CustomException(String message){
        super(message);
    }

    public CustomException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public CustomException(List<String> errors) {
        super();
        this.errors = errors;
    }

    public CustomException(String message, Throwable thrwbl) {
        super(message, thrwbl);
    }

    public CustomException(String message, Exception e) {
        super(message, e);
    }

    public List<String> getErrors() {
        return errors;
    }


}
