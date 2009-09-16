/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerSideRemotedServiceHolder implements ServiceHolder {

	private Object injectedPojo;
	private Object service;
	private ServiceInfo serviceInfo;
	
	/**
	 * @param service
	 * @param entry
	 */
	public ServerSideRemotedServiceHolder(Object service, Object injectedPojo, ServiceInfo entry) {
		this.setService(service);
		this.setServiceInfo(entry);
		this.setInjectedPojo(injectedPojo);
	}

	public Object getInjectedPojo() {
		return this.injectedPojo;
	}

	public void setInjectedPojo(Object injectedPojo) {
		this.injectedPojo = injectedPojo;
	}
	public Object getService() {
		return this.service;
	}

	public ServiceInfo getServiceInfo() {
	    return this.serviceInfo;
	}


	public void setService(Object service) {
	    this.service = service;
	}


	public void setServiceInfo(ServiceInfo serviceInfo) {
	    this.serviceInfo = serviceInfo;
	}
}
