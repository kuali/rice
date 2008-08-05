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
package org.kuali.rice.kew.routelog.web;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.actionrequests.ActionRequestService;
import org.kuali.rice.kew.actionrequests.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.doctype.DocumentSecurityService;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.engine.simulation.SimulationCriteria;
import org.kuali.rice.kew.engine.simulation.SimulationEngine;
import org.kuali.rice.kew.engine.simulation.SimulationResults;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.WorkflowAction;
import org.kuali.rice.kew.web.session.UserSession;


/**
 * A Struts Action used to display the routelog.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteLogAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RouteLogAction.class);
    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RouteLogForm rlForm = (RouteLogForm) form;
        Long routeHeaderId = null;
        if (! Utilities.isEmpty(rlForm.getRouteHeaderId())) {
            routeHeaderId = new Long(rlForm.getRouteHeaderId());
        } else if (! Utilities.isEmpty(rlForm.getDocId())) {
            routeHeaderId = new Long(rlForm.getDocId());
        } else {
        	throw new WorkflowRuntimeException("No paramater provided to fetch document");
        }

        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);

        DocumentSecurityService security = KEWServiceLocator.getDocumentSecurityService();
        if (!security.routeLogAuthorized(getUserSession(request), routeHeader, new SecuritySession(UserSession.getAuthenticatedUser()))) {
          return mapping.findForward("NotAuthorized");
        }

        for (Iterator iter = routeHeader.getActionsTaken().iterator(); iter.hasNext();) {
            ActionTakenValue actionTaken = (ActionTakenValue) iter.next();
            Collections.sort((List) actionTaken.getActionRequests(), new Utilities().new RouteLogActionRequestSorter());
        }

        populateRouteLogFormActionRequests(rlForm, routeHeader);

        rlForm.setLookFuture(routeHeader.getDocumentType().getLookIntoFuturePolicy().getPolicyValue().booleanValue());

        if (rlForm.isShowFuture()) {
            try {
                populateRouteLogFutureRequests(rlForm, routeHeader);
            } catch (Exception e) {
                String errorMsg = "Found Error while getting Future Action Requests:  " + e.getMessage();
                LOG.info(errorMsg,e);
                rlForm.setShowFutureError(errorMsg);
            }
        }
/*
        if (routeHeader.isLocked()) {
            return mapping.findForward("documentLocked");
        }
*/
        request.setAttribute("routeHeader", routeHeader);
        return mapping.findForward("viewRouteLog");
    }

    public void populateRouteLogFormActionRequests(RouteLogForm rlForm, DocumentRouteHeaderValue routeHeader) {
        List rootRequests = getActionRequestService().getRootRequests(routeHeader.getActionRequests());
        Collections.sort(rootRequests, new Utilities().new RouteLogActionRequestSorter());
        int arCount = 0;
        for (Iterator iterator = rootRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            if (actionRequest.isPending()) {
                arCount++;

                if (KEWConstants.ACTION_REQUEST_INITIALIZED.equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("PENDING");
                } else if (KEWConstants.ACTION_REQUEST_ACTIVATED.equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("IN ACTION LIST");
                }
            }
        }
        rlForm.setRootRequests(rootRequests);
        rlForm.setPendingActionRequestCount(arCount);
    }

    public void populateRouteLogFutureRequests(RouteLogForm rlForm, DocumentRouteHeaderValue document) throws Exception {
        SimulationEngine simEngine = new SimulationEngine();
        SimulationResults simResults = simEngine.runSimulation(new SimulationCriteria(document.getRouteHeaderId()));
        // should now be sorted list
        List rootRequests = getActionRequestService().getRootRequests(simResults.getSimulatedActionRequests());
        Collections.sort(rootRequests, new Utilities().new RouteLogActionRequestSorter());
        int arCount = 0;
        for (Iterator iterator = rootRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            if (actionRequest.isPending()) {
                arCount++;

                if (KEWConstants.ACTION_REQUEST_INITIALIZED.equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("PENDING");
                } else if (KEWConstants.ACTION_REQUEST_ACTIVATED.equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("IN ACTION LIST");
                }
            }
        }
        rlForm.setFutureRootRequests(rootRequests);
        rlForm.setFutureActionRequestCount(arCount);
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        return null;
    }

    private ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
    }
}