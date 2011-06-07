package org.kuali.rice.ksb.api;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.api.bus.ServiceBus;
import org.kuali.rice.ksb.api.cache.RiceCacheAdministrator;
import org.kuali.rice.ksb.api.messaging.MessageHelper;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;

/**
 * A static service locator which aids in locating the various services that
 * form the Kuali Service Bus API.
 */
public class KsbApiServiceLocator {

	public static final String SERVICE_BUS = "rice.ksb.serviceBus";
	public static final String SERVICE_REGISTRY = "rice.ksb.serviceRegistry";
    public static final String CACHE_ADMINISTRATOR = "enCacheAdministrator";
    public static final String MESSAGE_HELPER = "rice.ksb.messageHelper";

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

    public static MessageHelper getMessageHelper() {
        return getService(MESSAGE_HELPER);
    }
}
