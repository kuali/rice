/*
 * Copyright 2005-2007 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.OutboxItemActionListExtension;
import org.kuali.rice.kew.actionitem.dao.ActionItemDAO;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.WebFriendlyRecipient;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;
/**
 * OJB implementation of {@link ActionItemDAO}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionItemDAOOjbImpl extends PersistenceBrokerDaoSupport implements ActionItemDAO {
	
 
	private static final Logger LOG = Logger.getLogger(ActionItemDAOOjbImpl.class);

	
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
        crit.addEqualTo("principalId", workflowUserId);
        this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public void deleteByRouteHeaderId(Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ActionItem.class, crit));
    }

    public Collection<ActionItem> findByWorkflowUserRouteHeaderId(String workflowId, Long routeHeaderId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("principalId", workflowId);
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

    public Collection<ActionItem> findByDocumentTypeName(String documentTypeName) {
        return getItemsByDocumentType(ActionItem.class, documentTypeName);
    }

    public Collection<ActionItem> getOutboxItemsByDocumentType(String documentTypeName) {
        return getItemsByDocumentType(OutboxItemActionListExtension.class, documentTypeName);
    }

    private Collection<ActionItem> getItemsByDocumentType(Class<? extends ActionItem> objectClass, String documentTypeName) {
        Criteria crit = new Criteria();
        crit.addEqualTo("docName", documentTypeName);
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(objectClass, crit));
    }

    public void saveActionItem(ActionItem actionItem) {
        if (actionItem.getDateAssigned() == null) {
            actionItem.setDateAssigned(new Timestamp(new Date().getTime()));
        }
        this.getPersistenceBrokerTemplate().store(actionItem);
    }

    public Collection<Recipient> findSecondaryDelegators(String principalId) {
        Criteria notNullWorkflowCriteria = new Criteria();
        notNullWorkflowCriteria.addNotNull("delegatorWorkflowId");
        Criteria notNullWorkgroupCriteria = new Criteria();
        notNullWorkgroupCriteria.addNotNull("delegatorGroupId");
        Criteria orCriteria = new Criteria();
        orCriteria.addOrCriteria(notNullWorkflowCriteria);
        orCriteria.addOrCriteria(notNullWorkgroupCriteria);
        Criteria criteria = new Criteria();
        criteria.addEqualTo("principalId", principalId);
        criteria.addEqualTo("delegationType", KEWConstants.DELEGATION_SECONDARY);
        criteria.addAndCriteria(orCriteria);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(ActionItem.class, criteria);

        query.setAttributes(new String[]{"delegatorWorkflowId", "delegatorGroupId"});
        Map<Object, Recipient> delegators = new HashMap<Object, Recipient>();
        Iterator iterator = this.getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (iterator.hasNext()) {
            Object[] ids = (Object[]) iterator.next();
            if (ids[0] != null && !delegators.containsKey((String) ids[0])) {
            	WebFriendlyRecipient rec = new WebFriendlyRecipient(KimApiServiceLocator.getPersonService().getPerson((String) ids[0]));
                delegators.put((String) ids[0], rec);
            } else if (ids[1] != null) {
                String workgroupId = ids[1].toString();
                if (!delegators.containsKey(workgroupId)) {
                    delegators.put(workgroupId, new KimGroupRecipient(getIdentityManagementService().getGroup(workgroupId)));
                }
            }
        }
        return delegators.values();
    }

    public Collection<Recipient> findPrimaryDelegationRecipients(String principalId) {
    	List<String> workgroupIds = KimApiServiceLocator.getIdentityManagementService().getGroupIdsForPrincipal(principalId);
        Criteria orCriteria = new Criteria();
        Criteria delegatorWorkflowIdCriteria = new Criteria();
        delegatorWorkflowIdCriteria.addEqualTo("delegatorWorkflowId", principalId);
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

        query.setAttributes(new String[]{"principalId"});
        Map<Object, Recipient> delegators = new HashMap<Object, Recipient>();
        Iterator iterator = this.getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (iterator.hasNext()) {
            Object[] ids = (Object[]) iterator.next();
            if (ids[0] != null && !delegators.containsKey((String) ids[0])) {
               
            	Person person = KimApiServiceLocator.getPersonService().getPerson((String) ids[0]);
            	if (person != null) {
            		WebFriendlyRecipient rec = new WebFriendlyRecipient(person);
            	    delegators.put((String) ids[0],rec);
            	    LOG.warn("The name for " + (String) ids[0] + " was not added to the primary delegate drop down list because the delegate does not exist.");
            	}
              	
            }
        }
        return delegators.values();
    }

    private IdentityManagementService getIdentityManagementService() {
        return KimApiServiceLocator.getIdentityManagementService();
    }

	/**
	 * This overridden method replaced findByWorkfowUser
	 *
	 * @see org.kuali.rice.kew.actionitem.dao.ActionItemDAO#findByPrincipalId(java.lang.String)
	 */
	public Collection<ActionItem> findByPrincipalId(String principalId) {
		 Criteria crit = new Criteria();
	     crit.addEqualTo("principalId", principalId);
	     QueryByCriteria query = new QueryByCriteria(ActionItem.class, crit);
	     query.addOrderByAscending("routeHeaderId");
	     return this.getPersistenceBrokerTemplate().getCollectionByQuery(query);
	}

}
