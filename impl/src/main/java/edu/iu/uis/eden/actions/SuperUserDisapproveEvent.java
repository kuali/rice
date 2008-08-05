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

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Performs a disapprove as a super user
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SuperUserDisapproveEvent extends SuperUserActionTakenEvent {

    public SuperUserDisapproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(KEWConstants.ACTION_TAKEN_SU_DISAPPROVED_CD, routeHeader, user);
        this.superUserAction = KEWConstants.SUPER_USER_DISAPPROVE;
    }

    public SuperUserDisapproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, boolean runPostProcessor) {
        super(KEWConstants.ACTION_TAKEN_SU_DISAPPROVED_CD, routeHeader, user, annotation, runPostProcessor);
        this.superUserAction = KEWConstants.SUPER_USER_DISAPPROVE;
    }

    protected void markDocument() throws WorkflowException {
        //this.event = new DocumentRouteStatusChange(this.routeHeaderId, this.getRouteHeader().getAppDocId(), this.getRouteHeader().getDocRouteStatus(), KEWConstants.ROUTE_HEADER_DISAPPROVED_CD);
        getRouteHeader().markDocumentDisapproved();
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(getRouteHeader());
    }
}