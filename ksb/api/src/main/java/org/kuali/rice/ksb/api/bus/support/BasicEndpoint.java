package org.kuali.rice.ksb.api.bus.support;

import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;

/**
 * TODO - javadoc me!
 */
public final class BasicEndpoint implements Endpoint {

	private final ServiceConfiguration serviceConfiguration;
	private final Object service;
	
	private BasicEndpoint(ServiceConfiguration serviceConfiguration, Object service) {
		if (serviceConfiguration == null) {
			throw new IllegalArgumentException("serviceConfiguration must not be null");
		}
		if (service == null) {
			throw new IllegalArgumentException("service must not be null");
		}
		this.serviceConfiguration = serviceConfiguration;
		this.service = service;
	}
	
	public static BasicEndpoint newEndpoint(ServiceConfiguration serviceConfiguration, Object service) {
		return new BasicEndpoint(serviceConfiguration, service);
	}
	
	@Override
	public ServiceConfiguration getServiceConfiguration() {
		return serviceConfiguration;
	}

	@Override
	public Object getService() {
		return service;
	}

}
