package edu.iu.uis.eden;

import java.util.ArrayList;
import java.util.Collection;

/**
 * RuntimeException thrown from Service level classes when business rule validation
 * fails.  This exception is caught by StrutsExceptionHandler.  If any service errors
 * have been set on in the serviceErrors collection these are stripped off of the 
 * exception put into ActionMessages in the Error que and the request is directed back to 
 * the original ActionMapping input page.
 */
public class WorkflowServiceErrorException extends RuntimeException {


	private static final long serialVersionUID = 2457592489303923040L;
	private Collection serviceErrors;
    
    public WorkflowServiceErrorException(String message) {
        this(message, (Throwable)null);
    }
    
    public WorkflowServiceErrorException(String message, Throwable throwable) {
        super(message, throwable);
        serviceErrors = new ArrayList();
    }
    
    public WorkflowServiceErrorException(String msg, WorkflowServiceError error) {
        super(msg);
        serviceErrors = new ArrayList();
        serviceErrors.add(error);
    }
    
    
    public WorkflowServiceErrorException(String msg, Collection errors) {
        super(msg);
        setServiceErrors(errors);
    }
    
    public Collection getServiceErrors() {
        return serviceErrors;
    }

    public void setServiceErrors(Collection serviceErrors) {
        this.serviceErrors = serviceErrors;
    }

    public String toString() {
        if (serviceErrors != null) {
            return super.toString() + " " + serviceErrors;    
        } else {
            return super.toString() + " (no service errors)";
        }
    }
}