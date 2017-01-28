/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kew.impl.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.actionrequest.service.impl.NotificationSuppression;
import org.kuali.rice.kew.api.document.DocumentRefreshQueue;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.engine.OrchestrationConfig;
import org.kuali.rice.kew.engine.OrchestrationConfig.EngineCapability;
import org.kuali.rice.kew.engine.RouteHelper;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.service.RouteNodeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.PerformanceLogger;


/**
 * A service which effectively "refreshes" and requeues a document.  It first deletes any
 * pending action requests on the documents and then requeues the document for standard routing.
 * Addionally, it adds duplicate notification suppression state to RouteNodeInstanceS for 
 * which ActionRequestS will be regenerated. 
 * 
 * <p>Intended to be called async and wired that way in server/client spring beans.</p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentRefreshQueueImpl implements DocumentRefreshQueue {
	
	private RouteHelper helper = new RouteHelper();
    
	/**
	 * Requeues a document, and sets notification suppression data
	 * 
	 * @see org.kuali.rice.kew.api.document.DocumentRefreshQueue#refreshDocument(java.lang.String)
	 */
	@Override
	public void refreshDocument(String documentId) {
		if (StringUtils.isBlank(documentId)) {
            throw new RiceIllegalArgumentException("documentId is null or blank");
        }

        PerformanceLogger performanceLogger = new PerformanceLogger();
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId);
        Collection<RouteNodeInstance> activeNodes = getRouteNodeService().getActiveNodeInstances(documentId);
        List<ActionRequestValue> requestsToDelete = new ArrayList<ActionRequestValue>();
        
		NotificationSuppression notificationSuppression = new NotificationSuppression();
		
        for (RouteNodeInstance nodeInstance : activeNodes) {
            // only "requeue" if we're dealing with a request activation node
            if (helper.isRequestActivationNode(nodeInstance.getRouteNode())) {
            	List<ActionRequestValue> deletesForThisNode = 
            		getActionRequestService().findPendingRootRequestsByDocIdAtRouteNode(documentId, nodeInstance.getRouteNodeInstanceId());

            	for (ActionRequestValue deleteForThisNode : deletesForThisNode) {
                    // check either the request or its first present child request to see if it is system generated
                    boolean containsRoleOrRuleRequests = deleteForThisNode.isRouteModuleRequest();
                    if (!containsRoleOrRuleRequests) {
                        if (CollectionUtils.isNotEmpty(deleteForThisNode.getChildrenRequests())) {
                            containsRoleOrRuleRequests = deleteForThisNode.getChildrenRequests().get(0).isRouteModuleRequest();
                        }
                    }

                    if (containsRoleOrRuleRequests) {
                        // remove all route or rule system generated requests
                        requestsToDelete.add(deleteForThisNode);

                        // suppress duplicate notifications
            			notificationSuppression.addNotificationSuppression(nodeInstance, deleteForThisNode);
            		}
            	}

                // this will trigger a regeneration of requests
                nodeInstance.setInitial(true);
                getRouteNodeService().save(nodeInstance);
            }
        }
        for (ActionRequestValue requestToDelete : requestsToDelete) {
            getActionRequestService().deleteActionRequestGraphNoOutbox(requestToDelete);
        }
        try {
            OrchestrationConfig config = new OrchestrationConfig(EngineCapability.STANDARD);
        	KEWServiceLocator.getWorkflowEngineFactory().newEngine(config).process(documentId, null);
        } catch (Exception e) {
        	throw new WorkflowRuntimeException(e);
        }
        performanceLogger.log("Time to run DocumentRequeuer for document " + documentId);	
	}

    private ActionRequestService getActionRequestService() {
        return KEWServiceLocator.getActionRequestService();
    }
    
    private RouteNodeService getRouteNodeService() {
        return KEWServiceLocator.getRouteNodeService();
    }
}
