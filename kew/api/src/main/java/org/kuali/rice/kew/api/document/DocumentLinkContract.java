package org.kuali.rice.kew.api.document;

import org.kuali.rice.core.api.mo.common.Identifiable;

public interface DocumentLinkContract extends Identifiable {
	
	String getOriginatingDocumentId();
	
	String getDestinationDocumentId();
		
}

