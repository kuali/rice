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

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Does a return to previous as a superuser
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SuperUserReturnToPreviousNodeAction extends SuperUserActionTakenEvent {
    
    private String nodeName;
    
    public SuperUserReturnToPreviousNodeAction(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS_CD);
        this.superUserAction = EdenConstants.SUPER_USER_RETURN_TO_PREVIOUS_ROUTE_LEVEL;
    }
    
    public SuperUserReturnToPreviousNodeAction(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, String nodeName) {
        super(routeHeader, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS_CD);
        this.superUserAction = EdenConstants.SUPER_USER_RETURN_TO_PREVIOUS_ROUTE_LEVEL;
        this.nodeName = nodeName;
    }
    
    protected void markDocument() throws WorkflowException {
        if (getRouteHeader().isInException()) {
            //this.event = new DocumentRouteStatusChange(this.routeHeaderId, this.getRouteHeader().getAppDocId(), this.getRouteHeader().getDocRouteStatus(), EdenConstants.ROUTE_HEADER_ENROUTE_CD);
            getRouteHeader().markDocumentEnroute();
        }
        ReturnToPreviousNodeAction returnAction = new ReturnToPreviousNodeAction(getRouteHeader(), getUser(), annotation, nodeName, true);
        returnAction.setActionTakenCode(this.getActionTakenCode());
        returnAction.setSuperUserUsage(true);
        returnAction.recordAction();
    }
    
    protected void processActionRequests() throws InvalidActionTakenException, EdenUserNotFoundException {
        //do nothing
    }

}
