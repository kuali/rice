package org.kuali.rice.krad.service;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.service.util.OjbCollectionHelper;

/**
 * Service locator for the KRAD Service Impl Module
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KRADServiceLocatorInternal {
    public static final String OJB_COLLECTION_HELPER = "ojbCollectionHelper";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static OjbCollectionHelper getOjbCollectionHelper() {
        return (OjbCollectionHelper) getService(OJB_COLLECTION_HELPER);
    }
}
