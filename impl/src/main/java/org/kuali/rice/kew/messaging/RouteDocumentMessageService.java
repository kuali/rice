/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.messaging;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.docsearch.service.SearchableAttributeProcessingService;
import org.kuali.rice.kew.engine.OrchestrationConfig;
import org.kuali.rice.kew.engine.OrchestrationConfig.EngineCapability;
import org.kuali.rice.kew.engine.WorkflowEngine;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;


/**
 * An XML services which is used to submit documents to the engine.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RouteDocumentMessageService implements KSBXMLService {

	private static final Logger LOG = Logger.getLogger(RouteDocumentMessageService.class);
	
	public void invoke(String xml) {
		try {
			RouteMessageXmlElement newElement = RouteMessageXmlElement.construct(xml);
			OrchestrationConfig config = new OrchestrationConfig(EngineCapability.STANDARD, newElement.runPostProcessor);
			WorkflowEngine engine = KEWServiceLocator.getWorkflowEngineFactory().newEngine(config);
			engine.process(newElement.documentId, null);
			if (newElement.shouldSearchAttributeIndex) {
				SearchableAttributeProcessingService searchableAttService = (SearchableAttributeProcessingService) MessageServiceNames.getSearchableAttributeService(KEWServiceLocator.getRouteHeaderService().getRouteHeader(newElement.documentId));
				searchableAttService.indexDocument(newElement.documentId);
			}
		} catch (Exception e) {
			LOG.error(e);
			throw new WorkflowRuntimeException(e);
		}
	}
	
	public static class RouteMessageXmlElement {
		public String documentId;
		public boolean runPostProcessor = true;
		public boolean shouldSearchAttributeIndex = false;
		public RouteMessageXmlElement(String documentId) {
			this.documentId = documentId;
		}
		public RouteMessageXmlElement(String documentId, boolean runPostProcessor) {
			this(documentId);
			this.runPostProcessor = runPostProcessor;
		}
		public RouteMessageXmlElement(String documentId, boolean runPostProcessor, boolean shouldIndex) {
			this(documentId, runPostProcessor);
			this.shouldSearchAttributeIndex = shouldIndex;
		}
		private static final String SPLIT = "::~~::";
		public static RouteMessageXmlElement construct(String content) {
			if (content.contains(SPLIT)) {
				String[] values = content.split(SPLIT);
				if (values.length == 3) {
					return new RouteMessageXmlElement(values[0], Boolean.valueOf(values[1]), Boolean.valueOf(values[2]));
				} else {
					return new RouteMessageXmlElement(values[0],Boolean.valueOf(values[1]));
				}
			} else {
				return new RouteMessageXmlElement(content);
			}
		}
		public String translate() {
			return documentId + SPLIT + String.valueOf(runPostProcessor) + SPLIT + String.valueOf(shouldSearchAttributeIndex);
		}
	}

}
