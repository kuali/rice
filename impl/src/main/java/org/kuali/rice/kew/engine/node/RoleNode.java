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
package org.kuali.rice.kew.engine.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.RouteHelper;
import org.kuali.rice.kew.exception.ResourceUnavailableException;
import org.kuali.rice.kew.exception.RouteManagerException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.role.RoleRouteModule;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routemodule.RouteModule;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.ClassDumper;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * A node implementation which provides integration with KIM Roles for routing.
 * Essentially extends RequestsNode and provides a custom RouteModule
 * implementation.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class RoleNode extends RequestsNode {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger( RoleNode.class );

	@Override
	protected RouteModule getRouteModule(RouteContext context) throws Exception {
		return new RoleRouteModule();
	}
	
	/**
	 * @see org.kuali.rice.kew.engine.node.RequestsNode#processCustom(org.kuali.rice.kew.engine.RouteContext, org.kuali.rice.kew.engine.RouteHelper)
	 */
	@Override
	protected boolean processCustom(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
		DocumentRouteHeaderValue document = routeContext.getDocument();
		RouteNodeInstance nodeInstance = routeContext.getNodeInstance();
		RouteNode node = nodeInstance.getRouteNode();
		// while no routable actions are activated and there are more
		// routeLevels to process
		if ( nodeInstance.isInitial() ) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "RouteHeader info inside routing loop\n"
						+ ClassDumper.dumpFields( routeContext.getDocument() ) );
				LOG.debug( "Looking for new actionRequests - routeLevel: "
						+ node.getRouteNodeName() );
			}
			boolean suppressPolicyErrors = isSupressingPolicyErrors( routeContext );
			List<ActionRequestValue> requests = getNewActionRequests( routeContext );
