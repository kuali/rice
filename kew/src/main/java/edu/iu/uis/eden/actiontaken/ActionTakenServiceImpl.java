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
package edu.iu.uis.eden.actiontaken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actiontaken.dao.ActionTakenDAO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Default implementation of the {@link ActionTakenService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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

    public ActionTakenValue getPreviousAction(ActionRequestValue actionRequest) throws EdenUserNotFoundException {
    	return getPreviousAction(actionRequest, null);
    }

    public ActionTakenValue getPreviousAction(ActionRequestValue actionRequest, List simulatedActionsTaken) throws EdenUserNotFoundException {

        ActionTakenValue foundActionTaken = null;
        List users = new ArrayList();
        if (actionRequest.isWorkgroupRequest()) {
            users.addAll(actionRequest.getWorkgroup().getUsers());
        } else if (actionRequest.isUserRequest()){
            users.add(actionRequest.getWorkflowUser());
        }

        for (Iterator iter = users.iterator(); iter.hasNext();) {
            WorkflowUser user = (WorkflowUser) iter.next();
            List actionsTakenByUser = getActionTakenDAO().findByRouteHeaderIdWorkflowId(actionRequest.getRouteHeaderId(), user.getWorkflowUserId().getWorkflowId());
            if (simulatedActionsTaken != null) {
            	actionsTakenByUser.addAll(simulatedActionsTaken);
            }

            for (Iterator iterator = actionsTakenByUser.iterator(); iterator.hasNext();) {
                ActionTakenValue actionTaken = (ActionTakenValue) iterator.next();
                if (ActionRequestValue.compareActionCode(actionTaken.getActionTaken(), actionRequest.getActionRequested()) >= 0) {
                  foundActionTaken = actionTaken;
                }
            }
        }

        return foundActionTaken;
    }

    public Collection findByDocIdAndAction(Long docId, String action) {
        return getActionTakenDAO().findByDocIdAndAction(docId, action);
    }

    public Collection findByRouteHeaderId(Long routeHeaderId) {
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

    public WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }

    public void deleteByRouteHeaderId(Long routeHeaderId){
        actionTakenDAO.deleteByRouteHeaderId(routeHeaderId);
    }

    public void validateActionTaken(ActionTakenValue actionTaken){
        LOG.debug("Enter validateActionTaken(..)");
        List errors = new ArrayList();

        Long routeHeaderId = actionTaken.getRouteHeaderId();
        if(routeHeaderId == null){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken routeheaderid null.", "actiontaken.routeheaderid.empty", actionTaken.getActionTakenId().toString()));
        } else if(getRouteHeaderService().getRouteHeader(routeHeaderId) == null){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken routeheaderid invalid.", "actiontaken.routeheaderid.invalid", actionTaken.getActionTakenId().toString()));
        }

        String userId = actionTaken.getWorkflowId();
        if(userId == null || userId.trim().equals("")){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken personid null.", "actiontaken.personid.empty", actionTaken.getActionTakenId().toString()));
        } else {
            try{
                getUserService().getWorkflowUser(new WorkflowUserId(userId));
            } catch (EdenUserNotFoundException e){
                errors.add(new WorkflowServiceErrorImpl("ActionTaken personid invalid.", "actiontaken.personid.invalid", actionTaken.getActionTakenId().toString()));
            }
        }
        String actionTakenCd = actionTaken.getActionTaken();
        if(actionTakenCd == null || actionTakenCd.trim().equals("")){
            errors.add(new WorkflowServiceErrorImpl("ActionTaken cd null.", "actiontaken.actiontaken.empty", actionTaken.getActionTakenId().toString()));
        } else if(!EdenConstants.ACTION_TAKEN_CD.containsKey(actionTakenCd)){
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

    public boolean hasUserTakenAction(WorkflowUser user, Long documentId) {
    	return getActionTakenDAO().hasUserTakenAction(user.getWorkflowId(), documentId);
    }

    private RouteHeaderService getRouteHeaderService() {
        return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }

    private UserService getUserService() {
        return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    }
}
