/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ksb.messaging.serviceexporters;


import org.kuali.rice.ksb.messaging.JavaServiceDefinition;
import org.kuali.rice.ksb.messaging.RESTServiceDefinition;
import org.kuali.rice.ksb.messaging.SOAPServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.service.KSBContextServiceLocator;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServiceExporterFactory {

	public static ServiceExporter getServiceExporter(ServiceInfo serviceInfo, KSBContextServiceLocator serviceLocator) {
		
		if (serviceInfo.getServiceDefinition() instanceof JavaServiceDefinition) {
			return new HttpInvokerServiceExporter(serviceInfo);
		} else if (serviceInfo.getServiceDefinition() instanceof SOAPServiceDefinition) {
			return new SOAPServiceExporter(serviceInfo, serviceLocator);
		} else if (serviceInfo.getServiceDefinition() instanceof RESTServiceDefinition) {
			return new RESTServiceExporter(serviceInfo, serviceLocator);
		}
		
		throw new RuntimeException("ServiceDefinition type not supported " + serviceInfo.getServiceDefinition());
	}
	
	
	
}
