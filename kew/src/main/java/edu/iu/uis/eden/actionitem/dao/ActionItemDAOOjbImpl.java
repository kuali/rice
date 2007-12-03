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
package edu.iu.uis.eden.actionitem.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * OJB implementation of {@link ActionItemDAO}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionItemDAOOjbImpl extends PersistenceBrokerDaoSupport implements ActionItemDAO {

    public ActionItem findByActionItemId(Long actionItemId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("actionItemId", actionItemId);
        return (ActionItem) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public void deleteActionItems(Long actionRequestId) {
        Criteria crit = new Criteria();
		crit.addEqualTo("actionRequestId", actionRequestId);
		this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public void deleteActionItem(ActionItem actionItem) {
    	this.getPersistenceBrokerTemplate().delete(actionItem);
    }

	public void deleteByRouteHeaderIdWorkflowUserId(Long routeHeaderId, String workflowUserId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("routeHeaderId", routeHeaderId);
		crit.addEqualTo("workflowId", workflowUserId);
		this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ActionItem.class, crit));
	}

    public void deleteByRouteHeaderId(Long routeHeaderId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("routeHeaderId", routeHeaderId);
    	this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public Collection findByWorkflowUser(WorkflowUser workflowUser) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("workflowId", workflowUser.getWorkflowUserId().getWorkflowId());
    	QueryByCriteria query = new QueryByCriteria(ActionItem.class, crit);
        query.addOrderByAscending("routeHeader.routeHeaderId");
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }


    public Collection findByWorkflowUserRouteHeaderId(String workflowId, Long routeHeaderId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("workflowId", workflowId);
    	crit.addEqualTo("routeHeaderId", routeHeaderId);
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public Collection findByRouteHeaderId(Long routeHeaderId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("routeHeaderId", routeHeaderId);
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionItem.class, crit));
    }
    public Collection findByActionRequestId(Long actionRequestId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("actionRequestId", actionRequestId);
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public void saveActionItem(ActionItem actionItem) {
        if (actionItem.getDateAssigned() == null) {
            actionItem.setDateAssigned(new Timestamp(new Date().getTime()));
        }
        this.getPersistenceBrokerTemplate().store(actionItem);
    }

    public Collection findDelegators(WorkflowUser user, String delegationType) throws EdenUserNotFoundException {
        Criteria notNullWorkflowCriteria = new Criteria();
        notNullWorkflowCriteria.addNotNull("delegatorWorkflowId");
        Criteria notNullWorkgroupCriteria = new Criteria();
        notNullWorkgroupCriteria.addNotNull("delegatorWorkgroupId");
        Criteria orCriteria = new Criteria();
        orCriteria.addOrCriteria(notNullWorkflowCriteria);
        orCriteria.addOrCriteria(notNullWorkgroupCriteria);
        Criteria criteria = new Criteria();
        criteria.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
        if (delegationType != null && ! "".equals(delegationType)) {
            criteria.addEqualTo("delegationType", delegationType);
        }
        criteria.addAndCriteria(orCriteria);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(ActionItem.class, criteria);

        query.setAttributes(new String[] { "delegatorWorkflowId", "delegatorWorkgroupId" });
        Map delegators = new HashMap();
        Iterator iterator = this.getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (iterator.hasNext()) {
            Object[] ids = (Object[])iterator.next();
            if (ids[0] != null && !delegators.containsKey((String)ids[0])) {
                delegators.put((String)ids[0], getUserService().getWorkflowUser(new WorkflowUserId((String)ids[0])));
            } else if (ids[1] != null) {
                Long workgroupId = new Long(ids[1].toString());
                if (!delegators.containsKey(workgroupId)) {
                    delegators.put(workgroupId, getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId)));
                }
            }
        }
        return delegators.values();
    }

    private UserService getUserService() {
        return (UserService)KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    }

    private WorkgroupService getWorkgroupService() {
        return (WorkgroupService)KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }

}