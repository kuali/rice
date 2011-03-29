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
package org.kuali.rice.ksb.messaging;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.impl.resourceloader.ContextClassLoaderProxy;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JavaServiceDefinition extends ServiceDefinition {

	private static final long serialVersionUID = -5267226536419496719L;
	
	private List<String> serviceInterfaces = new ArrayList<String>();

	public List<String> getServiceInterfaces() {
		return this.serviceInterfaces;
	}
	public void setServiceInterfaces(List<String> serviceInterfaces) {
		this.serviceInterfaces = serviceInterfaces;
	}
	public void setServiceInterface(String serviceName) {
	    this.serviceInterfaces.add(serviceName);
	}
	public String getServiceInterface() {
		return this.serviceInterfaces.get(0);
	}
	
	@Override
	public void validate() {
		super.validate();
		if (this.serviceInterfaces == null || this.serviceInterfaces.isEmpty()) {
			Class[] interfaces = ContextClassLoaderProxy.getInterfacesToProxy(getService());
			for (Class serviceInterfaceClass : interfaces) {
			    this.serviceInterfaces.add(serviceInterfaceClass.getName());
			}
		}
		if (getBusSecurity() == null) {
			setBusSecurity(true);
		}
	}
	
	@Override
	public boolean isSame(ServiceDefinition serviceDefinition) {
		boolean same = serviceDefinition instanceof JavaServiceDefinition;
		if (! same) {
			return same;
		}
		same = super.isSame(serviceDefinition);
		if (! same) {
			return same;
		}
		for (String interfaceName : this.serviceInterfaces) {
			if (! ((JavaServiceDefinition)serviceDefinition).getServiceInterfaces().contains(interfaceName)) {
				return false;
			}
		}
		return same;
	}
	
	
}
