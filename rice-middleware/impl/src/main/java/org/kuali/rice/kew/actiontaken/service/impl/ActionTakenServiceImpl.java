/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.kuali.rice.core.api.criteria.CountFlag;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.actiontaken.dao.ActionTakenDao;
import org.kuali.rice.kew.actiontaken.service.ActionTakenService;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * Default implementation of the {@link ActionTakenService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionTakenServiceImpl implements ActionTakenService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionTakenServiceImpl.class);

    private static final String DOCUMENT_ID = "documentId";
    private static final String PRINCIPAL_ID = "principalId";
    private static final String CURRENT_INDICATOR = "currentIndicator";
    private static final String ACTION_DATE = "actionDate";

    private DataObjectService dataObjectService;
    private ActionTakenDao actionTakenDao;

    @Override
    public ActionTakenValue findByActionTakenId(String actionTakenId) {
        return getDataObjectService().find(ActionTakenValue.class, actionTakenId);
    }

    @Override
    public ActionTakenValue getPreviousAction(ActionRequestValue actionRequest) {
    	return getPreviousAction(actionRequest, null);
    }

    @Override
    public ActionTakenValue getPreviousAction(ActionRequestValue actionRequest, List<ActionTakenValue> simulatedActionsTaken) {
        GroupService ims = KimApiServiceLocator.getGroupService();
        ActionTakenValue foundActionTaken = null;
        List<String> principalIds = new ArrayList<String>();
        if (actionRequest.isGroupRequest()) {
            principalIds.addAll( ims.getMemberPrincipalIds(actionRequest.getGroup().getId()));
        } else if (actionRequest.isUserRequest()) {
            principalIds.add(actionRequest.getPrincipalId());
        }

        for (String id : principalIds) {
            List<ActionTakenValue> actionsTakenByUser =
                new ArrayList<ActionTakenValue>(findByDocumentIdPrincipalId(actionRequest.getDocumentId(), id));
            if (simulatedActionsTaken != null) {
                for (ActionTakenValue simulatedAction : simulatedActionsTaken) {
                    if (id.equals(simulatedAction.getPrincipalId())) {
                        actionsTakenByUser.add(simulatedAction);
                    }
                }
            }

            for (ActionTakenValue actionTaken : actionsTakenByUser) {
                if (ActionRequestValue.compareActionCode(actionTaken.getActionTaken(),
                        actionRequest.getActionRequested(), true) >= 0) {
                  foundActionTaken = actionTaken;
                }
            }
        }

        return foundActionTaken;
    }

    @Override
    public Collection<ActionTakenValue> findByDocumentId(String documentId) {
        LOG.debug("finding Action Takens by documentId " + documentId);
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(equal(DOCUMENT_ID,
                documentId), equal(CURRENT_INDICATOR, Boolean.TRUE));
        criteria.setOrderByFields(OrderByField.Builder.create(ACTION_DATE, OrderDirection.ASCENDING).build());
        return getDataObjectService().findMatching(ActionTakenValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionTakenValue> findByDocumentIdPrincipalId(String documentId, String principalId) {
        LOG.debug("finding Action Takens by documentId " + documentId + " and principalId" + principalId);
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(equal(DOCUMENT_ID,
                documentId), equal(PRINCIPAL_ID, principalId), equal(CURRENT_INDICATOR, Boolean.TRUE));
        return getDataObjectService().findMatching(ActionTakenValue.class, criteria.build()).getResults();
    }

    @Override
    public List<ActionTakenValue> findByDocumentIdIgnoreCurrentInd(String documentId) {
        LOG.debug("finding ActionsTaken ignoring currentInd by documentId:" + documentId);
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(equal(DOCUMENT_ID,
                documentId));
        criteria.setOrderByFields(OrderByField.Builder.create(ACTION_DATE, OrderDirection.ASCENDING).build());
        return getDataObjectService().findMatching(ActionTakenValue.class, criteria.build()).getResults();
    }

    @Override
    public ActionTakenValue saveActionTaken(ActionTakenValue actionTaken) {
        LOG.debug("saving ActionTaken");
        checkNull(actionTaken.getDocumentId(), "Document ID");
        checkNull(actionTaken.getActionTaken(), "action taken code");
        checkNull(actionTaken.getDocVersion(), "doc version");
        checkNull(actionTaken.getPrincipal(), "user principalId");

        if (actionTaken.getActionDate() == null) {
            actionTaken.setActionDate(new Timestamp(System.currentTimeMillis()));
        }
        if (actionTaken.getCurrentIndicator() == null) {
            actionTaken.setCurrentIndicator(Boolean.TRUE);
        }
        LOG.debug("saving ActionTaken: routeHeader " + actionTaken.getDocumentId() +
                ", actionTaken " + actionTaken.getActionTaken() + ", principalId " + actionTaken.getPrincipalId());
        return getDataObjectService().save(actionTaken);
    }

    @Override
    public void delete(ActionTakenValue actionTaken) {
        LOG.debug("deleting ActionTaken " + actionTaken.getActionTakenId());
        getDataObjectService().delete(actionTaken);
    }

    @Override
    public boolean hasUserTakenAction(String principalId, String documentId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create().setPredicates(
                equal(DOCUMENT_ID, documentId),
                equal(PRINCIPAL_ID, principalId),
                equal(CURRENT_INDICATOR, Boolean.TRUE)
        );
        criteria.setCountFlag(CountFlag.ONLY);
        return getDataObjectService().findMatching(ActionTakenValue.class, criteria.build()).getTotalRowCount() > 0;
    }


    @Override
    public Timestamp getLastApprovedDate(String documentId)
    {
        return getActionTakenDao().getLastActionTakenDate(documentId, ActionType.APPROVE);
    }

    private void checkNull(Object value, String valueName) throws RuntimeException {
        if (value == null) {
            throw new IllegalArgumentException("Null value for " + valueName);
        }
    }


    public ActionTakenDao getActionTakenDao() {
        return actionTakenDao;
    }

    public void setActionTakenDao(ActionTakenDao actionTakenDao) {
        this.actionTakenDao = actionTakenDao;
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
