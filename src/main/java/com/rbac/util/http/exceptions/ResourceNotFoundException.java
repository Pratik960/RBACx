package com.rbac.util.http.exceptions;


/**
 * @author pratiksolanki
 */

public class ResourceNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable thrwbl) {
        super(message, thrwbl);
    }

    public ResourceNotFoundException(Throwable thrwbl) {
        super(thrwbl);
    }

    protected ResourceNotFoundException(String message, Throwable thrwbl, boolean bln, boolean bln1) {
        super(message, thrwbl, bln, bln1);
    }
}
