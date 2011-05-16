package org.kuali.rice.ksb.messaging.serviceexporters;

import javax.xml.namespace.QName;

import org.kuali.rice.ksb.api.bus.ServiceDefinition;

/**
 * Managers service endpoints that are exported from this application
 * @author ewestfal
 *
 */
public interface ServiceExportManager {
	
	Object getService(QName serviceName);
	
	QName getServiceName(String url);
	
	void exportService(ServiceDefinition serviceDefinition);
	
	void removeService(QName serviceName);
	
}