// Debugging code to force an empty action request				
//				if ( document.getDocumentType().getName().equals( "SACC" ) ) {
//					LOG.fatal( "DEBUGGING CODE IN PLACE - SACC DOCUMENT ACTION REQUESTS CLEARED" );
//					requests.clear();
//				}
			// for mandatory routes, requests must be generated
			if ( requests.isEmpty() && !suppressPolicyErrors) {
//				KimResponsibilityInfo resp = getFirstResponsibilityWithMandatoryRouteFlag( document, node );
//				if ( resp != null ) {
//					throw new RouteManagerException( "No requests generated for KIM Responsibility-based mandatory route.\n" +
//							"Document Id:    " + document.getRouteHeaderId() + "\n" +
//							"DocumentType:   " + document.getDocumentType().getName() + "\n" +
//							"Route Node:     " + node.getRouteNodeName() + "\n" + 
//							"Responsibility: " + resp,
//							routeContext );
//				}
			}
			// determine if we have any approve requests for FinalApprover
			// checks
			if ( !suppressPolicyErrors ) {				
				verifyFinalApprovalRequest( document, requests, nodeInstance, routeContext );
			}
		}
		return true; // to indicate custom processing performed
	}
	
	/**
	 * Checks for any mandatory route responsibilities for the given document type and node.
	 * 
	 * Stops once it finds a responsibility for the document and node.
	 */	
	protected KimResponsibilityInfo getFirstResponsibilityWithMandatoryRouteFlag( DocumentRouteHeaderValue document, RouteNode node ) {
		// iterate over the document hierarchy
		// gather responsibilities - merge based on route level
		//Map<String,Boolean>
		Map<String,String> searchCriteria = new HashMap<String,String>();
		searchCriteria.put("template.namespaceCode", KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE);
		searchCriteria.put("template.name", KEWConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME);
		searchCriteria.put("active", "Y");
		DocumentType docType = document.getDocumentType();
		while ( docType != null ) {
			searchCriteria.put("detailCriteria", getDetailCriteriaString( document.getDocumentType().getName(), node.getRouteNodeName() ) );
			try {
				List<? extends KimResponsibilityInfo> responsibilities = KIMServiceLocator.getResponsibilityService().lookupResponsibilityInfo( searchCriteria, false );
				// once we find a responsibility, stop, since this overrides any parent 
				// responsibilities for this node
				if ( !responsibilities.isEmpty() ) {
					// if any has required=true - return true
					for ( KimResponsibilityInfo resp : responsibilities ) {
						if ( Boolean.parseBoolean( resp.getDetails().get( KimAttributes.REQUIRED ) ) ) {
							return resp;
						}
					}
					return null;
				}
			} catch ( Exception ex ) {
				LOG.error( "Problem looking up responsibilities to check mandatory route.  Criteria: " +searchCriteria, ex );
				return null;
			}
		}

		return null;
	}

	protected String getDetailCriteriaString( String documentTypeName, String routeNodeName ) {
		return KimAttributes.DOCUMENT_TYPE_NAME+"="+documentTypeName
				+ ","
				+ KimAttributes.ROUTE_NODE_NAME+"="+routeNodeName
//				+ ","
//				+ KimAttributes.REQUIRED+"=true"
				;
	}
	
	/**
	 * Activates the action requests that are pending at this routelevel of the
	 * document. The requests are processed by priority and then request ID. It
	 * is implicit in the access that the requests are activated according to
	 * the route level above all.
	 * <p>
	 * FYI and acknowledgment requests do not cause the processing to stop. Only
	 * action requests for approval or completion cause the processing to stop
	 * and then only for route level with a serialized activation policy. Only
	 * requests at the current document's current route level are activated.
	 * Inactive requests at a lower level cause a routing exception.
	 * <p>
	 * Exception routing and adhoc routing are processed slightly differently.
	 * 
	 * @return True if the any approval actions were activated.
	 * @throws ResourceUnavailableException
	 * @throws WorkflowException
	 */
	public boolean activateRequests(RouteContext context, DocumentRouteHeaderValue document,
			RouteNodeInstance nodeInstance) throws WorkflowException {
		MDC.put( "docID", document.getRouteHeaderId() );
		PerformanceLogger performanceLogger = new PerformanceLogger( document.getRouteHeaderId() );
		List<ActionItem> generatedActionItems = new ArrayList<ActionItem>();
		List<ActionRequestValue> requests = new ArrayList<ActionRequestValue>();
		if ( context.isSimulation() ) {
			for ( ActionRequestValue ar : context.getDocument().getActionRequests() ) {
				// logic check below duplicates behavior of the
				// ActionRequestService.findPendingRootRequestsByDocIdAtRouteNode(routeHeaderId,
				// routeNodeInstanceId) method
				if ( ar.getCurrentIndicator()
						&& (KEWConstants.ACTION_REQUEST_INITIALIZED.equals( ar.getStatus() ) || KEWConstants.ACTION_REQUEST_ACTIVATED
								.equals( ar.getStatus() ))
						&& ar.getNodeInstance().getRouteNodeInstanceId().equals(
								nodeInstance.getRouteNodeInstanceId() )
						&& ar.getParentActionRequest() == null ) {
					requests.add( ar );
				}
			}
			requests.addAll( context.getEngineState().getGeneratedRequests() );
		} else {
			requests = KEWServiceLocator.getActionRequestService()
					.findPendingRootRequestsByDocIdAtRouteNode( document.getRouteHeaderId(),
							nodeInstance.getRouteNodeInstanceId() );
		}
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Pending Root Requests " + requests.size() );
		}
		boolean requestActivated = activateRequestsCustom( context, requests, generatedActionItems,
				document, nodeInstance );
		// now let's send notifications, since this code needs to be able to
		// activate each request individually, we need
		// to collection all action items and then notify after all have been
		// generated
		if ( !context.isSimulation() ) {
			KEWServiceLocator.getNotificationService().notify( generatedActionItems );
		}
		performanceLogger.log( "Time to activate requests." );
		return requestActivated;
	}

	protected boolean activateRequestsCustom(RouteContext context,
			List<ActionRequestValue> requests, List<ActionItem> generatedActionItems,
			DocumentRouteHeaderValue document, RouteNodeInstance nodeInstance)
			throws WorkflowException {
		// FIXME: won't this undo any ordering from the role type service?
		Collections.sort( requests, new Utilities.PrioritySorter() );
		String activationType = nodeInstance.getRouteNode().getActivationType();
		boolean isParallel = KEWConstants.ROUTE_LEVEL_PARALLEL.equals( activationType );
		boolean requestActivated = false;
		String groupToActivate = null;
		for ( ActionRequestValue request : requests ) {
			if ( requestActivated
					&& !isParallel
					&& (!context.isSimulation() || !context.getActivationContext()
							.isActivateRequests()) ) {
				break;
			}
			if ( request.getParentActionRequest() != null || request.getNodeInstance() == null ) {
				// 1. disregard request if it's not a top-level request
				// 2. disregard request if it's a "future" request and hasn't
				// been attached to a node instance yet
				continue;
			}
			if ( request.isApproveOrCompleteRequest() ) {
				if ( groupToActivate == null ) {
					groupToActivate = request.getResponsibilityDesc();
				}
				if ( StringUtils.equals( groupToActivate, request.getResponsibilityDesc() ) ) {
					if ( request.isActive() ) {
						requestActivated = requestActivated || request.isApproveOrCompleteRequest();
						continue;
					}
					logProcessingMessage( request );
					if ( LOG.isDebugEnabled() ) {
						LOG.debug( "Activating request: " + request );
					}
					requestActivated = activateRequest( context, request, nodeInstance,
							generatedActionItems )
							|| requestActivated;
				}
			} else {
				logProcessingMessage( request );
				if ( LOG.isDebugEnabled() ) {
					LOG.debug( "Activating request: " + request );
				}
				requestActivated = activateRequest( context, request, nodeInstance,
						generatedActionItems )
						|| requestActivated;
			}
		}
		return requestActivated;
	}
}
