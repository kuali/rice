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
package edu.iu.uis.eden.clientapp;

import javax.xml.namespace.QName;

/**
 * Object to hold a service and it's qname when injecting services into the KSBConfigurer for overriding workflow services 
 * when using embedded workflow. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServiceHolder {

	private String localServiceName;
	private String serviceNameSpaceURI;
	private Object service;
	
	public String getLocalServiceName() {
		return localServiceName;
	}
	public void setLocalServiceName(String localServiceName) {
		this.localServiceName = localServiceName;
	}
	public Object getService() {
		return service;
	}
	public void setService(Object service) {
		this.service = service;
	}
	public String getServiceNameSpaceURI() {
		return serviceNameSpaceURI;
	}
	public void setServiceNameSpaceURI(String serviceNameSpaceURI) {
		this.serviceNameSpaceURI = serviceNameSpaceURI;
	}
	public QName getServiceName() {
		return new QName(getServiceNameSpaceURI(), getLocalServiceName());
//		if (getLocalServiceName() != null) {
//			return new QName(getLocalServiceName(), getServiceNameSpaceURI());	
//		} else {
//			return new QName(getServiceNameSpaceURI());
//		}
		
	}
}