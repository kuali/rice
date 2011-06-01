package org.kuali.rice.ksb.api.registry;

/**
 * Represents a service endpoint that has been published to the service registry.
 * Includes a reference to both {@link ServiceInfoContract} and
 * {@link ServiceDescriptorContract} instances which compose the two different
 * pieces of information about a service endpoint.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface ServiceEndpointContract {

	/**
	 * Returns the service information for this endpoint.
	 * 
	 * @return the service information for this endpoint, should never return null
	 */
	ServiceInfoContract getInfo();
	
	/**
	 * Returns the service descriptor for this endpoint.
	 * 
	 * @return the service descriptor for this endpoint, should never return null
	 */
	ServiceDescriptorContract getDescriptor();
	
}
