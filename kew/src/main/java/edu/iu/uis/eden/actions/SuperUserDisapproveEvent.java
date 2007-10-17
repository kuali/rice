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
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Performs a disapprove as a super user
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SuperUserDisapproveEvent extends SuperUserActionTakenEvent {

    public SuperUserDisapproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user) {
        super(routeHeader, user);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_DISAPPROVED_CD);
        this.superUserAction = EdenConstants.SUPER_USER_DISAPPROVE;
    }

    public SuperUserDisapproveEvent(DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation) {
        super(routeHeader, user, annotation);
        setActionTakenCode(EdenConstants.ACTION_TAKEN_SU_DISAPPROVED_CD);
        this.superUserAction = EdenConstants.SUPER_USER_DISAPPROVE;
    }

    protected void markDocument() throws WorkflowException {
        //this.event = new DocumentRouteStatusChange(this.routeHeaderId, this.getRouteHeader().getAppDocId(), this.getRouteHeader().getDocRouteStatus(), EdenConstants.ROUTE_HEADER_DISAPPROVED_CD);
        getRouteHeader().markDocumentDisapproved();
        getRouteHeaderService().saveRouteHeader(getRouteHeader());
    }
}