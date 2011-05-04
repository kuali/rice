package org.kuali.rice.ksb.api.registry;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.mo.Versioned;

public interface ServiceHeaderContract extends Versioned {

	public Long getServiceEndpointId();

	public QName getServiceName();

	public String getEndpointUrl();
	
	public String getEndpointAlternateUrl();

	public String getApplicationNamespace();

	public String getServerIpAddress();
	
	public String getType();
	
	public String getServiceVersion();
	
	public ServiceEndpointStatus getStatus();

	public String getChecksum();
	
}
