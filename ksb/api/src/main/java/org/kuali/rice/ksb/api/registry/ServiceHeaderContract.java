package org.kuali.rice.ksb.api.registry;

import org.kuali.rice.core.api.mo.common.Versioned;

import javax.xml.namespace.QName;

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
