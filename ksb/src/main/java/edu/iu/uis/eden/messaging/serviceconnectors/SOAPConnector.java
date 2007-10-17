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
package edu.iu.uis.eden.messaging.serviceconnectors;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.kuali.bus.security.soap.CredentialsOutHandler;
import org.kuali.rice.config.xfire.WorkflowXFireWSS4JInHandler;
import org.kuali.rice.config.xfire.WorkflowXFireWSS4JOutHandler;

import edu.iu.uis.eden.messaging.SOAPServiceDefinition;
import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @since 0.9
 */
public class SOAPConnector extends AbstractServiceConnector {

	public SOAPConnector(final ServiceInfo serviceInfo) {
		super(serviceInfo);
	}

	public Object getService() throws Exception {
		ObjectServiceFactory serviceFactory = new ObjectServiceFactory(new AegisBindingProvider());
		XFireProxyFactory proxyFactory = new XFireProxyFactory();
		Service serviceModel = serviceFactory.create(Class.forName(((SOAPServiceDefinition) getServiceInfo().getServiceDefinition()).getServiceInterface()));
		Object service = proxyFactory.create(serviceModel, getServiceInfo().getEndpointUrl());
		configureClient(Client.getInstance(service));
		return getServiceProxyWithFailureMode(service, this.getServiceInfo());
	}

	protected void configureClient(final Client client) {
		client.addOutHandler(new DOMOutHandler());
		client.addOutHandler(new org.codehaus.xfire.util.LoggingHandler());

		if (getCredentialsSource() != null) {
			client.addOutHandler(new CredentialsOutHandler(getCredentialsSource(), getServiceInfo()));
		}

		client.addOutHandler(new WorkflowXFireWSS4JOutHandler(getServiceInfo()));
		client.addInHandler(new DOMInHandler());
		client.addInHandler(new org.codehaus.xfire.util.LoggingHandler());
		client.addInHandler(new WorkflowXFireWSS4JInHandler(getServiceInfo()));
	}
}