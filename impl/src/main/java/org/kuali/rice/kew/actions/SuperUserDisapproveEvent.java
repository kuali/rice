/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.actions;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;


/**
 * Performs a disapprove as a super user
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SuperUserDisapproveEvent extends SuperUserActionTakenEvent {

    public SuperUserDisapproveEvent(DocumentRouteHeaderValue routeHeader, KimPrincipal principal) {
        super(KEWConstants.ACTION_TAKEN_SU_DISAPPROVED_CD, routeHeader, principal);
        this.superUserAction = KEWConstants.SUPER_USER_DISAPPROVE;
    }

    public SuperUserDisapproveEvent(DocumentRouteHeaderValue routeHeader, KimPrincipal principal, String annotation, boolean runPostProcessor) {
        super(KEWConstants.ACTION_TAKEN_SU_DISAPPROVED_CD, routeHeader, principal, annotation, runPostProcessor);
        this.superUserAction = KEWConstants.SUPER_USER_DISAPPROVE;
    }

    protected void markDocument() throws WorkflowException {
        //this.event = new DocumentRouteStatusChange(this.documentId, this.getRouteHeader().getAppDocId(), this.getRouteHeader().getDocRouteStatus(), KEWConstants.ROUTE_HEADER_DISAPPROVED_CD);
        getRouteHeader().markDocumentDisapproved();
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(getRouteHeader());
    }
}
