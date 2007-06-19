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

import edu.iu.uis.eden.messaging.KEWHttpInvokerServiceExporter;
import edu.iu.uis.eden.messaging.ServerSideRemotedServiceHolder;
import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 *
 * @author rkirkend
 */
public class HttpInvokerServiceExporter implements ServiceExporter {

	private ServiceInfo serviceInfo;
	
	public HttpInvokerServiceExporter(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}
	
	public ServerSideRemotedServiceHolder getServiceExporter(Object serviceImpl) {
		KEWHttpInvokerServiceExporter service = new KEWHttpInvokerServiceExporter();
		service.setServiceInfo(getServiceInfo());
		service.setService(serviceImpl);
		service.afterPropertiesSet();
		return new ServerSideRemotedServiceHolder(service, getServiceInfo().getServiceDefinition().getService(), getServiceInfo());
	}

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}
}