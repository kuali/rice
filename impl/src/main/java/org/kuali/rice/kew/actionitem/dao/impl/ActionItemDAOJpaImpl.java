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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.util.OrmUtils;
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
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
/**
 * OJB implementation of {@link ActionItemDAO}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionItemDAOJpaImpl implements ActionItemDAO {
	@PersistenceContext(unitName="kew-unit")
	private EntityManager entityManager;

    public ActionItem findByActionItemId(Long actionItemId) {
    	return entityManager.find(ActionItem.class, actionItemId);
    }

    public void deleteActionItems(Long actionRequestId) {
        Criteria crit = new Criteria(ActionItem.class.getName());
        crit.eq("actionRequestId", actionRequestId);
        for(Object actionItem:new QueryByCriteria(entityManager,crit).toQuery().getResultList()){
        	entityManager.remove(actionItem);
        }

    }

    public void deleteActionItem(ActionItem actionItem) {
    	entityManager.remove(findByActionItemId(actionItem.getActionItemId()));
    }

    public void deleteByRouteHeaderIdWorkflowUserId(Long routeHeaderId, String workflowUserId) {
        Criteria crit = new Criteria(ActionItem.class.getName());
        crit.eq("routeHeader.routeHeaderId", routeHeaderId);
        crit.eq("principalId", workflowUserId);
        for(Object actionItem: new QueryByCriteria(entityManager,crit).toQuery().getResultList()){
        	entityManager.remove(actionItem);
        }
    }

    public void deleteByRouteHeaderId(Long routeHeaderId) {
        Criteria crit = new Criteria(ActionItem.class.getName());
        crit.eq("routeHeader.routeHeaderId", routeHeaderId);
        for(Object actionItem: new QueryByCriteria(entityManager,crit).toQuery().getResultList()){
        	entityManager.remove(actionItem);
        }
    }

    public Collection<ActionItem> findByWorkflowUser(WorkflowUser workflowUser) {
        Criteria crit = new Criteria(ActionItem.class.getName());
        crit.eq("principalId", workflowUser.getWorkflowUserId().getWorkflowId());
        crit.orderBy("routeHeader.routeHeaderId", true);
        return new QueryByCriteria(entityManager, crit).toQuery().getResultList();

    }

    public Collection<ActionItem> findByWorkflowUserRouteHeaderId(String workflowId, Long routeHeaderId) {
        Criteria crit = new Criteria(ActionItem.class.getName());
        crit.eq("principalId", workflowId);
        crit.eq("routeHeader.routeHeaderId", routeHeaderId);
        return new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public Collection<ActionItem> findByRouteHeaderId(Long routeHeaderId) {
        Criteria crit = new Criteria(ActionItem.class.getName());
        crit.eq("routeHeader.routeHeaderId", routeHeaderId);
        return new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public Collection<ActionItem> findByActionRequestId(Long actionRequestId) {
        Criteria crit = new Criteria(ActionItem.class.getName());
        crit.eq("actionRequestId", actionRequestId);
        return new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public void saveActionItem(ActionItem actionItem) {
//    	OrmUtils.reattach(actionItem, entityManager.merge(actionItem));
    	if(actionItem.getActionItemId()==null){
        	entityManager.persist(actionItem);
    	}else{
    		OrmUtils.reattach(actionItem, entityManager.merge(actionItem));
    	}
    }

    public Collection<Recipient> findSecondaryDelegators(WorkflowUser user) throws KEWUserNotFoundException {
        Criteria notNullWorkflowCriteria = new Criteria(ActionItem.class.getName());
        notNullWorkflowCriteria.notNull("delegatorWorkflowId");
        Criteria notNullWorkgroupCriteria = new Criteria(ActionItem.class.getName());
        notNullWorkgroupCriteria.notNull("delegatorGroupId");
        Criteria orCriteria = new Criteria(ActionItem.class.getName());
        orCriteria.or(notNullWorkflowCriteria);
        orCriteria.or(notNullWorkgroupCriteria);
        Criteria criteria = new Criteria(ActionItem.class.getName());
        criteria.eq("principalId", user.getWorkflowUserId().getWorkflowId());
        criteria.eq("delegationType", KEWConstants.DELEGATION_SECONDARY);
        criteria.and(orCriteria);

        Map<Object, Recipient> delegators = new HashMap<Object, Recipient>();

        for(Object actionItem:new QueryByCriteria(entityManager, criteria).toQuery().getResultList()){
        	String delegatorWorkflowId = ((ActionItem)actionItem).getDelegatorWorkflowId();
        	String delegatorGroupId = ((ActionItem)actionItem).getDelegatorGroupId();

        	if (delegatorWorkflowId != null && !delegators.containsKey(delegatorWorkflowId)) {
                delegators.put(delegatorWorkflowId, getUserService().getWorkflowUser(new WorkflowUserId(delegatorWorkflowId)));
            }else if (delegatorGroupId != null) {
                if (!delegators.containsKey(delegatorGroupId)) {
                    delegators.put(delegatorGroupId, getWorkgroupService().getWorkgroup(new WorkflowGroupId(new Long(delegatorGroupId))));
                }
            }
        }
         return delegators.values();
    }

    public Collection<Recipient> findPrimaryDelegationRecipients(WorkflowUser user) throws KEWUserNotFoundException {
    	Set<Long> workgroupIds = KEWServiceLocator.getWorkgroupService().getUsersGroupIds(user.getWorkflowId());
        Criteria orCriteria = new Criteria(ActionItem.class.getName());
        Criteria delegatorWorkflowIdCriteria = new Criteria(ActionItem.class.getName());
        delegatorWorkflowIdCriteria.eq("delegatorWorkflowId", user.getWorkflowUserId().getWorkflowId());
        if (CollectionUtils.isNotEmpty(workgroupIds)) {
            Criteria delegatorWorkgroupCriteria = new Criteria(ActionItem.class.getName());
            delegatorWorkgroupCriteria.in("delegatorGroupId", new ArrayList(workgroupIds));
            orCriteria.or(delegatorWorkgroupCriteria);
            orCriteria.or(delegatorWorkflowIdCriteria);
        }
        else {
            orCriteria.and(delegatorWorkflowIdCriteria);
        }
        Criteria criteria = new Criteria(ActionItem.class.getName());
        criteria.eq("delegationType", KEWConstants.DELEGATION_PRIMARY);
        criteria.and(orCriteria);

        Map<Object, Recipient> delegators = new HashMap<Object, Recipient>();
        for(Object actionItem:new QueryByCriteria(entityManager, criteria).toQuery().getResultList()){
        	String principalId = ((ActionItem)actionItem).getPrincipalId();
            if (principalId != null && !delegators.containsKey(principalId)) {
                delegators.put(principalId, getUserService().getWorkflowUser(new WorkflowUserId(principalId)));
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