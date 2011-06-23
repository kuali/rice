package org.kuali.rice.kew.api.document;

import java.util.Map;

import org.joda.time.DateTime;

public interface DocumentContract {
    
	String getDocumentId();

	DocumentStatus getStatus();

	DateTime getDateCreated();

	DateTime getDateLastModified();

	DateTime getDateApproved();

	DateTime getDateFinalized();

	String getTitle();

	String getApplicationDocumentId();

	String getInitiatorPrincipalId();

	String getRoutedByPrincipalId();

	String getDocumentTypeName();

	String getDocumentTypeId();

	String getDocumentHandlerUrl();

	String getApplicationDocumentStatus();

	DateTime getApplicationDocumentStatusDate();

	Map<String, String> getVariables();
	
}
