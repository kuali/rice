package org.kuali.rice.kew.api.document;

import javax.xml.ws.WebFault;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowRuntimeException;

@WebFault(name = "InvalidDocumentContentFault", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
public class InvalidDocumentContentException extends WorkflowRuntimeException {
    
    private static final long serialVersionUID = -7192699210643743641L;

    public InvalidDocumentContentException() {
        super();
    }
    
    public InvalidDocumentContentException(String message) {
        super(message);
    }

    public InvalidDocumentContentException(Throwable t) {
        super(t);
    }

    public InvalidDocumentContentException(String message, Throwable t) {
        super(message, t);
    }
}
