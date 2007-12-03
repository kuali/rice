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
package edu.iu.uis.eden.routelog.web;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.doctype.DocumentSecurityService;
import edu.iu.uis.eden.doctype.SecuritySession;
import edu.iu.uis.eden.engine.simulation.SimulationCriteria;
import edu.iu.uis.eden.engine.simulation.SimulationEngine;
import edu.iu.uis.eden.engine.simulation.SimulationResults;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * A Struts Action used to display the routelog.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteLogAction extends WorkflowAction {

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
            populateRouteLogFutureRequests(rlForm, routeHeader);
        }

        if (routeHeader.isLocked()) {
            return mapping.findForward("documentLocked");
        }

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

                if (EdenConstants.ACTION_REQUEST_INITIALIZED.equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("PENDING");
                } else if (EdenConstants.ACTION_REQUEST_ACTIVATED.equals(actionRequest.getStatus())) {
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

                if (EdenConstants.ACTION_REQUEST_INITIALIZED.equals(actionRequest.getStatus())) {
                    actionRequest.setDisplayStatus("PENDING");
                } else if (EdenConstants.ACTION_REQUEST_ACTIVATED.equals(actionRequest.getStatus())) {
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