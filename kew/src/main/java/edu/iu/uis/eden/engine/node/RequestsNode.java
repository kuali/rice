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
package edu.iu.uis.eden.engine.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.RouteManagerException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routemodule.RouteModule;
import edu.iu.uis.eden.util.ClassDumper;

/**
 * A node which generates {@link ActionRequestValue} objects from a {@link RouteModule}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RequestsNode extends RequestActivationNode {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequestsNode.class);
	
	private static String SUPPRESS_POLICY_ERRORS_KEY = "_suppressPolicyErrorsRequestActivationNode";

	public SimpleResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
		DocumentRouteHeaderValue document = routeContext.getDocument();
		RouteNodeInstance nodeInstance = routeContext.getNodeInstance();
		RouteNode node = nodeInstance.getRouteNode();
		try {
//			refreshSearchableAttributes(routeContext);
			// while no routable actions are activated and there are more routeLevels to process
			if (nodeInstance.isInitial()) {
				// document = SpringServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
				if (LOG.isDebugEnabled()) {
					LOG.debug("RouteHeader info inside routing loop\n" + ClassDumper.dumpFields(document));
					LOG.debug("Looking for new actionRequests - routeLevel: " + node.getRouteNodeName());
				}
				boolean suppressPolicyErrors = isSupressingPolicyErrors(routeContext);
				boolean pastFinalApprover = isPastFinalApprover(document, nodeInstance);
				// routeContext.setDocument(document);
				List requests = getNewActionRequests(routeContext);
				// for mandatory routes, requests must be generated
				if ((requests.isEmpty()) && node.getMandatoryRouteInd().booleanValue() && ! suppressPolicyErrors) {
					LOG.warn("no requests generated for mandatory route - " + node.getRouteNodeName());
					throw new RouteManagerException("No requests generated for mandatory route " + node.getRouteNodeName() + ":" + node.getRouteMethodName(), routeContext);
				}
				// determine if we have any approve requests for FinalApprover checks
				boolean hasApproveRequest = false;
				for (Iterator iter = requests.iterator(); iter.hasNext();) {
					ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
					hasApproveRequest = actionRequest.isApproveOrCompleteRequest() || hasApproveRequest;
				}
				// if final approver route level and no approve request send to exception routing
				if (node.getFinalApprovalInd().booleanValue()) {
					// we must have an approve request generated if final approver level.
					if (!hasApproveRequest && ! suppressPolicyErrors) {
						throw new RuntimeException("No Approve Request generated after final approver");
					}
				} else if (pastFinalApprover) {
					// we can't allow generation of approve requests after final approver. This guys going to exception routing.
					if (hasApproveRequest && ! suppressPolicyErrors) {
						throw new RuntimeException("Approve Request generated after final approver");
					}
				}
			}
			return super.process(routeContext, routeHelper);
		} catch (Exception e) {
			LOG.error("Caught exception routing", e);
			throw new RouteManagerException(e.getMessage(), e, routeContext);
		}
	}

	/**
	 * @param routeLevel
	 *            Route level for which the action requests will be generated
	 * @param routeHeader
	 *            route header for which the action requests are generated
	 * @param saveFlag
	 *            if true the new action requests will be saved, if false they are not written to the db
	 * @return List of ActionRequests - NOTE they are only written to DB if saveFlag is set
	 * @throws WorkflowException
	 * @throws ResourceUnavailableException
	 */
	public List getNewActionRequests(RouteContext context) throws Exception {
		RouteNodeInstance nodeInstance = context.getNodeInstance();
		String routeMethodName = nodeInstance.getRouteNode().getRouteMethodName();
		LOG.debug("Looking for action requests in " + routeMethodName + " : " + nodeInstance.getRouteNode().getRouteNodeName());
		List newRequests = new ArrayList();
		try {
			RouteModule routeModule = KEWServiceLocator.getRouteModuleService().findRouteModule(nodeInstance.getRouteNode());
			List requests = routeModule.findActionRequests(context);
			for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
				ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
				actionRequest = KEWServiceLocator.getActionRequestService().initializeActionRequestGraph(actionRequest, context.getDocument(), nodeInstance);
                saveActionRequest(context, actionRequest);
				newRequests.add(actionRequest);
			}
		} catch (WorkflowException ex) {
			LOG.warn("Caught WorkflowException during routing", ex);
			throw new RouteManagerException(ex, context);
		}
		return newRequests;
	}

	/**
	 * Checks if the document has past the final approver node by walking backward through the previous node instances.
	 * Ignores any previous nodes that have been "revoked".
	 */
	private boolean isPastFinalApprover(DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance) {
		FinalApproverContext context = new FinalApproverContext();
		List revokedNodeInstances = KEWServiceLocator.getRouteNodeService().getRevokedNodeInstances(document);
		Set revokedNodeInstanceIds = new HashSet();
		for (Iterator iterator = revokedNodeInstances.iterator(); iterator.hasNext(); ) {
			RouteNodeInstance revokedNodeInstance = (RouteNodeInstance) iterator.next();
			revokedNodeInstanceIds.add(revokedNodeInstance.getRouteNodeInstanceId());
		}
		isPastFinalApprover(nodeInstance.getPreviousNodeInstances(), context, revokedNodeInstanceIds);
		return context.isPast;
	}

	private void isPastFinalApprover(List previousNodeInstances, FinalApproverContext context, Set revokedNodeInstanceIds) {
		if (previousNodeInstances != null && !previousNodeInstances.isEmpty()) {
			for (Iterator iterator = previousNodeInstances.iterator(); iterator.hasNext();) {
				if (context.isPast) {
					return;
				}
				RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
				if (context.inspected.contains(getKey(nodeInstance))) {
					continue;
				} else {
					context.inspected.add(getKey(nodeInstance));
				}
				if (Boolean.TRUE.equals(nodeInstance.getRouteNode().getFinalApprovalInd())) {
					// if the node instance has been revoked (by a Return To Previous action for example)
					// then we don't want to consider that node when we determine if we are past final
					// approval or not
					if (!revokedNodeInstanceIds.contains(nodeInstance.getRouteNodeInstanceId())) {
						context.isPast = true;
					}
					return;
				}
				isPastFinalApprover(nodeInstance.getPreviousNodeInstances(), context, revokedNodeInstanceIds);
			}
		}
	}

	/**
	 * The method will get a key value which can be used for comparison purposes. If the node instance has a primary key value, it will be returned. However, if the node instance has not been saved to the database (i.e. during a simulation) this method will return the node instance passed in.
	 */
	private Object getKey(RouteNodeInstance nodeInstance) {
		Long id = nodeInstance.getRouteNodeInstanceId();
		return (id != null ? (Object) id : (Object) nodeInstance);
	}

	private class FinalApproverContext {
		public Set inspected = new HashSet();
		public boolean isPast = false;
	}
	
	public static boolean isSupressingPolicyErrors(RouteContext routeContext) {
		Boolean suppressPolicyErrors = (Boolean)routeContext.getParameters().get(SUPPRESS_POLICY_ERRORS_KEY);
		if (suppressPolicyErrors == null || ! suppressPolicyErrors) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static void setSupressPolicyErrors(RouteContext routeContext) {
		routeContext.getParameters().put(SUPPRESS_POLICY_ERRORS_KEY, Boolean.TRUE);
	}
}
