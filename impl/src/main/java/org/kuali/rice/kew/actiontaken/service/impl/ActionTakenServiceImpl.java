/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.actiontaken.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.actiontaken.dao.ActionTakenDAO;
import org.kuali.rice.kew.actiontaken.service.ActionTakenService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.entity.KimPrincipal;


/**
 * Default implementation of the {@link ActionTakenService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionTakenServiceImpl implements ActionTakenService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionTakenServiceImpl.class);
    private ActionTakenDAO actionTakenDAO;

    public ActionTakenValue load(Long id) {
        return getActionTakenDAO().load(id);
    }

    public ActionTakenValue findByActionTakenId(Long actionTakenId) {
        return getActionTakenDAO().findByActionTakenId(actionTakenId);
    }

    public ActionTakenValue getPreviousAction(ActionRequestValue actionRequest) {
    	return getPreviousAction(actionRequest, null);
    }

    public ActionTakenValue getPreviousAction(ActionRequestValue actionRequest, List<ActionTakenValue> simulatedActionsTaken)
    {
        IdentityManagementService ims = KIMServiceLocator.getIdentityManagementService();
        ActionTakenValue foundActionTaken = null;
        List<String> principalIds = new ArrayList<String>();
        if (actionRequest.isGroupRequest()) {
            principalIds.addAll( ims.getGroupMemberPrincipalIds(actionRequest.getGroup().getId()));
        } else if (actionRequest.isUserRequest()) {
            principalIds.add(actionRequest.getPrincipalId());
        }

        for (String id : principalIds)
        {
            List<ActionTakenValue> actionsTakenByUser =
                getActionTakenDAO().findByRouteHeaderIdWorkflowId(actionRequest.getRouteHeaderId(), id );
            if (simulatedActionsTaken != null) {
                for (ActionTakenValue simulatedAction : simulatedActionsTaken)
                {
                    if (id.equals(simulatedAction.getPrincipalId()))
                    {
                        actionsTakenByUser.add(simulatedAction);
                    }
                }
            }

            for (ActionTakenValue actionTaken : actionsTakenByUser)
            {
                if (ActionRequestValue.compareActionCode(actionTaken.getActionTaken(),
                        actionRequest.getActionRequested(), true) >= 0)
                {
                  foundActionTaken = actionTaken;
                }
            }
        }

        return foundActionTaken;
    }

    public Collection findByDocIdAndAction(Long docId, String action) {
        return getActionTakenDAO().findByDocIdAndAction(docId, action);
    }

    public Collection<ActionTakenValue> findByRouteHeaderId(Long routeHeaderId) {
        return getActionTakenDAO().findByRouteHeaderId(routeHeaderId);
    }

    public List findByRouteHeaderIdWorkflowId(Long routeHeaderId, String workflowId) {
        return getActionTakenDAO().findByRouteHeaderIdWorkflowId(routeHeaderId, workflowId);
    }

    public Collection getActionsTaken(Long routeHeaderId) {
        return getActionTakenDAO().findByRouteHeaderId(routeHeaderId);
    }

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId) {
        return getActionTakenDAO().findByRouteHeaderIdIgnoreCurrentInd(routeHeaderId);
    }

    public void saveActionTaken(ActionTakenValue actionTaken) {
        this.getActionTakenDAO().saveActionTaken(actionTaken);
    }

    public void delete(ActionTakenValue actionTaken) {
        getActionTakenDAO().deleteActionTaken(actionTaken);
    }

    public ActionTakenDAO getActionTakenDAO() {
        return actionTakenDAO;
    }

    public void setActionTakenDAO(ActionTakenDAO actionTakenDAO) {
        this.actionTakenDAO = actionTakenDAO;
    }

    public void deleteByRouteHeaderId(Long routeHeaderId){
        actionTakenDAO.deleteByRouteHeaderId(routeHeaderId);
    }

    public void validateActionTaken(ActionTakenValue actionTaken){
        LOG.debug("Enter validateActionTaken(..)");
        List<WorkflowServiceErrorImpl> errors = new ArrayList<WorkflowServiceErrorImpl>();

        Long routeHeaderId = actionTaken.getRouteHeaderId();
        if(routeHeaderId == null){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken routeheaderid null.", "actiontaken.routeheaderid.empty", actionTaken.getActionTakenId().toString()));
        } else if(getRouteHeaderService().getRouteHeader(routeHeaderId) == null){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken routeheaderid invalid.", "actiontaken.routeheaderid.invalid", actionTaken.getActionTakenId().toString()));
        }

        String principalId = actionTaken.getPrincipalId();
        if(StringUtils.isBlank(principalId)){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken personid null.", "actiontaken.personid.empty", actionTaken.getActionTakenId().toString()));
        } else {
        	KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipal(principalId);
        	if (principal == null) {
                errors.add(new WorkflowServiceErrorImpl("ActionTaken personid invalid.", "actiontaken.personid.invalid", actionTaken.getActionTakenId().toString()));
            }
        }
        String actionTakenCd = actionTaken.getActionTaken();
        if(actionTakenCd == null || actionTakenCd.trim().equals("")){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken cd null.", "actiontaken.actiontaken.empty", actionTaken.getActionTakenId().toString()));
        } else if(!KEWConstants.ACTION_TAKEN_CD.containsKey(actionTakenCd)){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken invalid.", "actiontaken.actiontaken.invalid", actionTaken.getActionTakenId().toString()));
        }
        if(actionTaken.getActionDate() == null){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken actiondate null.", "actiontaken.actiondate.empty", actionTaken.getActionTakenId().toString()));
        }

        if(actionTaken.getDocVersion() == null){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken docversion null.", "actiontaken.docverion.empty", actionTaken.getActionTakenId().toString()));
        }
        LOG.debug("Exit validateActionRequest(..) ");
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("ActionRequest Validation Error", errors);
        }
    }

    public boolean hasUserTakenAction(String principalId, Long documentId) {
    	return getActionTakenDAO().hasUserTakenAction(principalId, documentId);
    }

    private RouteHeaderService getRouteHeaderService() {
        return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }

    public Timestamp getLastApprovedDate(Long routeHeaderId)
    {
    	Timestamp dateLastApproved = null;
    	Collection<ActionTakenValue> actionsTaken= getActionTakenDAO().findByDocIdAndAction(routeHeaderId, KEWConstants.ACTION_TAKEN_APPROVED_CD);
        for (ActionTakenValue actionTaken : actionsTaken)
        {
            // search for the most recent approval action
            if (dateLastApproved == null || dateLastApproved.compareTo(actionTaken.getActionDate()) <= -1)
            {
                dateLastApproved = actionTaken.getActionDate();
            }
        }
    	LOG.info("Exit getLastApprovedDate("+routeHeaderId+") "+dateLastApproved);
    	return dateLastApproved;
    }

}
