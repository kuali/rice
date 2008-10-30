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
package org.kuali.rice.kew.actionlist.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.ActionItemActionListExtension;
import org.kuali.rice.kew.actionitem.OutboxItemActionListExtension;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionlist.dao.ActionListDAO;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


/**
 * OJB implementation of the {@link ActionListDAO}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListDAOOjbImpl extends PersistenceBrokerDaoSupport implements ActionListDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListDAOOjbImpl.class);

    public Collection<ActionItem> getActionList(WorkflowUser workflowUser, ActionListFilter filter) {
        return getActionItemsInActionList(ActionItemActionListExtension.class, workflowUser, filter);
//        LOG.debug("getting action list for user " + workflowUser.getWorkflowUserId().getWorkflowId());
//        Criteria crit = null;
//        if (filter == null) {
//            crit = new Criteria();
//            crit.addEqualTo("workflowId", workflowUser.getWorkflowUserId().getWorkflowId());
//        } else {
//            crit = setUpActionListCriteria(workflowUser, filter);
//        }
//        LOG.debug("running query to get action list for criteria " + crit);
//        Collection<ActionItem> collection = this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionItemActionListExtension.class, crit));
//        LOG.debug("found " + collection.size() + " action items for user " + workflowUser.getWorkflowUserId().getWorkflowId());
//        return createActionListForUser(collection);
    }
    
    public Collection<ActionItem> getActionListForSingleDocument(Long routeHeaderId) {
        LOG.debug("getting action list for route header id " + routeHeaderId);
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", routeHeaderId);
        Collection<ActionItem> collection = this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ActionItemActionListExtension.class, crit));
        LOG.debug("found " + collection.size() + " action items for route header id " + routeHeaderId);
        return createActionListForRouteHeader(collection);
    }
    
    private Criteria setUpActionListCriteria(WorkflowUser user, ActionListFilter filter) {
        LOG.debug("setting up Action List criteria");
        Criteria crit = new Criteria();
        boolean filterOn = false;
        String filteredByItems = "";
        
        if (filter.getActionRequestCd() != null && !"".equals(filter.getActionRequestCd().trim()) && !filter.getActionRequestCd().equals(KEWConstants.ALL_CODE)) {
            if (filter.isExcludeActionRequestCd()) {
                crit.addNotEqualTo("actionRequestCd", filter.getActionRequestCd());
            } else {
                crit.addEqualTo("actionRequestCd", filter.getActionRequestCd());
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Action Requested";
        }

        if (filter.getCreateDateFrom() != null || filter.getCreateDateTo() != null) {
            if (filter.isExcludeCreateDate()) {
                if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() != null) {
                    crit.addNotBetween("routeHeader.createDate", new Timestamp(beginningOfDay(filter.getCreateDateFrom()).getTime()), new Timestamp(endOfDay(filter.getCreateDateTo()).getTime()));
                } else if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() == null) {
                    crit.addLessOrEqualThan("routeHeader.createDate", new Timestamp(beginningOfDay(filter.getCreateDateFrom()).getTime()));
                } else if (filter.getCreateDateFrom() == null && filter.getCreateDateTo() != null) {
                    crit.addGreaterOrEqualThan("routeHeader.createDate", new Timestamp(endOfDay(filter.getCreateDateTo()).getTime()));
                }
            } else {
                if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() != null) {
                    crit.addBetween("routeHeader.createDate", new Timestamp(beginningOfDay(filter.getCreateDateFrom()).getTime()), new Timestamp(endOfDay(filter.getCreateDateTo()).getTime()));
                } else if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() == null) {
                    crit.addGreaterOrEqualThan("routeHeader.createDate", new Timestamp(beginningOfDay(filter.getCreateDateFrom()).getTime()));
                } else if (filter.getCreateDateFrom() == null && filter.getCreateDateTo() != null) {
                    crit.addLessOrEqualThan("routeHeader.createDate", new Timestamp(endOfDay(filter.getCreateDateTo()).getTime()));
                }
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Date Created";
        }

        if (filter.getDocRouteStatus() != null && !"".equals(filter.getDocRouteStatus().trim()) && !filter.getDocRouteStatus().equals(KEWConstants.ALL_CODE)) {
            if (filter.isExcludeRouteStatus()) {
                crit.addNotEqualTo("routeHeader.docRouteStatus", filter.getDocRouteStatus());
            } else {
                crit.addEqualTo("routeHeader.docRouteStatus", filter.getDocRouteStatus());
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Document Route Status";
        }

        if (filter.getDocumentTitle() != null && !"".equals(filter.getDocumentTitle().trim())) {
            String docTitle = filter.getDocumentTitle();
            if (docTitle.trim().endsWith("*")) {
                docTitle = docTitle.substring(0, docTitle.length() - 1);
            }

            if (filter.isExcludeDocumentTitle()) {
                crit.addNotLike("docTitle", "%" + docTitle + "%");
            } else {
                crit.addLike("docTitle", "%" + docTitle + "%");
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Document Title";
        }

        if (filter.getDocumentType() != null && !"".equals(filter.getDocumentType().trim())) {
            if (filter.isExcludeDocumentType()) {
                crit.addNotLike("docName", "%" + filter.getDocumentType() + "%");
            } else {
                crit.addLike("docName", "%" + filter.getDocumentType() + "%");
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Document Type";
        }

        if (filter.getLastAssignedDateFrom() != null || filter.getLastAssignedDateTo() != null) {
            if (filter.isExcludeLastAssignedDate()) {
                if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() != null) {
                    crit.addNotBetween("dateAssigned", new Timestamp(beginningOfDay(filter.getLastAssignedDateFrom()).getTime()), new Timestamp(endOfDay(filter.getLastAssignedDateTo()).getTime()));
                } else if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() == null) {
                    crit.addLessOrEqualThan("dateAssigned", new Timestamp(beginningOfDay(filter.getLastAssignedDateFrom()).getTime()));
                } else if (filter.getLastAssignedDateFrom() == null && filter.getLastAssignedDateTo() != null) {
                    crit.addGreaterOrEqualThan("dateAssigned", new Timestamp(endOfDay(filter.getLastAssignedDateTo()).getTime()));
                }
            } else {
                if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() != null) {
                    crit.addBetween("dateAssigned", new Timestamp(beginningOfDay(filter.getLastAssignedDateFrom()).getTime()), new Timestamp(endOfDay(filter.getLastAssignedDateTo()).getTime()));
                } else if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() == null) {
                    crit.addGreaterOrEqualThan("dateAssigned", new Timestamp(beginningOfDay(filter.getLastAssignedDateFrom()).getTime()));
                } else if (filter.getLastAssignedDateFrom() == null && filter.getLastAssignedDateTo() != null) {
                    crit.addLessOrEqualThan("dateAssigned", new Timestamp(endOfDay(filter.getLastAssignedDateTo()).getTime()));
                }
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Date Last Assigned";
        }

        filter.setWorkgroupId(null);
        if (filter.getWorkgroupIdString() != null && !"".equals(filter.getWorkgroupIdString().trim()) && !filter.getWorkgroupIdString().trim().equals(KEWConstants.NO_FILTERING)) {
            filter.setWorkgroupId(new Long (filter.getWorkgroupIdString().trim()));
            if (filter.isExcludeWorkgroupId()) {
                Criteria critNotEqual = new Criteria();
                critNotEqual.addNotEqualTo("workgroupId", filter.getWorkgroupId());
                Criteria critNull = new Criteria();
                critNull.addIsNull("workgroupId");
                critNotEqual.addOrCriteria(critNull);
                crit.addAndCriteria(critNotEqual);
            } else {
                crit.addEqualTo("workgroupId", filter.getWorkgroupId());
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Action Request Workgroup";
        }

        if (filteredByItems.length() > 0) {
            filterOn = true;
        }
        
        boolean addedDelegationCriteria = false;
        if (StringUtils.isBlank(filter.getDelegationType()) && StringUtils.isBlank(filter.getPrimaryDelegateId()) && StringUtils.isBlank(filter.getDelegatorId())) {
            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
            addedDelegationCriteria = true;
        } else if ((StringUtils.isNotBlank(filter.getDelegationType()) && KEWConstants.DELEGATION_PRIMARY.equals(filter.getDelegationType()))
                || StringUtils.isNotBlank(filter.getPrimaryDelegateId())) {
            // using a primary delegation
            if ((StringUtils.isBlank(filter.getPrimaryDelegateId())) || (filter.getPrimaryDelegateId().trim().equals(KEWConstants.ALL_CODE))) {
                // user wishes to see all primary delegations
                Criteria userCrit = new Criteria();
                Criteria groupCrit = new Criteria();
                Criteria orCrit = new Criteria();
                userCrit.addEqualTo("delegatorWorkflowId", user.getWorkflowUserId().getWorkflowId());
                groupCrit.addIn("delegatorWorkgroupId", KEWServiceLocator.getWorkgroupService().getUsersGroupIds(user));
                orCrit.addOrCriteria(userCrit);
                orCrit.addOrCriteria(groupCrit);
                crit.addAndCriteria(orCrit);
                crit.addEqualTo("delegationType", KEWConstants.DELEGATION_PRIMARY);
                filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
                filter.setExcludeDelegationType(false);
                addToFilterDescription(filteredByItems, "Primary Delegator Id");
                addedDelegationCriteria = true;
                filterOn = true;
            } else if (!filter.getPrimaryDelegateId().trim().equals(KEWConstants.PRIMARY_DELEGATION_DEFAULT)) {
                // user wishes to see primary delegation for a single user
                crit.addEqualTo("workflowId", filter.getPrimaryDelegateId());
                Criteria userCrit = new Criteria();
                Criteria groupCrit = new Criteria();
                Criteria orCrit = new Criteria();
                userCrit.addEqualTo("delegatorWorkflowId", user.getWorkflowUserId().getWorkflowId());
                groupCrit.addIn("delegatorWorkgroupId", KEWServiceLocator.getWorkgroupService().getUsersGroupIds(user));
                orCrit.addOrCriteria(userCrit);
                orCrit.addOrCriteria(groupCrit);
                crit.addAndCriteria(orCrit);
                crit.addEqualTo("delegationType", KEWConstants.DELEGATION_PRIMARY);
                filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
                filter.setExcludeDelegationType(false);
                addToFilterDescription(filteredByItems, "Primary Delegator Id");
                addedDelegationCriteria = true;
                filterOn = true;
            }
        } else if ((StringUtils.isNotBlank(filter.getDelegationType()) && KEWConstants.DELEGATION_SECONDARY.equals(filter.getDelegationType()))
                || StringUtils.isNotBlank(filter.getDelegatorId())) {
            // using a secondary delegation
            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
            if (StringUtils.isBlank(filter.getDelegatorId())) {
                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
                // if isExcludeDelegationType() we want to show the default aciton list which is set up later in this method
                if (!filter.isExcludeDelegationType()) {
                    crit.addEqualTo("delegationType", KEWConstants.DELEGATION_SECONDARY);
                    addToFilterDescription(filteredByItems, "Secondary Delegator Id");
                    addedDelegationCriteria = true;
                    filterOn = true;
                }
            } else if (filter.getDelegatorId().trim().equals(KEWConstants.ALL_CODE)) {
                // user wishes to see all secondary delegations
                crit.addEqualTo("delegationType", KEWConstants.DELEGATION_SECONDARY);
                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
                filter.setExcludeDelegationType(false);
                addToFilterDescription(filteredByItems, "Secondary Delegator Id");
                addedDelegationCriteria = true;
                filterOn = true;
            } else if (!filter.getDelegatorId().trim().equals(
                    KEWConstants.DELEGATION_DEFAULT)) {
                // user has specified an id to see for secondary delegation
                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
                filter.setExcludeDelegationType(false);
                Criteria userCrit = new Criteria();
                Criteria groupCrit = new Criteria();
                if (filter.isExcludeDelegatorId()) {
                    Criteria userNull = new Criteria();
                    userCrit.addNotEqualTo("delegatorWorkflowId", filter.getDelegatorId());
                    userNull.addIsNull("delegatorWorkflowId");
                    userCrit.addOrCriteria(userNull);
                    Criteria groupNull = new Criteria();
                    groupCrit.addNotEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
                    groupNull.addIsNull("delegatorWorkgroupId");
                    groupCrit.addOrCriteria(groupNull);
                    crit.addAndCriteria(userCrit);
                    crit.addAndCriteria(groupCrit);
                } else {
                    Criteria orCrit = new Criteria();
                    userCrit.addEqualTo("delegatorWorkflowId", filter.getDelegatorId());
                    groupCrit.addEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
                    orCrit.addOrCriteria(userCrit);
                    orCrit.addOrCriteria(groupCrit);
                    crit.addAndCriteria(orCrit);
                }
                addToFilterDescription(filteredByItems, "Secondary Delegator Id");
                addedDelegationCriteria = true;
                filterOn = true;
            }
//            } else if ( (StringUtils.isNotBlank(filter.getDelegationType()) && KEWConstants.DELEGATION_DEFAULT.equals(filter.getDelegationType())) || 
//                    StringUtils.isNotBlank(filter.getDelegatorId()) ) {
//            // not using a primary delegation so we can assume the action item will be assigned to the given user
//            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
//            if (filter.getDelegatorId() != null && !"".equals(filter.getDelegatorId().trim()) && !filter.getDelegatorId().trim().equals(KEWConstants.DELEGATION_DEFAULT)
//                    && !filter.getDelegatorId().trim().equals(KEWConstants.ALL_CODE)) {
//                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//                filter.setExcludeDelegationType(false);
//                Criteria userCrit = new Criteria();
//                Criteria groupCrit = new Criteria();
//                if (filter.isExcludeDelegatorId()) {
//                    Criteria userNull = new Criteria();
//                    userCrit.addNotEqualTo("delegatorWorkflowId", filter.getDelegatorId());
//                    userNull.addIsNull("delegatorWorkflowId");
//                    userCrit.addOrCriteria(userNull);
//                    Criteria groupNull = new Criteria();
//                    groupCrit.addNotEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
//                    groupNull.addIsNull("delegatorWorkgroupId");
//                    groupCrit.addOrCriteria(groupNull);
//                    crit.addAndCriteria(userCrit);
//                    crit.addAndCriteria(groupCrit);
//                } else {
//                    Criteria orCrit = new Criteria();
//                    userCrit.addEqualTo("delegatorWorkflowId", filter.getDelegatorId());
//                    groupCrit.addEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
//                    orCrit.addOrCriteria(userCrit);
//                    orCrit.addOrCriteria(groupCrit);
//                    crit.addAndCriteria(orCrit);
//                }
//                addToFilterDescription(filteredByItems, "Secondary Delegator Id");
//                addedDelegationCriteria = true;
//            } else if (filter.getDelegatorId().trim().equals(KEWConstants.ALL_CODE)) {
//                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//                filter.setExcludeDelegationType(false);
//                addToFilterDescription(filteredByItems, "Secondary Delegator Id");
//                addedDelegationCriteria = true;
//            }
        }
        
        // if we haven't added delegation criteria then use the default criteria below
        if (!addedDelegationCriteria) {
            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
            filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
            filter.setExcludeDelegationType(true);
            Criteria critNotEqual = new Criteria();
            Criteria critNull = new Criteria();
            critNotEqual.addNotEqualTo("delegationType", KEWConstants.DELEGATION_SECONDARY);
            critNull.addIsNull("delegationType");
            critNotEqual.addOrCriteria(critNull);
            crit.addAndCriteria(critNotEqual);
        }

                
//        if (filter.getPrimaryDelegateId().equals(KEWConstants.PRIMARY_DELEGATION_DEFAULT) && filter.getDelegatorId().equals(KEWConstants.DELEGATION_DEFAULT)) {
//            // no secondary or primary delegation displayed
//            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
//            filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//            Criteria critNotEqual = new Criteria();
//            Criteria critNull = new Criteria();
//            critNotEqual.addNotEqualTo("delegationType", KEWConstants.DELEGATION_SECONDARY);
//            critNull.addIsNull("delegationType");
//            critNotEqual.addOrCriteria(critNull);
//            crit.addAndCriteria(critNotEqual);
//            filter.setExcludeDelegationType(true);
//        } else if (filter.getPrimaryDelegateId().trim().equals(KEWConstants.ALL_CODE)) {
//            // user wishes to see all primary delegations
//            Criteria userCrit = new Criteria();
//            Criteria groupCrit = new Criteria();
//            Criteria orCrit = new Criteria();
//            userCrit.addEqualTo("delegatorWorkflowId", user.getWorkflowUserId().getWorkflowId());
//            groupCrit.addEqualTo("delegatorWorkgroupId", filter.getPrimaryDelegateId()); // TODO delyea: add all workgroups here?
//            orCrit.addOrCriteria(userCrit);
//            orCrit.addOrCriteria(groupCrit);
//            crit.addAndCriteria(orCrit);
//            crit.addEqualTo("delegationType", KEWConstants.DELEGATION_PRIMARY);
//            filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
//            filter.setExcludeDelegationType(false);
//            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
//            filteredByItems += "Primary Delegator Id";
//            filterOn = true;
//        } else if (filter.getDelegatorId().trim().equals(KEWConstants.ALL_CODE)) {
//            // user wishes to see all secondary delegations
//            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
//            crit.addEqualTo("delegationType", KEWConstants.DELEGATION_SECONDARY);
//            filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//            filter.setExcludeDelegationType(false);
//            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
//            filteredByItems += "Secondary Delegator Id";
//            filterOn = true;
//        } else if (filter.getPrimaryDelegateId() != null && !"".equals(filter.getPrimaryDelegateId().trim())) {
//            // user wishes to see primary delegation for a single user
//            Criteria userCrit = new Criteria();
//            Criteria groupCrit = new Criteria();
//            Criteria orCrit = new Criteria();
//            userCrit.addEqualTo("delegatorWorkflowId", user.getWorkflowUserId().getWorkflowId());
//            groupCrit.addEqualTo("delegatorWorkgroupId", filter.getDelegatorId()); // TODO delyea: add all workgroups here?
//            orCrit.addOrCriteria(userCrit);
//            orCrit.addOrCriteria(groupCrit);
//            crit.addAndCriteria(orCrit);
//            crit.addEqualTo("delegationType", KEWConstants.DELEGATION_PRIMARY);
//            filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
//            filter.setExcludeDelegationType(false);
//            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
//            filteredByItems += "Primary Delegator Id";
//            filterOn = true;
//        } else if (filter.getDelegatorId() != null && !"".equals(filter.getDelegatorId().trim())) {
//            // user wishes to see secondary delegation for a single user
//            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
//            crit.addEqualTo("delegationType", KEWConstants.DELEGATION_SECONDARY);
//            filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//            filter.setExcludeDelegationType(false);
//            Criteria userCrit = new Criteria();
//            Criteria groupCrit = new Criteria();
//            if (filter.isExcludeDelegatorId()) {
//                Criteria userNull = new Criteria();
//                userCrit.addNotEqualTo("delegatorWorkflowId", filter.getDelegatorId());
//                userNull.addIsNull("delegatorWorkflowId");
//                userCrit.addOrCriteria(userNull);
//                Criteria groupNull = new Criteria();
//                groupCrit.addNotEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
//                groupNull.addIsNull("delegatorWorkgroupId");
//                groupCrit.addOrCriteria(groupNull);
//                crit.addAndCriteria(userCrit);
//                crit.addAndCriteria(groupCrit);
//            } else {
//                Criteria orCrit = new Criteria();
//                userCrit.addEqualTo("delegatorWorkflowId", filter.getDelegatorId());
//                groupCrit.addEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
//                orCrit.addOrCriteria(userCrit);
//                orCrit.addOrCriteria(groupCrit);
//                crit.addAndCriteria(orCrit);
//            }
//            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
//            filteredByItems += "SeDelegator Id";
//            filterOn = true;
//        } else if (StringUtils.isBlank(filter.getPrimaryDelegateId()) && StringUtils.isBlank(filter.getDelegatorId())) {
//            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
//            if (filter.getDelegationType() != null && !"".equals(filter.getDelegationType().trim())) {
//                if (filter.isExcludeDelegationType()) {
//                    Criteria critNotEqual = new Criteria();
//                    Criteria critNull = new Criteria();
//                    critNotEqual.addNotEqualTo("delegationType", filter.getDelegationType());
//                    critNull.addIsNull("delegationType");
//                    critNotEqual.addOrCriteria(critNull);
//                    crit.addAndCriteria(critNotEqual);
//                } else {
//                    crit.addEqualTo("delegationType", filter.getDelegationType());
//                }
//            }
//        }

        
//        if (primary delegation) {
//            filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
//            crit.addEqualTo("delegatorWorkflowId", user.getWorkflowUserId().getWorkflowId());
//            
//        } else {
//            crit.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
//            if (filter.getDelegatorId() != null && !"".equals(filter.getDelegatorId().trim()) && !filter.getDelegatorId().trim().equals(KEWConstants.DELEGATION_DEFAULT)
//                    && !filter.getDelegatorId().trim().equals(KEWConstants.ALL_CODE)) {
//                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//                filter.setExcludeDelegationType(false);
//                Criteria userCrit = new Criteria();
//                Criteria groupCrit = new Criteria();
//                if (filter.isExcludeDelegatorId()) {
//                    Criteria userNull = new Criteria();
//                    userCrit.addNotEqualTo("delegatorWorkflowId", filter.getDelegatorId());
//                    userNull.addIsNull("delegatorWorkflowId");
//                    userCrit.addOrCriteria(userNull);
//                    Criteria groupNull = new Criteria();
//                    groupCrit.addNotEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
//                    groupNull.addIsNull("delegatorWorkgroupId");
//                    groupCrit.addOrCriteria(groupNull);
//                    crit.addAndCriteria(userCrit);
//                    crit.addAndCriteria(groupCrit);
//                } else {
//                    Criteria orCrit = new Criteria();
//                    userCrit.addEqualTo("delegatorWorkflowId", filter.getDelegatorId());
//                    groupCrit.addEqualTo("delegatorWorkgroupId", filter.getDelegatorId());
//                    orCrit.addOrCriteria(userCrit);
//                    orCrit.addOrCriteria(groupCrit);
//                    crit.addAndCriteria(orCrit);
//                }
//                filteredByItems += filteredByItems.length() > 0 ? ", " : "";
//                filteredByItems += "Delegator Id";
//                filterOn = true;
//            } else if (filter.getDelegatorId().trim().equals(KEWConstants.DELEGATION_DEFAULT)) {
//                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//                filter.setExcludeDelegationType(true);
//            } else if (filter.getDelegatorId().trim().equals(KEWConstants.ALL_CODE)) {
//                filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//                filter.setExcludeDelegationType(false);
//                filteredByItems += filteredByItems.length() > 0 ? ", " : "";
//                filteredByItems += "Delegator Id";
//                filterOn = true;
//            }
//            
//        }
//
//
//        //must come after delegation id since the delegation choices are all secondary delegations
//        if (filter.getDelegationType() != null && !"".equals(filter.getDelegationType().trim())) {
//            if (filter.isExcludeDelegationType()) {
//                Criteria critNotEqual = new Criteria();
//                Criteria critNull = new Criteria();
//                critNotEqual.addNotEqualTo("delegationType", filter.getDelegationType());
//                critNull.addIsNull("delegationType");
//                critNotEqual.addOrCriteria(critNull);
//                crit.addAndCriteria(critNotEqual);
//            } else {
//                crit.addEqualTo("delegationType", filter.getDelegationType());
//            }
//        }

        if (! "".equals(filteredByItems)) {
            filteredByItems = "Filtered by " + filteredByItems;
        }
        filter.setFilterLegend(filteredByItems);
        filter.setFilterOn(filterOn);

        LOG.debug("returning from Action List criteria");
        return crit;
    }
    
    private void addToFilterDescription(String filterDescription, String labelToAdd) {
        filterDescription += filterDescription.length() > 0 ? ", " : "";
        filterDescription += labelToAdd;
    }

    private static final String ACTION_LIST_COUNT_QUERY = "select count(distinct(ai.doc_hdr_id)) from krew_actn_itm_t ai where ai.PRNCPL_ID = ? and (ai.dlgn_typ is null or ai.dlgn_typ = 'P')";

    public int getCount(final String workflowId) {
    	return (Integer)getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
                PreparedStatement statement = null;
                ResultSet resultSet = null;
                try {
                    Connection connection = broker.serviceConnectionManager().getConnection();
                    statement = connection.prepareStatement(ACTION_LIST_COUNT_QUERY);
                    statement.setString(1, workflowId);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        throw new WorkflowRuntimeException("Error determining Action List Count.");
                    }
                    return resultSet.getInt(1);
                } catch (SQLException e) {
                    throw new WorkflowRuntimeException("Error determining Action List Count.", e);
                } catch (LookupException e) {
                    throw new WorkflowRuntimeException("Error determining Action List Count.", e);
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {}
                    }
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {}
                    }
                }
            }
        });
    }

    /**
     * Creates an Action List from the given collection of Action Items.  The Action List should
     * contain only one action item per document.  The action item chosen should be the most "critical"
     * or "important" one on the document.
     *
     * @return the Action List as a Collection of ActionItems
     */
    private Collection<ActionItem> createActionListForUser(Collection<ActionItem> actionItems) {
        Map<Long, ActionItem> actionItemMap = new HashMap<Long, ActionItem>();
        ActionListPriorityComparator comparator = new ActionListPriorityComparator();
        for (ActionItem potentialActionItem: actionItems) {
            ActionItem existingActionItem = actionItemMap.get(potentialActionItem.getRouteHeaderId());
            if (existingActionItem == null || comparator.compare(potentialActionItem, existingActionItem) > 0) {
                actionItemMap.put(potentialActionItem.getRouteHeaderId(), potentialActionItem);
            }
        }
        return actionItemMap.values();
    }

    /**
     * Creates an Action List from the given collection of Action Items.  The Action List should
     * contain only one action item per user.  The action item chosen should be the most "critical"
     * or "important" one on the document.
     *
     * @return the Action List as a Collection of ActionItems
     */
    private Collection<ActionItem> createActionListForRouteHeader(Collection<ActionItem> actionItems) {
        Map<String, ActionItem> actionItemMap = new HashMap<String, ActionItem>();
        ActionListPriorityComparator comparator = new ActionListPriorityComparator();
        for (ActionItem potentialActionItem: actionItems) {
            ActionItem existingActionItem = actionItemMap.get(potentialActionItem.getWorkflowId());
            if (existingActionItem == null || comparator.compare(potentialActionItem, existingActionItem) > 0) {
                actionItemMap.put(potentialActionItem.getWorkflowId(), potentialActionItem);
            }
        }
        return actionItemMap.values();
    }
    
    private Collection<ActionItem> getActionItemsInActionList(Class objectsToRetrieve, WorkflowUser workflowUser, ActionListFilter filter) {
        LOG.debug("getting action list for user " + workflowUser.getWorkflowUserId().getWorkflowId());
        Criteria crit = null;
        if (filter == null) {
            crit = new Criteria();
            crit.addEqualTo("workflowId", workflowUser.getWorkflowUserId().getWorkflowId());
        } else {
            crit = setUpActionListCriteria(workflowUser, filter);
        }
        LOG.debug("running query to get action list for criteria " + crit);
        Collection<ActionItem> collection = this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(objectsToRetrieve, crit));
        LOG.debug("found " + collection.size() + " action items for user " + workflowUser.getWorkflowUserId().getWorkflowId());
        return createActionListForUser(collection);
    }

    public Collection<ActionItem> getOutbox(WorkflowUser workflowUser, ActionListFilter filter) {
        return getActionItemsInActionList(OutboxItemActionListExtension.class, workflowUser, filter);
//        LOG.debug("getting action list for user " + workflowUser.getWorkflowUserId().getWorkflowId());
//        Criteria crit = new Criteria();
//        crit.addEqualTo("workflowId", workflowUser.getWorkflowUserId().getWorkflowId());
//        if (filter != null) {
//            setUpActionListCriteria(workflowUser, filter);
//        }
//        LOG.debug("running query to get action list for criteria " + crit);
//        Collection<ActionItem> collection = this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(OutboxItemActionListExtension.class, crit));
//        LOG.debug("found " + collection.size() + " action items for user " + workflowUser.getWorkflowUserId().getWorkflowId());
//        return createActionListForUser(collection);
    }

    /**
     * Deletes all outbox items specified by the list of ids
     * 
     * @see org.kuali.rice.kew.actionlist.dao.ActionListDAO#removeOutboxItems(org.kuali.rice.kew.user.WorkflowUser, java.util.List)
     */
    public void removeOutboxItems(WorkflowUser workflowUser, List<Long> outboxItems) {
        Criteria crit = new Criteria();
        crit.addIn("actionItemId", outboxItems);
        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(OutboxItemActionListExtension.class, crit));
    }

    /**
     * Saves an outbox item
     * 
     * @see org.kuali.rice.kew.actionlist.dao.ActionListDAO#saveOutboxItem(org.kuali.rice.kew.actionitem.OutboxItemActionListExtension)
     */
    public void saveOutboxItem(OutboxItemActionListExtension outboxItem) {
        this.getPersistenceBrokerTemplate().store(outboxItem);
    }

    /**
     * Gets the outbox item associated with the document id
     * 
     * @see org.kuali.rice.kew.actionlist.dao.ActionListDAO#getOutboxByDocumentId(java.lang.Long)
     */
    public OutboxItemActionListExtension getOutboxByDocumentId(Long documentId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", documentId);
        return (OutboxItemActionListExtension)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(OutboxItemActionListExtension.class, crit));
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kew.actionlist.dao.ActionListDAO#getOutboxByDocumentIdUserId(java.lang.Long)
     */
    public OutboxItemActionListExtension getOutboxByDocumentIdUserId(Long documentId, String userId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("routeHeaderId", documentId);
        crit.addEqualTo("workflowId", userId);
        return (OutboxItemActionListExtension)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(OutboxItemActionListExtension.class, crit));
    }
    
    private Date beginningOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
    
    private Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();        
    }
    
}