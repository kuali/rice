package org.kuali.rice.core.framework.services;

import org.kuali.rice.core.framework.parameter.ClientParameterService;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;

public class CoreFrameworkServiceLocator {

    public static final String CLIENT_PARAMETER_SERVICE = "clientParameterService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static ClientParameterService getClientParameterService() {
        return getService(CLIENT_PARAMETER_SERVICE);
    }
}
