package org.kuali.rice.ksb.api.bus.services;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.api.bus.ServiceBus;
import org.kuali.rice.ksb.api.cache.RiceCacheAdministrator;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;

/**
 * TODO...
 */
public class KsbApiServiceLocator {

	public static final String SERVICE_BUS = "rice.ksb.serviceBus";
	public static final String SERVICE_REGISTRY = "rice.ksb.serviceRegistry";
    public static final String CACHE_ADMINISTRATOR = "enCacheAdministrator";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static ServiceBus getServiceBus() {
        return getService(SERVICE_BUS);
    }
    
    public static ServiceRegistry getServiceRegistry() {
    	return getService(SERVICE_REGISTRY);
    }

    public static RiceCacheAdministrator getCacheAdministrator() {
        return getService(CACHE_ADMINISTRATOR);
    }
}
