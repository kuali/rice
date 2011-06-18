package org.kuali.rice.kew.api.action;

import javax.xml.ws.WebFault;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowRuntimeException;

@WebFault(name = "InvalidActionTakenFault", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
public class InvalidActionTakenException extends WorkflowRuntimeException {

	private static final long serialVersionUID = 4282067592790143952L;

	public InvalidActionTakenException() {
		super();
	}

	public InvalidActionTakenException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidActionTakenException(String message) {
		super(message);
	}

	public InvalidActionTakenException(Throwable cause) {
		super(cause);
	}

}
