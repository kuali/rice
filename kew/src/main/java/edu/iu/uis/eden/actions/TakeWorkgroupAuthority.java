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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Removes all workgroup action items for a document from everyone's action list except the person 
 * who took the workgroup authority
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class TakeWorkgroupAuthority extends ActionTakenEvent {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TakeWorkgroupAuthority.class);
    
    private Workgroup workgroup;
    
    /**
     * @param routeHeader
     * @param user
     */
    public TakeWorkgroupAuthority(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
        super.setActionTakenCode(EdenConstants.ACTION_TAKEN_TAKE_WORKGROUP_AUTHORITY_CD);
    }

    /**
     * @param routeHeader
     * @param user
     * @param annotation
     * @param workgroup
     */
    public TakeWorkgroupAuthority(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, Workgroup workgroup) {
        super(routeHeader, user, annotation);
        this.workgroup = workgroup;
        super.setActionTakenCode(EdenConstants.ACTION_TAKEN_TAKE_WORKGROUP_AUTHORITY_CD);
    }
    
    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#requireInitiatorCheck()
     */
    @Override
    protected boolean requireInitiatorCheck() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.actions.ActionTakenEvent#validateActionRules()
     */
    @Override
    public String validateActionRules() throws EdenUserNotFoundException {
        String superError = super.validateActionTakenRules();
        if (!Utilities.isEmpty(superError)) {
            return superError;
        }
        if  ( (workgroup != null) && (!workgroup.hasMember(getUser())) ) {
            return (getUser().getAuthenticationUserId() + " not a member of workgroup " + workgroup.getDisplayName());
        }
        return "";
    }

    public void recordAction() throws InvalidActionTakenException, EdenUserNotFoundException {
        
        String errorMessage = validateActionRules();
        if (!Utilities.isEmpty(errorMessage)) {
            throw new InvalidActionTakenException(errorMessage);
        }
//        if (! workgroup.hasMember(getUser())) {
//            throw new InvalidActionTakenException(getUser().getAuthenticationUserId() + " not a member of workgroup " + workgroup.getDisplayName());
//        }
        
        List documentRequests = getActionRequestService().findPendingByDoc(getRouteHeaderId());
        List workgroupRequests = new ArrayList();
        for (Iterator iter = documentRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (actionRequest.isWorkgroupRequest() && actionRequest.getWorkgroup().getWorkflowGroupId().getGroupId().equals(workgroup.getWorkflowGroupId().getGroupId())) {
                workgroupRequests.add(actionRequest);
            }
        }
        
        saveActionTaken(findDelegatorForActionRequests(workgroupRequests));
        notifyActionTaken(this.actionTaken);
        
        ActionListService actionListService = KEWServiceLocator.getActionListService();
        Collection actionItems = actionListService.findByRouteHeaderId(getRouteHeaderId());
        for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
            ActionItem actionItem = (ActionItem) iter.next();
            //delete all requests for this workgroup on this document not to this user
            if (actionItem.isWorkgroupItem() && actionItem.getWorkgroupId().equals(workgroup.getWorkflowGroupId().getGroupId()) &&
                    ! actionItem.getWorkflowId().equals(getUser().getWorkflowId())) {
                actionListService.deleteActionItem(actionItem);
            }
        }
    }
}