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
package org.kuali.rice.edl.impl;

import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.util.GlobalVariables;


/**
 * A collection of handy workflow queries to be used from style sheets.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class WorkflowFunctions {


    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowFunctions.class);

    public static boolean isUserInitiator(String id) throws WorkflowException {
    	boolean initiator = false;
    	UserSession userSession = GlobalVariables.getUserSession();
    	if (userSession != null) {
    		try {
    			long documentId = Long.parseLong(id.trim());
    			WorkflowInfo workflowInfo = new WorkflowInfo();
    			if (userSession.getPrincipalId().equals(workflowInfo.getRouteHeader(documentId).getInitiatorPrincipalId())) {
    				initiator = true;
    			}
    		} catch (Exception e) {
    			LOG.debug("Exception encountered trying to determine if user is the document initiator:" + e );
    		}
    	}
    	return initiator;
    }

	public static boolean isUserRouteLogAuthenticated(String id) {
		boolean authenticated=false;
		WorkflowInfo workflowInfo = new WorkflowInfo();
		UserSession userSession=GlobalVariables.getUserSession();
		if(userSession!=null){
			String principalId = userSession.getPrincipalId();
			try {
				Long routeHeaderId = new Long(id);
				authenticated = workflowInfo.isUserAuthenticatedByRouteLog(routeHeaderId, principalId, true);
			} catch (NumberFormatException e) {
				LOG.debug("Invalid format routeHeaderId (should be LONG): " + id);
			} catch (WorkflowException e) {
				LOG.debug("Error checking if user is route log authenticated: userId: " + principalId + ";routeHeaderId: " + id);

		    } catch (RiceRuntimeException e) {
		    	LOG.error("Runtime Exception checking if user is route log authenticated: userId: " + principalId + ";routeHeaderId: " + id);

		    }
		}

	    return authenticated;
	}

	public static boolean isPrincipalIdAuthenticated(String principalId) {
		return GlobalVariables.getUserSession().getPrincipalId().equals(principalId);
	}
	
	public static boolean isPrincipalNameAuthenticated(String principalName) {
		return GlobalVariables.getUserSession().getPrincipalName().equals(principalName);
	}
	
	public static boolean isEmployeeIdAuthenticated(String employeeId) {
		return GlobalVariables.getUserSession().getPerson().getEmployeeId().equals(employeeId);
	}

	public static Person getAuthenticatedPerson(){
		UserSession userSession=GlobalVariables.getUserSession();
		Person user = userSession.getPerson();
		return user;
	}

	public static String getUserId() {
	        return getAuthenticatedPerson().getPrincipalId();
	}

	public static String getLastName() {
	        return getAuthenticatedPerson().getLastName();
	}

	public static String getGivenName() {
	    return getAuthenticatedPerson().getFirstName();
	}

	public static String getEmailAddress() {
	    return getAuthenticatedPerson().getEmailAddress();
	}

	public static boolean isNodeInPreviousNodeList(String nodeName, String id) {
		LOG.debug("nodeName came in as: " + nodeName);
		LOG.debug("id came in as: " + id);
		//get list of previous node names
		String[] previousNodeNames;
		WorkflowInfo workflowInfo = new WorkflowInfo();
		try {
			previousNodeNames = workflowInfo.getPreviousRouteNodeNames(new Long(id));
		} catch (Exception e) {
			throw new WorkflowRuntimeException("Problem generating list of previous node names for documentID = " + id, e);
		}
		//see if node name is in the list of previous node names
		for (String previousNodeName : previousNodeNames) {
			if (previousNodeName.equals(nodeName)) {
				return true;
			}
		}
		return false;
	}

	public static String escapeJavascript(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	public static boolean isNodeBetween(String firstNodeName, String lastNodeName, String id) {
		if (isNodeInPreviousNodeList(firstNodeName, id)) {
			if (isNodeInPreviousNodeList(lastNodeName, id)) {
				return false;
			}else {
				return true;
			}
		} else {
			return false;
		}
	}

	public static boolean isAtNode(String documentId, String nodeName) throws Exception {
	    WorkflowInfo workflowInfo = new WorkflowInfo();
	    RouteNodeInstanceDTO[] activeNodeInstances = workflowInfo.getActiveNodeInstances(new Long(documentId));
	    for (RouteNodeInstanceDTO nodeInstance : activeNodeInstances) {
	        if (nodeInstance.getName().equals(nodeName)) {
	            return true;
	        }
	    }
	    return false;
	}

	public static boolean hasActiveNode(String documentId) throws Exception {
	    WorkflowInfo workflowInfo = new WorkflowInfo();
	    RouteNodeInstanceDTO[] activeNodeInstances = workflowInfo.getActiveNodeInstances(new Long(documentId));
	    if (activeNodeInstances.length > 0) {
	            return true;
	    }
	    return false;
	}

	public static String getAuthenticationId() {
	    UserSession userSession=GlobalVariables.getUserSession();
	    return userSession.getPrincipalName();
	
	}
	
}
