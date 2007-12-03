/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.server.WorkflowDocumentActions;
import edu.iu.uis.eden.server.WorkflowUtility;
import edu.iu.uis.eden.util.Utilities;

/**
 * Assists in locating the WorkflowUtility and WorkflowDocumentActions services from a Workflow client.
 * It's behavior is to check the workflow client configuration (workfow-config.properties) and if it
 * finds a webservice url configured it connects to the services via web services.  Otherwise it will
 * attempt to use locally running services.  In order for local running services to be functional,
 * this kind of configuration must be used only in an application running inside of the same JVM as
 * workflow, such as the workflow core or a workflow client plugin.
 *
 * This class stores configured services keyed off of the current Thread's context classloader (provided
 * it has one).  Therefore, multiple Workflow plugins can configure their workflow client access differently.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClientServiceLocator {

	private static final Logger LOG = Logger.getLogger(ClientServiceLocator.class);

	public static WorkflowUtility getWorkflowUtility() throws WorkflowException {
		WorkflowUtility workflowUtility = null;
		String clientProtocol = getClientProtocol();
        if (EdenConstants.LOCAL_CLIENT_PROTOCOL.equals(clientProtocol)) {
            LOG.debug("Connecting to locally running instance of WorkflowUtility");
            workflowUtility = KEWServiceLocator.getWorkflowUtilityService();
        } else if (EdenConstants.WEBSERVICE_CLIENT_PROTOCOL.equals(clientProtocol)) {
            LOG.debug("Connecting to WorkflowUtility web service at url " + getClientConfig().getBaseWebServiceURL());
//            workflowUtility = WebServiceLocator.getWorkflowUtilityProxy();
            throw new UnsupportedOperationException("This object does not currently support Web services");
        } else {
        	throw new WorkflowException("Did not recognize the configured client.protocol: '" + clientProtocol + "'");
        }
        return workflowUtility;
	}

	public static WorkflowDocumentActions getWorkflowDocumentActions() throws WorkflowException {
        WorkflowDocumentActions workflowDocumentActions = null;
		String clientProtocol = getClientProtocol();
        if (EdenConstants.LOCAL_CLIENT_PROTOCOL.equals(clientProtocol)) {
            LOG.debug("Connecting to locally running instance of WorkflowDocumentActions");
            workflowDocumentActions = KEWServiceLocator.getWorkflowDocumentActionsService();
        } else if (EdenConstants.WEBSERVICE_CLIENT_PROTOCOL.equals(clientProtocol)) {
            LOG.debug("Connecting to WorkflowDocumentActions web service at url " + getClientConfig().getBaseWebServiceURL());
//            workflowDocumentActions = WebServiceLocator.getWorkflowDocumentActionsProxy();
            throw new UnsupportedOperationException("This object does not currently support web services");
        } else {
        	throw new WorkflowException("Did not recognize the configured client.protocol: '" + clientProtocol + "'");
        }
        return workflowDocumentActions;
    }

	public static synchronized Config getClientConfig() throws WorkflowException {
		Config config = Core.getCurrentContextConfig();
		if (config == null) {
			config = new ClientConfig();
			try {
				config.parseConfig();
			} catch (IOException e) {
				throw new WorkflowException(e);
			}
		}
		return config;
	}

	/**
	 * Returns the configured client protocol.  If there is no configured client protocol
	 * then the default is webservices.
	 */
	private static String getClientProtocol() throws WorkflowException {
		String clientProtocol = getClientConfig().getClientProtocol();
		if (Utilities.isEmpty(clientProtocol)) {
			clientProtocol = EdenConstants.WEBSERVICE_CLIENT_PROTOCOL;
		}
		return clientProtocol;
	}

}