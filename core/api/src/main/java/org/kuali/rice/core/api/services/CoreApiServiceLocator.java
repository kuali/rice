package org.kuali.rice.core.api.services;

import org.kuali.rice.core.api.namespace.NamespaceService;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;

public class CoreApiServiceLocator {
        public static final String NAMESPACE_SERVICE = "namespaceService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static NamespaceService getNamespaceService() {
        return getService(NAMESPACE_SERVICE);
    }
}
