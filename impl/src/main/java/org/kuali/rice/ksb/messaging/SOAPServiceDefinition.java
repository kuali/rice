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
package org.kuali.rice.ksb.messaging;

import java.net.URL;
import java.net.URLEncoder;

import org.apache.derby.tools.sysinfo;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @since 0.9
 */
public class SOAPServiceDefinition extends ServiceDefinition {

	private static final long serialVersionUID = 5892163789061959602L;

	private String serviceInterface;
	private boolean jaxWsService = false;

	/**
	 * @return the jaxWsService
	 */
	public boolean isJaxWsService() {
		return this.jaxWsService;
	}

	/**
	 * @param jaxWsService
	 *            define service as jaxws service.
	 */
	public void setJaxWsService(boolean jaxWsService) {
		this.jaxWsService = jaxWsService;
	}

	/**
	 * Constructor that sets the bus security (i.e. digital signing) to FALSE by
	 * default.
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
				throw new ConfigurationException(
						"Service needs to implement interface to be exported as SOAP service");
			}
			setServiceInterface(this.getService().getClass().getInterfaces()[0]
					.getName());
		}
		if (getBusSecurity() == null) {
			setBusSecurity(false);
		}
	}

	@Override
	public boolean isSame(final ServiceDefinition serviceDefinition) {
		boolean same = super.isSame(serviceDefinition)
				&& serviceDefinition instanceof SOAPServiceDefinition;
		if (!same) {
			return same;
		}
		return ((SOAPServiceDefinition) serviceDefinition)
				.getServiceInterface().equals(this.getServiceInterface());
	}
}