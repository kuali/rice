package org.kuali.rice.kew.api.doctype;

import javax.xml.ws.WebFault;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowRuntimeException;

/**
 * An exception which is thrown when a DocumentType cannot be found.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebFault(name = "DocumentTypeNotFoundFault", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
public class DocumentTypeNotFoundException extends WorkflowRuntimeException {

	private static final long serialVersionUID = 5896411514876640531L;

	public DocumentTypeNotFoundException() {
		super();
	}

	public DocumentTypeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocumentTypeNotFoundException(String message) {
		super(message);
	}

	public DocumentTypeNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
