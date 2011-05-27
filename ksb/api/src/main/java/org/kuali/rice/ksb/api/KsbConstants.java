package org.kuali.rice.ksb.api;

import org.kuali.rice.core.api.CoreConstants;

public final class KsbConstants {

	public static final String KSB_MODULE_NAME = "KSB";
	
	public static final String HTTP_INVOKER_SERVICE_TYPE = "httpInvoker";
	public static final String SOAP_SERVICE_TYPE = "SOAP";
	public static final String REST_SERVICE_TYPE = "REST";
	
	public static final class Namespaces {	
        public static final String KSB_NAMESPACE_2_0 = CoreConstants.Namespaces.ROOT_NAMESPACE_PREFIX + "/ksb/" + CoreConstants.Versions.VERSION_2_0;
	}
	
}
