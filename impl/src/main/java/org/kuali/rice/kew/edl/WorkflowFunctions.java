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
package org.kuali.rice.kew.edl;

import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.WorkflowIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.session.UserSession;


/**
 * A collection of handy workflow queries to be used from style sheets.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WorkflowFunctions {
	
	
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowFunctions.class);
        
        public static boolean isUserInitiator(String id) throws WorkflowException {
            boolean initiator = false;
            UserSession userSession = UserSession.getAuthenticatedUser();
            if (userSession != null) {            
        	try {
        	    long documentId = Long.parseLong(id.trim());
        	    WorkflowInfo workflowInfo = new WorkflowInfo();
        	    if (userSession.getNetworkId().equals(workflowInfo.getRouteHeader(documentId).getInitiator().getNetworkId())) {
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
		UserSession userSession=UserSession.getAuthenticatedUser();
		if(userSession!=null){
			UserIdDTO userId = new WorkflowIdDTO(userSession.getWorkflowUser().getWorkflowId());
			try {
				Long routeHeaderId = new Long(id);
				authenticated = workflowInfo.isUserAuthenticatedByRouteLog(routeHeaderId, userId, true);
			} catch (NumberFormatException e) {
				LOG.debug("Invalid format routeHeaderId (should be LONG): " + id);
			} catch (WorkflowException e) {
				LOG.debug("Error checking if user is route log authenticated: userId: "+userId + ";routeHeaderId: " + id);
		    }
		}
		
	    return authenticated;
	}
	
	public static boolean isUidAuthentictedUserUid(String uuid) {
		UserSession userSession = UserSession.getAuthenticatedUser();
		if (uuid.equals(userSession.getWorkflowUser().getEmplId().getEmplId())) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isUserInGroup(String groupName){
		boolean isUserInGroup=false;
		UserSession userSession=UserSession.getAuthenticatedUser();
		if(userSession!=null){
			if(!Utilities.isEmpty(groupName)){
				isUserInGroup = userSession.isMemberOfGroup(groupName);
			}
		}
		return isUserInGroup;
	}

	public static WorkflowUser getWorkflowUser() {
		UserSession userSession=UserSession.getAuthenticatedUser();
		//testing
		LOG.debug("Given name safe: " + userSession.getWorkflowUser().getGivenNameSafe());
		LOG.debug("Display name safe: " + userSession.getWorkflowUser().getDisplayNameSafe());
		LOG.debug("Last name safe: " + userSession.getWorkflowUser().getLastNameSafe());
		LOG.debug("Auth User ID: " + userSession.getWorkflowUser().getAuthenticationUserId().getId());
		LOG.debug("EPLID: " + userSession.getWorkflowUser().getEmplId().getEmplId());
		return userSession.getWorkflowUser();
	}

	public static String getUserId() {
	        return getWorkflowUser().getEmplId().getId();
	}

	public static String getLastName() {
	        return getWorkflowUser().getLastName();
	}

	public static String getGivenName() {
	    return getWorkflowUser().getGivenName();
	}

	public static String getEmailAddress() {
	    UserSession userSession=UserSession.getAuthenticatedUser();
	    return userSession.getEmailAddress();
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
		for (int i = 0; i < previousNodeNames.length; i++) {
			if (previousNodeNames[i].equals(nodeName)) {
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

}
