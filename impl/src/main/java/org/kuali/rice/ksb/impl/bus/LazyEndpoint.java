package org.kuali.rice.ksb.impl.bus;

import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;
import org.kuali.rice.ksb.messaging.serviceconnectors.ServiceConnectorFactory;

public class LazyEndpoint implements Endpoint {

	private final Object lock = new Object();
	
	private final ServiceConfiguration serviceConfiguration;
	private volatile Object service;
	
	public LazyEndpoint(ServiceConfiguration serviceConfiguration) {
		if (serviceConfiguration == null) {
			throw new IllegalArgumentException("serviceConfiguration was null");
		}
		this.serviceConfiguration = serviceConfiguration;
	}
	
	@Override
	public ServiceConfiguration getServiceConfiguration() {
		return this.serviceConfiguration;
	}

	@Override
	public Object getService() {
		// double-checked locking idiom - see Effective Java, Item 71
		Object internalService = this.service;
		if (internalService == null) {
			synchronized (lock) {
				internalService = this.service;
				if (internalService == null) {
					this.service = internalService = ServiceConnectorFactory.getServiceConnector(serviceConfiguration).getService(); 
				}
			}
		}
		return internalService;
	}

}
