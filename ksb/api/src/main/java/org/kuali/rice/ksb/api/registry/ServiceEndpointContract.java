package org.kuali.rice.ksb.api.registry;

public interface ServiceEndpointContract {

	Long getId();
	ServiceHeaderContract getHeader();
	ServiceDescriptorContract getDescriptor();
	
}
