package org.kuali.rice.kew.api.document;

import javax.xml.ws.WebFault;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowRuntimeException;

@WebFault(name = "DocumentCreationException", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
public class DocumentCreationException extends WorkflowRuntimeException {

	private static final long serialVersionUID = -3242530105155693657L;

	public DocumentCreationException() {
		super();
	}

	public DocumentCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocumentCreationException(String message) {
		super(message);
	}

	public DocumentCreationException(Throwable cause) {
		super(cause);
	}
	
}
