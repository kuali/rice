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
package org.kuali.rice.kew.actiontaken.dao.impl;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.actiontaken.dao.ActionTakenDAO;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;


/**
 * OJB implementation of the {@link ActionTakenDAO}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionTakenDAOOjbImpl extends PersistenceBrokerDaoSupport implements ActionTakenDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionTakenDAOOjbImpl.class);

    public ActionTakenValue load(Long id) {
        LOG.debug("Loading Action Taken for the given id " + id);
        Criteria crit = new Criteria();
        crit.addEqualTo("actionTakenId", id);
        return (ActionTakenValue) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public void deleteActionTaken(ActionTakenValue actionTaken) {
        LOG.debug("deleting ActionTaken " + actionTaken.getActionTakenId());
        this.getPersistenceBrokerTemplate().delete(actionTaken);
    }

    public ActionTakenValue findByActionTakenId(Long actionTakenId) {
        LOG.debug("finding Action Taken by actionTakenId " + actionTakenId);
        Criteria crit = new Criteria();
        crit.addEqualTo("actionTakenId", actionTakenId);
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        return (ActionTakenValue) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public Collection<ActionTakenValue> findByDocIdAndAction(Long routeHeaderId, String action) {
        LOG.debug("finding Action Taken by routeHeaderId " + routeHeaderId + " and action " + action);
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("actionTaken", action);
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        return (Collection<ActionTakenValue>) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public Collection<ActionTakenValue> findByRouteHeaderId(Long routeHeaderId) {
        LOG.debug("finding Action Takens by routeHeaderId " + routeHeaderId);
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        return (Collection<ActionTakenValue>) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public List<ActionTakenValue> findByRouteHeaderIdWorkflowId(Long routeHeaderId, String principalId) {
        LOG.debug("finding Action Takens by routeHeaderId " + routeHeaderId + " and principalId" + principalId);
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("principalId", principalId);
        crit.addEqualTo("currentIndicator", Boolean.TRUE);
        return (List<ActionTakenValue>) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public List findByRouteHeaderIdIgnoreCurrentInd(Long routeHeaderId) {
        LOG.debug("finding ActionsTaken ignoring currentInd by routeHeaderId:" + routeHeaderId);
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public void saveActionTaken(ActionTakenValue actionTaken) {
        LOG.debug("saving ActionTaken");
        checkNull(actionTaken.getRouteHeaderId(), "Document ID");
        checkNull(actionTaken.getActionTaken(), "action taken code");
        checkNull(actionTaken.getDocVersion(), "doc version");
        checkNull(actionTaken.getPrincipal(), "user principalId");

        if (actionTaken.getActionDate() == null) {
            actionTaken.setActionDate(new Timestamp(System.currentTimeMillis()));
        }
        if (actionTaken.getCurrentIndicator() == null) {
            actionTaken.setCurrentIndicator(Boolean.TRUE);
        }
        LOG.debug("saving ActionTaken: routeHeader " + actionTaken.getRouteHeaderId() +
                ", actionTaken " + actionTaken.getActionTaken() + ", principalId " + actionTaken.getPrincipalId());
        this.getPersistenceBrokerTemplate().store(actionTaken);
    }

    //TODO perhaps runtime isn't the best here, maybe a dao runtime exception
    private void checkNull(Object value, String valueName) throws RuntimeException {
        if (value == null) {
            throw new RuntimeException("Null value for " + valueName);
        }
    }

    public void deleteByRouteHeaderId(Long routeHeaderId){
	    Criteria crit = new Criteria();
	    crit.addEqualTo("routeHeaderId", routeHeaderId);
	    this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ActionRequestValue.class, crit));
    }

    public boolean hasUserTakenAction(String principalId, Long routeHeaderId) {
    	Criteria crit = new Criteria();
	    crit.addEqualTo("routeHeaderId", routeHeaderId);
	    crit.addEqualTo("principalId", principalId);
	    crit.addEqualTo("currentIndicator", Boolean.TRUE);
        int count = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(ActionTakenValue.class, crit));
        return count > 0;
    }
    
    
    private static final String LAST_ACTION_TAKEN_DATE_QUERY =
            "select max(ACTN_DT) from KREW_ACTN_TKN_T where DOC_HDR_ID=? and ACTN_CD=?";

    //@Override
    public Timestamp getLastActionTakenDate(final Long documentId, final String actionType) {
        return (Timestamp) getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Timestamp doInPersistenceBroker(PersistenceBroker broker) {
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                try {
                    Connection connection = broker.serviceConnectionManager().getConnection();
                    statement = connection.prepareStatement(LAST_ACTION_TAKEN_DATE_QUERY);
                    statement.setLong(1, documentId);
                    statement.setString(2, actionType);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        return null;
                    } else {
                        return resultSet.getTimestamp(1);
                    }
                } catch (Exception e) {
                    throw new WorkflowRuntimeException("Error determining Last Action Taken Date.", e);
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
        });
    }
}
