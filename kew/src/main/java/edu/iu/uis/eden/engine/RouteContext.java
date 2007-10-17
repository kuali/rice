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
package edu.iu.uis.eden.engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.StandardDocumentContent;

/**
 * Represents the current context of a Document being processed by the engine.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteContext implements Serializable {

	private static final long serialVersionUID = -7125137491367944594L;

	private DocumentRouteHeaderValue routeHeader;

	private DocumentContent documentContent;

	private RouteNodeInstance nodeInstance;

	private EngineState engineState;

	private ActionRequestValue actionRequest;

	private ActivationContext activationContext = new ActivationContext(!ActivationContext.CONTEXT_IS_SIMULATION);

	private boolean doNotSendApproveNotificationEmails = false;

	private Map parameters = new HashMap();

	public RouteContext() {
	}

	private static ThreadLocal<List<RouteContext>> ROUTE_CONTEXT_STACK = new ThreadLocal<List<RouteContext>>() {
		protected List<RouteContext> initialValue() {
			List<RouteContext> contextStack = new LinkedList<RouteContext>();
			contextStack.add(0, new RouteContext());
			return contextStack;
		}
	};

	public static RouteContext getCurrentRouteContext() {
		return ROUTE_CONTEXT_STACK.get().get(0);
	}

	public static void clearCurrentRouteContext() {
		ROUTE_CONTEXT_STACK.get().remove(0);
		ROUTE_CONTEXT_STACK.get().add(0, new RouteContext());
	}

	public static RouteContext createNewRouteContext() {
		ROUTE_CONTEXT_STACK.get().add(0, new RouteContext());
		return getCurrentRouteContext();
	}

	public static RouteContext releaseCurrentRouteContext() {
		return ROUTE_CONTEXT_STACK.get().remove(0);
	}

	/**
	 * @deprecated use getDocument() instead
	 */
	public DocumentRouteHeaderValue getRouteHeader() {
		return routeHeader;
	}

	/**
	 * @deprecated user setDocument() instead
	 */
	public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
		this.routeHeader = routeHeader;
	}

	public DocumentRouteHeaderValue getDocument() {
		return routeHeader;
	}

	public void setDocument(DocumentRouteHeaderValue routeHeader) {
		this.routeHeader = routeHeader;
		try {
			setDocumentContent(new StandardDocumentContent(routeHeader.getDocContent(), this));
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	public DocumentContent getDocumentContent() {
		return documentContent;
	}

	public void setDocumentContent(DocumentContent documentContent) {
		this.documentContent = documentContent;
	}

	public RouteNodeInstance getNodeInstance() {
		return nodeInstance;
	}

	public void setNodeInstance(RouteNodeInstance nodeInstance) {
		this.nodeInstance = nodeInstance;
	}

	public EngineState getEngineState() {
		return engineState;
	}

	public void setEngineState(EngineState engineState) {
		this.engineState = engineState;
	}

	public ActionRequestValue getActionRequest() {
		return actionRequest;
	}

	public void setActionRequest(ActionRequestValue actionRequest) {
		this.actionRequest = actionRequest;
	}

	public boolean isSimulation() {
		if (activationContext == null) {
			return false;
		}
		return activationContext.isSimulation();
	}

	public ActivationContext getActivationContext() {
		return activationContext;
	}

	public void setActivationContext(ActivationContext activationContext) {
		this.activationContext = activationContext;
	}

	public boolean isDoNotSendApproveNotificationEmails() {
		return doNotSendApproveNotificationEmails;
	}

	public void setDoNotSendApproveNotificationEmails(boolean sendNotificationEmails) {
		this.doNotSendApproveNotificationEmails = sendNotificationEmails;
	}

	public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}
}