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
package edu.iu.uis.eden.actiontaken.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;

/**
 * OJB implementation of the {@link ActionTakenDAO}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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
        crit.addEqualTo("currentIndicator", new Boolean(true));
        return (ActionTakenValue) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public Collection findByDocIdAndAction(Long routeHeaderId, String action) {
        LOG.debug("finding Action Taken by routeHeaderId " + routeHeaderId + " and action " + action);
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("actionTaken", action);
        crit.addEqualTo("currentIndicator", new Boolean(true));
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public Collection findByRouteHeaderId(Long routeHeaderId) {
        LOG.debug("finding Action Takens by routeHeaderId " + routeHeaderId);
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("currentIndicator", new Boolean(true));
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
    }

    public List findByRouteHeaderIdWorkflowId(Long routeHeaderId, String workflowId) {
        LOG.debug("finding Action Takens by routeHeaderId " + routeHeaderId + " and workflowId" + workflowId);
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        crit.addEqualTo("workflowId", workflowId);
        crit.addEqualTo("currentIndicator", new Boolean(true));
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionTakenValue.class, crit));
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
        checkNull(actionTaken.getWorkflowId(), "user workflowId");

        if (actionTaken.getActionDate() == null) {
            actionTaken.setActionDate(new Timestamp(System.currentTimeMillis()));
        }
        if (actionTaken.getCurrentIndicator() == null) {
            actionTaken.setCurrentIndicator(new Boolean(true));
        }
        LOG.debug("saving ActionTaken: routeHeader " + actionTaken.getRouteHeaderId() +
                ", actionTaken " + actionTaken.getActionTaken() + ", workflowId " + actionTaken.getWorkflowId());
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

    public boolean hasUserTakenAction(String workflowId, Long routeHeaderId) {
    	Criteria crit = new Criteria();
	    crit.addEqualTo("routeHeaderId", routeHeaderId);
	    crit.addEqualTo("workflowId", workflowId);
	    crit.addEqualTo("currentIndicator", Boolean.TRUE);
        int count = getPersistenceBrokerTemplate().getCount(new QueryByCriteria(ActionTakenValue.class, crit));
        return count > 0;
    }

}