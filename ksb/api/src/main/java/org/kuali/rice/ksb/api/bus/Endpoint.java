package org.kuali.rice.ksb.api.bus;

/**
 * An {@code Endpoint} contains a reference to the {@link ServiceConfiguration}
 * for a service as well as a proxy to the service endpoint that can be invoked.
 * 
 * <p>This service can be cast to the appropriate service interface in order to
 * invoke the desired operations.
 */
public interface Endpoint {

	/**
	 * Returns the service configuration information for this endpoint.
	 * 
	 * @return the service configuration for this endpoint, should never return null
	 */
	ServiceConfiguration getServiceConfiguration();
	
	/**
	 * Returns a reference to the service that can be invoked through this
	 * endpoint.  This could potentially be a proxy to the service (in the case
	 * that the endpoint is pointing to a remote service) so calling code
	 * should cast this object to the appropriate service interface before
	 * using.
	 * 
	 * <p>It is the client's responsibility to ensure that they are casting the
	 * service to the correct interface(s) based on their knowledge of what
	 * interface the service should implement.
	 * 
	 * @return a reference to the service object which can be used to invoke
	 * operations against the endpoint, should never return null
	 */
	Object getService();
	
}
