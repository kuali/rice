package org.kuali.rice.exceptions;

public class RiceRuntimeException extends RuntimeException {

private static final long serialVersionUID = 2012770642382150523L;
    
    public RiceRuntimeException(String message) {
        super(message);
    }

    public RiceRuntimeException() {
        super();
    }

    public RiceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RiceRuntimeException(Throwable cause) {
        super(cause);
    }	
}