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

import org.apache.log4j.Logger;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * An XML services which is used to submit documents to the engine.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteDocumentMessageService implements KEWXMLService {

	private static final Logger LOG = Logger.getLogger(RouteDocumentMessageService.class);
	
	public void invoke(String xml) {
		try {
			Long routeHeaderId = new Long(xml);
			KEWServiceLocator.getWorkflowEngine().process(routeHeaderId, null);
		} catch (Exception e) {
			LOG.error(e);
			throw new WorkflowRuntimeException(e);
		}
	}

}
