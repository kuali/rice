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

import org.kuali.rice.config.ConfigurationException;


/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version $Revision: 1.3 $ $Date: 2007-12-03 02:51:28 $
 * @since 0.9
 */
public class SOAPServiceDefinition extends ServiceDefinition {

	private static final long serialVersionUID = 5892163789061959602L;

	private String serviceInterface;
	
	/**
	 * Constructor that sets the bus security (i.e. digital signing) to FALSE by default.
	 */
	public SOAPServiceDefinition() {
	    super(Boolean.TRUE);
	}

	public String getServiceInterface() {
		return this.serviceInterface;
	}

	public void setServiceInterface(final String serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public void validate() {
		super.validate();
		// if interface is null grab the first one and use it
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

	@Override
	public boolean isSame(final ServiceDefinition serviceDefinition) {
		boolean same = super.isSame(serviceDefinition) && serviceDefinition instanceof SOAPServiceDefinition;
		if (!same) {
			return same;
		}
		return ((SOAPServiceDefinition) serviceDefinition).getServiceInterface().equals(this.getServiceInterface());
	}
}