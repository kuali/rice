package org.kuali.rice.kew.api;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.CoreConstants.Versions;

public final class KewApiConstants {
	
    public static final String MACHINE_GENERATED_RESPONSIBILITY_ID = "0";
    public static final String ADHOC_REQUEST_RESPONSIBILITY_ID = "-1";
    public static final String EXCEPTION_REQUEST_RESPONSIBILITY_ID = "-2";
    public static final String SAVED_REQUEST_RESPONSIBILITY_ID = "-3";
    
    public static final int TITLE_MAX_LENGTH = 255;
    
    public static final String DOCUMENT_CONTENT_ELEMENT = "documentContent";
    public static final String ATTRIBUTE_CONTENT_ELEMENT = "attributeContent";
    public static final String SEARCHABLE_CONTENT_ELEMENT = "searchableContent";
    public static final String APPLICATION_CONTENT_ELEMENT = "applicationContent";
    public static final String DEFAULT_DOCUMENT_CONTENT = "<"+DOCUMENT_CONTENT_ELEMENT+"/>";
    public static final String DEFAULT_DOCUMENT_CONTENT2 = "<"+DOCUMENT_CONTENT_ELEMENT+"></"+DOCUMENT_CONTENT_ELEMENT+">";
	
	public static final class DocumentContentVersions {
		public static final int ROUTE_LEVEL = 0;
		public static final int NODAL = 1;
		public static final int CURRENT = NODAL;
	}
	
    public static final class Namespaces {

    	public static final String KEW_NAMESPACE_PREFIX = CoreConstants.Namespaces.ROOT_NAMESPACE_PREFIX + "/kew";

    	/**
    	 * Namespace for the kew module which is compatible with Kuali Rice 2.0.x.
    	 */
    	public static final String KEW_NAMESPACE_2_0 = KEW_NAMESPACE_PREFIX + "/" + Versions.VERSION_2_0;

    }

	
	private KewApiConstants() {
		throw new UnsupportedOperationException("Should never be called.");
	}

}
