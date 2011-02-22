package org.kuali.rice.shareddata.api;

import org.kuali.rice.core.api.CoreConstants;

public final class SharedDataConstants {

	public final static class Namespaces {
    	public static final String CORE_NAMESPACE_PREFIX = CoreConstants.Namespaces.ROOT_NAMESPACE_PREFIX + "/shareddata";
    	public static final String CAMPUS_NAMESPACE_PREFIX = CORE_NAMESPACE_PREFIX + "/campus";
    	
    	public static final String MAJOR_VERSION = "v1_1";
    	
    	public static final String CAMPUS_NAMESPACE = CAMPUS_NAMESPACE_PREFIX + "/" + MAJOR_VERSION;
    }
}
