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
package edu.iu.uis.eden.messaging;

/**
 * Holds the reference to an endpoint of a service as well as the {@link ServiceInfo}
 * that defines the service.
 *
 * @author rkirkend
 */
public class RemotedServiceHolder {
	
	private Object service;
	private ServiceInfo serviceInfo;
	
	public RemotedServiceHolder(Object service, ServiceInfo entry) {
		this.setService(service);
		this.setServiceInfo(entry);
	}

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

	public void setServiceInfo(ServiceInfo entry) {
		this.serviceInfo = entry;
	}

	public Object getService() {
		return this.service;
	}

	public void setService(Object service) {
		this.service = service;
	}
	
}