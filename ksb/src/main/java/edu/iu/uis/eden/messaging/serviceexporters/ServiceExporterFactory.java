/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.messaging.serviceexporters;


import edu.iu.uis.eden.messaging.JavaServiceDefinition;
import edu.iu.uis.eden.messaging.JmsServiceDefinition;
import edu.iu.uis.eden.messaging.SOAPServiceDefinition;
import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServiceExporterFactory {

	public static ServiceExporter getServiceExporter(ServiceInfo serviceInfo) {
		
		if (serviceInfo.getServiceDefinition() instanceof JavaServiceDefinition) {
			return new HttpInvokerServiceExporter(serviceInfo);
		} else if (serviceInfo.getServiceDefinition() instanceof SOAPServiceDefinition) {
			return new SOAPServiceExporter(serviceInfo);
		} else if (serviceInfo.getServiceDefinition() instanceof JmsServiceDefinition) {
			return new JmsExporter(serviceInfo);
		}
		
		throw new RuntimeException("ServiceDefinition type not supported " + serviceInfo.getServiceDefinition());
	}
	
	
	
}
