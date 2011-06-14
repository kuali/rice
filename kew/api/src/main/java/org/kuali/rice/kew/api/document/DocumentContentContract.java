package org.kuali.rice.kew.api.document;

public interface DocumentContentContract {

	String getDocumentId();
	
	String getApplicationContent();
	
	String getAttributeContent();
	
	String getSearchableContent();
	
	int getFormatVersion();
	
}
