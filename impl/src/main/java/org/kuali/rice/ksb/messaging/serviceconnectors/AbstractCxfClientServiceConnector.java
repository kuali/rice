/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging.serviceconnectors;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * Abstract superclass for CXF http client based service connectors.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class AbstractCxfClientServiceConnector extends AbstractServiceConnector {
	
	private static final Logger LOG = Logger.getLogger(AbstractCxfClientServiceConnector.class);
	private static final String HTTP_CLIENT_POLICY_BEAN = "httpClientPolicy";
	
	public AbstractCxfClientServiceConnector(ServiceInfo serviceInfo) {
		super(serviceInfo);
	}
	
	/**
	 * This method sets the client policy.  It assumes this is a CXF web client
	 * and uses the configured httpClientPolicy bean (if one exists).
	 * @param service
	 */
	@Override
	protected void applyClientPolicy(Object service) {
		// set http client policy
		HTTPClientPolicy policy = 
			(HTTPClientPolicy)KSBServiceLocator.getService(HTTP_CLIENT_POLICY_BEAN);
		if (policy != null) {
			try {
				Client cl = ClientProxy.getClient(service);
				((HTTPConduit)cl.getConduit()).setClient(policy);
			} catch (Exception e) {
				LOG.warn("unable to set HTTP client policy", e);
			}
		}

		super.applyClientPolicy(service);
	}
	
}
