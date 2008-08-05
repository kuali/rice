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

import org.apache.log4j.Logger;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.WorkflowEngine;

/**
 * An XML services which is used to submit documents to the engine.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteDocumentMessageService implements KEWXMLService {

	private static final Logger LOG = Logger.getLogger(RouteDocumentMessageService.class);
	
	public void invoke(String xml) {
		try {
			RouteMessageXmlElement newElement = RouteMessageXmlElement.construct(xml);
			WorkflowEngine engine = KEWServiceLocator.getWorkflowEngine();
			engine.setRunPostProcessorLogic(newElement.runPostProcessor);
			engine.process(newElement.routeHeaderId, null);
		} catch (Exception e) {
			LOG.error(e);
			throw new WorkflowRuntimeException(e);
		}
	}
	
	public static class RouteMessageXmlElement {
		public static Long routeHeaderId;
		public static boolean runPostProcessor = true;
		public RouteMessageXmlElement(Long routeHeaderId) {
			this.routeHeaderId = routeHeaderId;
		}
		public RouteMessageXmlElement(Long routeHeaderId, boolean runPostProcessor) {
			this(routeHeaderId);
			this.runPostProcessor = runPostProcessor;
		}
		private static final String SPLIT = "::~~::";
		public static RouteMessageXmlElement construct(String content) {
			if (content.indexOf(SPLIT) != -1) {
				String[] values = content.split(SPLIT);
				return new RouteMessageXmlElement(Long.valueOf(values[0]),Boolean.valueOf(values[1]));
			} else {
				return new RouteMessageXmlElement(Long.valueOf(content));
			}
		}
		public String translate() {
			return routeHeaderId + SPLIT + String.valueOf(runPostProcessor);
		}
	}

}
