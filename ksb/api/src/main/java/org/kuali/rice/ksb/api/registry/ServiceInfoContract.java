package org.kuali.rice.ksb.api.registry;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.mo.common.Versioned;

public interface ServiceInfoContract extends Versioned {

	public String getServiceId();

	public QName getServiceName();

	public String getEndpointUrl();
	
	public String getInstanceId();
	
	public String getApplicationNamespace();

	public String getServerIpAddress();
	
	public String getType();
	
	public String getServiceVersion();
	
	public ServiceEndpointStatus getStatus();
	
	public String getServiceDescriptorId();

	public String getChecksum();
	
}
