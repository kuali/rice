/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kew.actionitem.dao.impl;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.dao.ActionItemDAO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.UserService;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kew.workgroup.WorkgroupService;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import org.kuali.rice.kim.service.*;
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

    public Collection<ActionItem> findByWorkflowUser(WorkflowUser workflowUser) {
        Criteria crit = new Criteria();
        crit.addEqualTo("workflowId", workflowUser.getWorkflowUserId().getWorkflowId());
        QueryByCriteria query = new QueryByCriteria(ActionItem.class, crit);
        query.addOrderByAscending("routeHeader.routeHeaderId");
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    public Collection<ActionItem> findByWorkflowUserRouteHeaderId(String workflowId, Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("workflowId", workflowId);
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public Collection<ActionItem> findByRouteHeaderId(Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public Collection<ActionItem> findByActionRequestId(Long actionRequestId) {
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

    public Collection<Recipient> findSecondaryDelegators(WorkflowUser user) throws KEWUserNotFoundException {
        Criteria notNullWorkflowCriteria = new Criteria();
        notNullWorkflowCriteria.addNotNull("delegatorWorkflowId");
        Criteria notNullWorkgroupCriteria = new Criteria();
        notNullWorkgroupCriteria.addNotNull("delegatorGroupId");
        Criteria orCriteria = new Criteria();
        orCriteria.addOrCriteria(notNullWorkflowCriteria);
        orCriteria.addOrCriteria(notNullWorkgroupCriteria);
        Criteria criteria = new Criteria();
        criteria.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
        criteria.addEqualTo("delegationType", KEWConstants.DELEGATION_SECONDARY);
        criteria.addAndCriteria(orCriteria);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(ActionItem.class, criteria);

        query.setAttributes(new String[]{"delegatorWorkflowId", "delegatorGroupId"});
        Map<Object, Recipient> delegators = new HashMap<Object, Recipient>();
        Iterator iterator = this.getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (iterator.hasNext()) {
            Object[] ids = (Object[]) iterator.next();
            if (ids[0] != null && !delegators.containsKey((String) ids[0])) {
                delegators.put((String) ids[0], getUserService().getWorkflowUser(new WorkflowUserId((String) ids[0])));
            } else if (ids[1] != null) {
                Long workgroupId = new Long(ids[1].toString());
                if (!delegators.containsKey(workgroupId)) {
                    delegators.put(workgroupId, getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId)));
                }
            }
        }
        return delegators.values();
    }

    public Collection<Recipient> findPrimaryDelegationRecipients(WorkflowUser user) throws KEWUserNotFoundException {
    	Set<Long> workgroupIds = KEWServiceLocator.getWorkgroupService().getUsersGroupIds(user);
        Criteria orCriteria = new Criteria();
        Criteria delegatorWorkflowIdCriteria = new Criteria();
        delegatorWorkflowIdCriteria.addEqualTo("delegatorWorkflowId", user.getWorkflowUserId().getWorkflowId());
        if (CollectionUtils.isNotEmpty(workgroupIds)) {
            Criteria delegatorWorkgroupCriteria = new Criteria();
            delegatorWorkgroupCriteria.addIn("delegatorGroupId", workgroupIds);
            orCriteria.addOrCriteria(delegatorWorkgroupCriteria);
            orCriteria.addOrCriteria(delegatorWorkflowIdCriteria);
        }
        else {
            orCriteria.addAndCriteria(delegatorWorkflowIdCriteria);
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo("delegationType", KEWConstants.DELEGATION_PRIMARY);
        criteria.addAndCriteria(orCriteria);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(ActionItem.class, criteria, true);

        query.setAttributes(new String[]{"workflowId"});
        Map<Object, Recipient> delegators = new HashMap<Object, Recipient>();
        Iterator iterator = this.getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (iterator.hasNext()) {
            Object[] ids = (Object[]) iterator.next();
            if (ids[0] != null && !delegators.containsKey((String) ids[0])) {
                delegators.put((String) ids[0], getUserService().getWorkflowUser(new WorkflowUserId((String) ids[0])));
//            } else if (ids[1] != null) {
//                Long workgroupId = new Long(ids[1].toString());
//                if (!delegators.containsKey(workgroupId)) {
//                    delegators.put(workgroupId, getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId)));
//                }
            }
        }
        return delegators.values();
    }

    private UserService getUserService() {
        return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    }

    private WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }
    
    private GroupService getGroupService(){
    	return (GroupService) KIMServiceLocator.getGroupService();
    }
}