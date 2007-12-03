/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.messaging.serviceconnectors;

import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.security.credentials.CredentialsSource;
import org.kuali.rice.security.credentials.CredentialsSourceFactory;

import edu.iu.uis.eden.messaging.JavaServiceDefinition;
import edu.iu.uis.eden.messaging.JmsServiceDefinition;
import edu.iu.uis.eden.messaging.SOAPServiceDefinition;
import edu.iu.uis.eden.messaging.ServiceDefinition;
import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * Constructs a ServiceConnector based on the provided ServiceInfo/ServiceDefinition. Connects that ServiceConnector to the
 * appropriate CredentialsSource.
 * <p>
 * ServiceConnector will fail if a CredentialsSource for the Service Definition cannot be located.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version $Revision: 1.3 $ $Date: 2007-12-03 02:51:30 $
 * @since 0.9
 * 
 * @see ServiceConnector
 * @see ServiceInfo
 * @see ServiceDefinition
 * @see CredentialsSource
 */
public class ServiceConnectorFactory {

    public static ServiceConnector getServiceConnector(final ServiceInfo serviceInfo) {
	final ServiceDefinition serviceDefinition = serviceInfo.getServiceDefinition();
	final CredentialsSourceFactory credentialsSourceFactory = (CredentialsSourceFactory) Core.getCurrentContextConfig()
		.getObjects().get(Config.CREDENTIALS_SOURCE_FACTORY);
	final CredentialsSource credentialsSource = credentialsSourceFactory != null ? credentialsSourceFactory
		.getCredentialsForType(serviceDefinition.getCredentialsType()) : null;
	ServiceConnector serviceConnector = null;

	if (serviceDefinition.getCredentialsType() != null && credentialsSource == null) {
	    throw new RiceRuntimeException(
		    "Service requires credentials but no factory or CredentialsSource could be located.");
	}

	// if set in local mode then preempt any protocol connectors
	if (Core.getCurrentContextConfig().getDevMode()) {
	    serviceConnector = new BusLocalConnector(serviceInfo);
	} else if (serviceDefinition instanceof JavaServiceDefinition) {
	    serviceConnector = new HttpInvokerConnector(serviceInfo);
	} else if (serviceDefinition instanceof SOAPServiceDefinition) {
	    serviceConnector = new SOAPConnector(serviceInfo);
	} else if (serviceDefinition instanceof JmsServiceDefinition) {
	    serviceConnector = new JmsConnector(serviceInfo);
	}

	if (serviceConnector == null) {
	    throw new RiceRuntimeException("Don't support service type of " + serviceInfo.getServiceDefinition());
	}

	serviceConnector.setCredentialsSource(credentialsSource);

	return serviceConnector;
    }

    
}