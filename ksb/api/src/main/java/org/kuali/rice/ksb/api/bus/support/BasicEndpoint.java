package org.kuali.rice.ksb.api.bus.support;

import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;

/**
 * A simple immutable implementation of an {@link Endpoint} which simply
 * wraps a {@link ServiceConfiguration} and it's associated service implementation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
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
	
	/**
	 * Constructs a new basic endpoint from the given service configuration and
	 * service instance.
	 * 
	 * @param serviceConfiguration the service configuration to include in this endpoint
	 * @param service the service implementation instance to include in this endpoint
	 * 
	 * @return the constructed {@code BasicEndpoint} which contains the given
	 * configuration and service, will never return null
	 * 
	 * @throws IllegalArgumentException if either serviceConfiguration or service are null
	 */
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
