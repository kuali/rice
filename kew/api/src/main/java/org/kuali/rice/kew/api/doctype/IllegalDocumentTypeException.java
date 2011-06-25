package org.kuali.rice.kew.api.doctype;

import javax.xml.ws.WebFault;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowRuntimeException;

@WebFault(name = "IllegalDocumentTypeException", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
public class IllegalDocumentTypeException extends WorkflowRuntimeException {

	private static final long serialVersionUID = -3242530105155693657L;

	public IllegalDocumentTypeException() {
		super();
	}

	public IllegalDocumentTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalDocumentTypeException(String message) {
		super(message);
	}

	public IllegalDocumentTypeException(Throwable cause) {
		super(cause);
	}
	
}
