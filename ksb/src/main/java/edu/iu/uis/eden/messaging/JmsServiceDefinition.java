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

import javax.jms.ConnectionFactory;

import org.kuali.rice.config.ConfigurationException;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class JmsServiceDefinition extends ServiceDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4406697016217436247L;
	
	private ConnectionFactory connectionFactory;
	private String serviceInterface;
	
	@Override
	public void validate() {
		super.validate();
		//if interface is null grab the first one and use it
		if (getServiceInterface() == null) {
			if (this.getService().getClass().getInterfaces().length == 0) {
				throw new ConfigurationException("Service needs to implement interface to be exported as SOAP service");
			}
			setServiceInterface(this.getService().getClass().getInterfaces()[0].getName());
		}
		if (getBusSecurity() == null) {
			setBusSecurity(false);
		}
	}
	
	public ConnectionFactory getConnectionFactory() {
		return this.connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public String getServiceInterface() {
		return this.serviceInterface;
	}

	public void setServiceInterface(String interfaceName) {
		this.serviceInterface = interfaceName;
	}

	@Override
	public boolean isSame(ServiceDefinition serviceDefinition) {
		boolean same = super.isSame(serviceDefinition) && serviceDefinition instanceof JavaServiceDefinition; 
		if (! same) {
			return same;
		}
		return ((JavaServiceDefinition)serviceDefinition).getServiceInterface().equals(this.getServiceInterface());
	}
	
	
}