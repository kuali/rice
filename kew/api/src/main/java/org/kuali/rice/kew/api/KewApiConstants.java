package org.kuali.rice.kew.api;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.CoreConstants.Versions;

public final class KewApiConstants {
	
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
