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
package edu.iu.uis.eden.edl;

import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * A collection of handy workflow queries to be used from style sheets.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WorkflowFunctions {
	
	
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowFunctions.class);
	
	public static boolean isUserRouteLogAuthenticated(String id) {
		boolean authenticated=false;
		WorkflowInfo workflowInfo = new WorkflowInfo();

		UserSession userSession=UserSession.getAuthenticatedUser();
		if(userSession!=null){
			UserIdVO userId = new WorkflowIdVO(userSession.getWorkflowUser().getWorkflowId());
			try {
				Long routeHeaderId = new Long(id);
				authenticated = workflowInfo.isUserAuthenticatedByRouteLog(routeHeaderId, userId, true);
			} catch (NumberFormatException e) {
				LOG.error("Invalid format routeHeaderId (should be LONG): " + id);
			} catch (WorkflowException e) {
				LOG.error("Error checking if user is route log authenticated: userId: "+userId + ";routeHeaderId: " + id);
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

	public static boolean isNodeInPreviousNodeList(String nodeName, String id) {
		LOG.error("nodeName came in as: " + nodeName);
		LOG.error("id came in as: " + id);
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
}
