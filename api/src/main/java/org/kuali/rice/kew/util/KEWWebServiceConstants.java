/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kew.util;

import org.kuali.rice.core.util.RiceConstants;


/**
 * This class is used to hold constants that are used when exposing services to the bus 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class KEWWebServiceConstants {

	public static final String MODULE_TARGET_NAMESPACE = RiceConstants.RICE_JAXWS_TARGET_NAMESPACE_BASE + "/kew";

	public static final class SimpleDocumentActionsWebService {
		public static final String WEB_SERVICE_NAME = "simpleDocumentActionsService";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService";
		public static final String WEB_SERVICE_PORT = "SimpleDocumentActionsWebServicePort";
		
		private SimpleDocumentActionsWebService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class WorkflowUtility {
		public static final String WEB_SERVICE_NAME = "WorkflowUtilityServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kew.service.WorkflowUtility";
		public static final String WEB_SERVICE_PORT = "WorkflowUtilityPort";
		
		private WorkflowUtility() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	private KEWWebServiceConstants() {
		throw new UnsupportedOperationException("do not call");
	}
}
