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
package edu.iu.uis.eden.actions;

import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * This is the inverse of the {@link TakeWorkgroupAuthority} action.  This puts the document back 
 * in all the peoples action lists that have the document routed to them.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ReleaseWorkgroupAuthority extends ActionTakenEvent {

    private Workgroup workgroup;
    
    /**
     * @param routeHeader
     * @param user
     */
    public ReleaseWorkgroupAuthority(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
        super.setActionTakenCode(EdenConstants.ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY_CD);
    }
    
    /**
     * @param routeHeader
     * @param user
     * @param annotation
     * @param workgroup
     */
    public ReleaseWorkgroupAuthority(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, Workgroup workgroup) {
        super(routeHeader, user, annotation);
        this.workgroup = workgroup;
        super.setActionTakenCode(EdenConstants.ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY_CD);
    }
    
    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#validateActionRules()
     */
    @Override
    public String validateActionRules() throws EdenUserNotFoundException {
        if (workgroup == null) {
            return "User cannot Release Workgroup Authority without a given workgroup";
        } else {
            return performReleaseWorkgroupAuthority(true);
        }
    }

    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
        String error = performReleaseWorkgroupAuthority(false);
        if (!Utilities.isEmpty(error)) {
            throw new InvalidActionTakenException(error);
        }
    }
    
    private String performReleaseWorkgroupAuthority(boolean forValidationOnly) throws EdenUserNotFoundException {
        if (! workgroup.hasMember(getUser())) {
            return (getUser().getAuthenticationUserId() + " not a member of workgroup " + workgroup.getDisplayName());
        }
        
        List actionRequests = getActionRequestService().findPendingByDoc(getRouteHeaderId());
        //List groupRequestsToActivate = new ArrayList();//requests for this group that need action items
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            //we left the group active from take authority action.  pending havent been normally activated yet
            if (actionRequest.isWorkgroupRequest() && actionRequest.isActive() && actionRequest.getWorkgroupId().equals(workgroup.getWorkflowGroupId().getGroupId())) {
                if (actionRequest.getActionItems().size() == 1) {
                    ActionItem actionItem = (ActionItem) actionRequest.getActionItems().get(0);
                    if (! actionItem.getWorkflowId().equals(getUser().getWorkflowId())) {
                        return "User attempting to release workgroup authority did not take it.";
                    } else if (!forValidationOnly) {
                        actionRequest.setStatus(EdenConstants.ACTION_REQUEST_INITIALIZED);//to circumvent check in service during activation
                        getActionRequestService().activateRequest(actionRequest);
                    }
                }
            }
        }
        return "";
    }
}