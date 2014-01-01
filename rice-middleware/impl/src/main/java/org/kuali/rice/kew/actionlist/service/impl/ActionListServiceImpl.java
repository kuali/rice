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
package org.kuali.rice.kew.actionlist.service.impl;

import static org.kuali.rice.core.api.criteria.PredicateFactory.between;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.greaterThanOrEqual;
import static org.kuali.rice.core.api.criteria.PredicateFactory.in;
import static org.kuali.rice.core.api.criteria.PredicateFactory.isNotNull;
import static org.kuali.rice.core.api.criteria.PredicateFactory.isNull;
import static org.kuali.rice.core.api.criteria.PredicateFactory.lessThanOrEqual;
import static org.kuali.rice.core.api.criteria.PredicateFactory.like;
import static org.kuali.rice.core.api.criteria.PredicateFactory.notBetween;
import static org.kuali.rice.core.api.criteria.PredicateFactory.notEqual;
import static org.kuali.rice.core.api.criteria.PredicateFactory.notLike;
import static org.kuali.rice.core.api.criteria.PredicateFactory.or;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.ActionItemBase;
import org.kuali.rice.kew.actionitem.OutboxItem;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionlist.dao.ActionListDAO;
import org.kuali.rice.kew.actionlist.dao.impl.ActionListPriorityComparator;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.notification.service.NotificationService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.useroptions.UserOptions;
import org.kuali.rice.kew.useroptions.UserOptionsService;
import org.kuali.rice.kew.util.WebFriendlyRecipient;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;

/**
 * Default implementation of the {@link ActionListService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionListServiceImpl implements ActionListService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListServiceImpl.class);

    protected DataObjectService dataObjectService;
    protected NotificationService notificationService;
    protected DateTimeService dateTimeService;
    protected ActionRequestService actionRequestService;
    protected DocumentTypeService documentTypeService;
    protected UserOptionsService userOptionsService;
    protected RouteHeaderService routeHeaderService;

    protected ActionListDAO actionListDAO;

    @Override
    public Collection<Recipient> findUserSecondaryDelegators(String principalId) {

        QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(
                equal("principalId", principalId),
                equal("delegationType", DelegationType.SECONDARY.getCode()),
                or(isNotNull("delegatorPrincipalId"), isNotNull("delegatorGroupId")));

        QueryResults<ActionItem> results = dataObjectService.findMatching(ActionItem.class, query);

        Map<Object, Recipient> delegators = new HashMap<Object, Recipient>(results.getResults().size());

        for ( ActionItem actionItem : results.getResults() ) {
            String delegatorPrincipalId = actionItem.getDelegatorPrincipalId();
            String delegatorGroupId = actionItem.getDelegatorGroupId();

            if (delegatorPrincipalId != null && !delegators.containsKey(delegatorPrincipalId)) {
                delegators.put(delegatorPrincipalId,new WebFriendlyRecipient(KimApiServiceLocator.getPersonService().getPerson(delegatorPrincipalId)));
            } else if (delegatorGroupId != null && !delegators.containsKey(delegatorGroupId)) {
                delegators.put(delegatorGroupId, new KimGroupRecipient(KimApiServiceLocator.getGroupService().getGroup(delegatorGroupId)));
            }
        }

        return delegators.values();
    }

    @Override
    public Collection<Recipient> findUserPrimaryDelegations(String principalId) {
        List<String> workgroupIds = KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(principalId);

        Predicate whoPredicate = null;
        if (CollectionUtils.isNotEmpty(workgroupIds)) {
            whoPredicate = or( equal("delegatorPrincipalId", principalId), in("delegatorGroupId", workgroupIds ) );
        } else {
            whoPredicate = equal("delegatorPrincipalId", principalId);
        }
        QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(whoPredicate, equal("delegationType", DelegationType.PRIMARY.getCode() ) );

        QueryResults<ActionItem> results = dataObjectService.findMatching(ActionItem.class, query);

        Map<String, Recipient> delegators = new HashMap<String, Recipient>(results.getResults().size());

        for ( ActionItem actionItem : results.getResults() ) {
            String recipientPrincipalId = actionItem.getPrincipalId();
            if (recipientPrincipalId != null && !delegators.containsKey(recipientPrincipalId)) {
                delegators.put(recipientPrincipalId, new WebFriendlyRecipient(
                        KimApiServiceLocator.getPersonService().getPerson(recipientPrincipalId)));
            }
        }

        return delegators.values();
    }

    @Override
    public Collection<ActionItem> getActionList(String principalId, ActionListFilter filter) {
        List<String> filteredByItems = new ArrayList<String>();

        List<Predicate> crit = handleActionItemCriteria(principalId, filter, filteredByItems);

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("running query to get action list for criteria " + crit);
        }
        QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(crit);
        QueryResults<ActionItem> results = dataObjectService.findMatching(ActionItem.class, query);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("found " + results.getResults().size() + " action items for user " + principalId);
        }

        if (filter != null) {
            boolean filterOn = !filteredByItems.isEmpty();
            filter.setFilterOn(filterOn);
            filter.setFilterLegend(StringUtils.join(filteredByItems, ", "));
        }

        return createActionListForUser(results.getResults());
    }

    protected List<Predicate> handleActionItemCriteria( String principalId, ActionListFilter filter, List<String> filteredByItems ) {
        LOG.debug("setting up Action List criteria");
        ArrayList<Predicate> crit = new ArrayList<Predicate>();

        if ( filter != null ) {
            handleActionRequestedCriteria(filter, crit, filteredByItems);
            handleDocumentCreateDateCriteria(filter, crit, filteredByItems);
            handleAssignedDateCriteria(filter, crit, filteredByItems);
            handleRouteStatusCriteria(filter, crit, filteredByItems);
            handleDocumentTitleCriteria(filter, crit, filteredByItems);
            handleDocumentTypeCriteria(filter, crit, filteredByItems);
            handleWorkgroupCriteria(filter, crit, filteredByItems);
            handleRecipientCriteria(principalId, filter, crit, filteredByItems);
        } else {
            crit.add( equal("principalId", principalId) );
        }
        LOG.debug( "Completed setting up Action List criteria");
        return crit;
    }

    protected void handleActionRequestedCriteria( ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        if ( StringUtils.isNotBlank(filter.getActionRequestCd())
                && !filter.getActionRequestCd().equals(KewApiConstants.ALL_CODE)) {
            if (filter.isExcludeActionRequestCd()) {
                crit.add( notEqual("actionRequestCd", filter.getActionRequestCd()));
            } else {
                crit.add( equal("actionRequestCd", filter.getActionRequestCd()));
            }
            filteredByItems.add( "Action Requested" );
        }
    }

    protected void handleDateCriteria( String propertyPath, String filterLabel, Date fromDate, Date toDate, boolean excludeDates, Collection<Predicate> crit, List<String> filteredByItems ) {
        if (fromDate != null || toDate != null) {
            Timestamp fromDateTimestamp = beginningOfDay(fromDate);
            Timestamp toDateTimestamp = endOfDay(toDate);
            if (excludeDates) {
                if (fromDate != null && toDate != null) {
                    crit.add( notBetween(propertyPath, fromDateTimestamp, toDateTimestamp ) );
                } else if (fromDate != null && toDate == null) {
                    crit.add( lessThanOrEqual(propertyPath, fromDateTimestamp ) );
                } else if (fromDate == null && toDate != null) {
                    crit.add( greaterThanOrEqual(propertyPath, toDateTimestamp ) );
                }
            } else {
                if (fromDate != null && toDate != null) {
                    crit.add( between(propertyPath, fromDateTimestamp, toDateTimestamp ) );
                } else if (fromDate != null && toDate == null) {
                    crit.add( greaterThanOrEqual(propertyPath, fromDateTimestamp ) );
                } else if (fromDate == null && toDate != null) {
                    crit.add( lessThanOrEqual(propertyPath, toDateTimestamp ) );
                }
            }
            filteredByItems.add("Date Created");
        }
    }

    protected void handleDocumentCreateDateCriteria( ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        handleDateCriteria("routeHeader.createDate", "Date Created", filter.getCreateDateFrom(), filter.getCreateDateTo(), filter.isExcludeCreateDate(), crit, filteredByItems);
    }

    protected void handleAssignedDateCriteria( ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        handleDateCriteria("dateAssigned", "Date Last Assigned", filter.getLastAssignedDateFrom(), filter.getLastAssignedDateTo(), filter.isExcludeLastAssignedDate(), crit, filteredByItems);
    }

    protected void handleRouteStatusCriteria( ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        if ( StringUtils.isNotBlank(filter.getDocRouteStatus())
                && !filter.getDocRouteStatus().equals(KewApiConstants.ALL_CODE)) {
            if (filter.isExcludeRouteStatus()) {
                crit.add( notEqual("routeHeader.docRouteStatus", filter.getDocRouteStatus() ) );
            } else {
                crit.add( equal("routeHeader.docRouteStatus", filter.getDocRouteStatus() ) );
            }
            filteredByItems.add( "Document Route Status" );
        }
    }

    protected void handleDocumentTitleCriteria( ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        if ( StringUtils.isNotBlank(filter.getDocumentTitle()) ) {
            String docTitle = filter.getDocumentTitle().trim();
            if (docTitle.endsWith("*")) {
                docTitle = docTitle.substring(0, docTitle.length() - 1);
            }
            if (filter.isExcludeDocumentTitle()) {
                crit.add( notLike("docTitle", "%" + docTitle + "%" ) );
            } else {
                crit.add( like("docTitle", "%" + docTitle + "%" ) );
            }
            filteredByItems.add( "Document Title" );
        }
    }

    protected void handleDocumentTypeCriteria( ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        if ( StringUtils.isNotBlank(filter.getDocumentType()) ) {
            String documentTypeName = filter.getDocumentType();
            if (filter.isExcludeDocumentType()) {
                crit.add( notLike( "docName", "%" + documentTypeName + "%" ) );
            } else {
                DocumentType documentType = documentTypeService.findByName(documentTypeName);
                // not an exact document type - just use it as is
                if (documentType == null) {
                    crit.add( like( "docName", "%" + documentTypeName + "%" ) );
                } else {

                    Collection<DocumentType> docs = getAllChildDocumentTypes(documentType);
                    Collection<String> docNames = new ArrayList<String>(docs.size()+1);
                    docNames.add(documentType.getName());
                    for ( DocumentType doc : docs ) {
                        docNames.add(doc.getName());
                    }
                    crit.add( in("docName", docNames) );
                }
            }
            filteredByItems.add( "Document Type" );
        }
    }

    protected Collection<DocumentType> getAllChildDocumentTypes( DocumentType docType ) {
        Collection<DocumentType> allChildren = new ArrayList<DocumentType>();

        List<DocumentType> immediateChildren = documentTypeService.getChildDocumentTypes(docType.getId());
        if ( immediateChildren != null ) {
            allChildren.addAll(immediateChildren);

            for ( DocumentType childDoc : immediateChildren ) {
                allChildren.addAll( getAllChildDocumentTypes(childDoc));
            }
        }

        return allChildren;
    }

    protected void handleWorkgroupCriteria( ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        filter.setGroupId(null);
        if ( StringUtils.isNotBlank(filter.getGroupIdString())
                && !filter.getGroupIdString().trim().equals(KewApiConstants.NO_FILTERING)) {

            filter.setGroupId(filter.getGroupIdString().trim());

            if (filter.isExcludeGroupId()) {
                crit.add( or(
                        notEqual("groupId", filter.getGroupId()),
                        isNull("groupId") ) );
            } else {
                crit.add( equal("groupId", filter.getGroupId()) );
            }
            filteredByItems.add( "Action Request Workgroup" );
        }
    }

    protected void applyPrimaryDelegationCriteria( String actionListUserPrincipalId, ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        // get the groups the user is a part of
        List<String> delegatorGroupIds = KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(actionListUserPrincipalId);
        // add filter for requests where the current user was the primary delegator
        if (delegatorGroupIds != null && !delegatorGroupIds.isEmpty()) {
            crit.add( or( equal("delegatorPrincipalId", actionListUserPrincipalId), in("delegatorGroupId", delegatorGroupIds) ) );
        } else {
            crit.add( equal("delegatorPrincipalId", actionListUserPrincipalId) );
        }
        crit.add( equal("delegationType", DelegationType.PRIMARY.getCode() ) );
        filter.setDelegationType(DelegationType.PRIMARY.getCode());
        filter.setExcludeDelegationType(false);
        filteredByItems.add("Primary Delegator Id");
    }

    /**
     * Apply criteria related to primary delegations.
     *
     * Called only after detecting that the user is filtering on primary validations.
     *
     * @return <b>true</b> if any criteria were applied, <b>false</b> otherwise
     */
    protected boolean handlePrimaryDelegation( String actionListUserPrincipalId, ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        if ( StringUtils.isBlank(filter.getPrimaryDelegateId())
                || filter.getPrimaryDelegateId().trim().equals(KewApiConstants.ALL_CODE) ) {
            // user wishes to see all primary delegations
            applyPrimaryDelegationCriteria(actionListUserPrincipalId, filter, crit, filteredByItems);

            return true;
        } else if (!filter.getPrimaryDelegateId().trim().equals(KewApiConstants.PRIMARY_DELEGATION_DEFAULT)) {
            // user wishes to see primary delegation for a single user
            crit.add( equal("principalId", filter.getPrimaryDelegateId() ) );
            applyPrimaryDelegationCriteria(actionListUserPrincipalId, filter, crit, filteredByItems);

            return true;
        }

        return false;
    }

    /**
     * Apply criteria related to secondary delegations.
     *
     * Called only after detecting that the user is filtering on secondary validations.
     *
     * @return <b>true</b> if any criteria were applied, <b>false</b> otherwise
     */
    protected boolean handleSecondaryDelegation( String actionListUserPrincipalId, ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        crit.add( equal("principalId", actionListUserPrincipalId) );
        if (StringUtils.isBlank(filter.getDelegatorId())) {
            filter.setDelegationType(DelegationType.SECONDARY.getCode());

            // if isExcludeDelegationType() we want to show the default action list which is set up later in this method
            if (!filter.isExcludeDelegationType()) {
                crit.add( equal("delegationType", DelegationType.SECONDARY.getCode() ) );
                filteredByItems.add("Secondary Delegator Id");

                return true;
            }
        } else if (filter.getDelegatorId().trim().equals(KewApiConstants.ALL_CODE)) {
            // user wishes to see all secondary delegations
            crit.add( equal("delegationType", DelegationType.SECONDARY.getCode() ) );
            filter.setDelegationType(DelegationType.SECONDARY.getCode());
            filter.setExcludeDelegationType(false);
            filteredByItems.add("Secondary Delegator Id");

            return true;
        } else if (!filter.getDelegatorId().trim().equals(KewApiConstants.DELEGATION_DEFAULT)) {
            // user has specified an id to see for secondary delegation
            filter.setDelegationType(DelegationType.SECONDARY.getCode());
            filter.setExcludeDelegationType(false);

            if (filter.isExcludeDelegatorId()) {
                crit.add( or( notEqual("delegatorPrincipalId", filter.getDelegatorId()), isNull("delegatorPrincipalId") ) );
                crit.add( or( notEqual("delegatorGroupId", filter.getDelegatorId()), isNull("delegatorGroupId") ) );
            } else {
                crit.add( or( equal("delegatorPrincipalId", filter.getDelegatorId()), equal("delegatorGroupId", filter.getDelegatorId()) ) );
            }
            filteredByItems.add("Secondary Delegator Id");

            return true;
        }

        return false;
    }

    /**
     * Handle the general recipient criteria (user, delegate)
     *
     * @param actionListUserPrincipalId
     * @param filter
     * @param crit
     * @param filteredByItems
     */
    protected void handleRecipientCriteria( String actionListUserPrincipalId, ActionListFilter filter, Collection<Predicate> crit, List<String> filteredByItems ) {
        if (StringUtils.isBlank(filter.getDelegationType())
                && StringUtils.isBlank(filter.getPrimaryDelegateId())
                && StringUtils.isBlank(filter.getDelegatorId())) {
            crit.add( equal("principalId", actionListUserPrincipalId) );
            return;
        }
        if ( StringUtils.equals(filter.getDelegationType(), DelegationType.PRIMARY.getCode() )
                || StringUtils.isNotBlank(filter.getPrimaryDelegateId())) {
            // using a primary delegation
            if ( handlePrimaryDelegation(actionListUserPrincipalId, filter, crit, filteredByItems)) {
                return;
            }
        }

        if (StringUtils.equals(filter.getDelegationType(), DelegationType.SECONDARY.getCode())
                || StringUtils.isNotBlank(filter.getDelegatorId()) ) {
            // using a secondary delegation
            if ( handleSecondaryDelegation(actionListUserPrincipalId, filter, crit, filteredByItems) ) {
                return;
            }
        }

        // if we haven't added delegation criteria above then use the default criteria below
        filter.setDelegationType(DelegationType.SECONDARY.getCode());
        filter.setExcludeDelegationType(true);
        crit.add( equal("principalId", actionListUserPrincipalId) );
        crit.add( or( notEqual("delegationType", DelegationType.SECONDARY.getCode()), isNull("delegationType") ) );
    }

    /**
     * Creates an Action List from the given collection of Action Items.  The Action List should
     * contain only one action item per document.  The action item chosen should be the most "critical"
     * or "important" one on the document.
     *
     * @return the Action List as a Collection of ActionItems
     */
    private Collection<ActionItem> createActionListForUser(Collection<ActionItem> actionItems) {
        Map<String, ActionItem> actionItemMap = new HashMap<String, ActionItem>();
        ActionListPriorityComparator comparator = new ActionListPriorityComparator();
        for (ActionItem potentialActionItem: actionItems) {
            ActionItem existingActionItem = actionItemMap.get(potentialActionItem.getDocumentId());
            if (existingActionItem == null || comparator.compare(potentialActionItem, existingActionItem) > 0) {
                actionItemMap.put(potentialActionItem.getDocumentId(), potentialActionItem);
            }
        }
        return actionItemMap.values();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ActionItem> getActionListForSingleDocument(String documentId) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("getting action list for document id " + documentId);
        }
        Collection<ActionItem> collection = findByDocumentId(documentId);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("found " + collection.size() + " action items for document id " + documentId);
        }
        return createActionListForRouteHeader(collection);
    }

    /**
     * Creates an Action List from the given collection of Action Items.  The Action List should
     * contain only one action item per user.  The action item chosen should be the most "critical"
     * or "important" one on the document.
     *
     * @return the Action List as a Collection of ActionItems
     */
    protected Collection<ActionItem> createActionListForRouteHeader(Collection<ActionItem> actionItems) {
        Map<String, ActionItem> actionItemMap = new HashMap<String, ActionItem>();
        ActionListPriorityComparator comparator = new ActionListPriorityComparator();
        for (ActionItem potentialActionItem: actionItems) {
            ActionItem existingActionItem = actionItemMap.get(potentialActionItem.getPrincipalId());
            if (existingActionItem == null || comparator.compare(potentialActionItem, existingActionItem) > 0) {
                actionItemMap.put(potentialActionItem.getPrincipalId(), potentialActionItem);
            }
        }
        return actionItemMap.values();
    }

    public void setActionListDAO(ActionListDAO actionListDAO) {
        this.actionListDAO = actionListDAO;
    }

    @Override
    public void deleteActionItemNoOutbox(ActionItem actionItem) {
        deleteActionItem(actionItem, false, false);
    }

    @Override
    public void deleteActionItem(ActionItem actionItem) {
        deleteActionItem(actionItem, false);
    }

    @Override
    public void deleteActionItem(ActionItem actionItem, boolean forceIntoOutbox) {
        deleteActionItem(actionItem, forceIntoOutbox, true);
    }

    protected void deleteActionItem(ActionItem actionItem, boolean forceIntoOutbox, boolean putInOutbox) {
        dataObjectService.delete(actionItem);
        // remove notification from KCB
        notificationService.removeNotification(Collections.singletonList(ActionItem.to(actionItem)));
        if (putInOutbox) {
            saveOutboxItem(actionItem, forceIntoOutbox);
        }
    }

    @Override
    public void deleteByDocumentId(String documentId) {
        dataObjectService.deleteMatching(ActionItem.class, QueryByCriteria.Builder.forAttribute("documentId", documentId).build());
    }

    @Override
    public Collection<ActionItem> findByDocumentId(String documentId) {
        QueryResults<ActionItem> results = dataObjectService.findMatching(ActionItem.class,
                QueryByCriteria.Builder.forAttribute("documentId", documentId).build());

        return results.getResults();
    }

    @Override
    public Collection<ActionItem> findByActionRequestId(String actionRequestId) {
        QueryResults<ActionItem> results = dataObjectService.findMatching(ActionItem.class,
                QueryByCriteria.Builder.forAttribute("actionRequestId", actionRequestId).build());

        return results.getResults();
    }

    @Override
    public Collection<ActionItem> findByWorkflowUserDocumentId(String workflowUserId, String documentId) {
        Map<String,String> criteria = new HashMap<String, String>(2);
        criteria.put( "principalId", workflowUserId );
        criteria.put( "documentId", documentId );
        QueryResults<ActionItem> results = dataObjectService.findMatching(ActionItem.class,
                QueryByCriteria.Builder.andAttributes(criteria).build());

        return results.getResults();
    }

    @Override
    public Collection<ActionItem> findByDocumentTypeName(String documentTypeName) {
        QueryResults<ActionItem> results = dataObjectService.findMatching(ActionItem.class,
                QueryByCriteria.Builder.forAttribute("docName", documentTypeName).build());

        return results.getResults();
    }

    @Override
    public ActionItem createActionItemForActionRequest(ActionRequestValue actionRequest) {
        ActionItem actionItem = new ActionItem();

        DocumentRouteHeaderValue routeHeader = actionRequest.getRouteHeader();
        DocumentType docType = routeHeader.getDocumentType();

        actionItem.setActionRequestCd(actionRequest.getActionRequested());
        actionItem.setActionRequestId(actionRequest.getActionRequestId());
        actionItem.setDocName(docType.getName());
        actionItem.setRoleName(actionRequest.getQualifiedRoleName());
        actionItem.setPrincipalId(actionRequest.getPrincipalId());
        actionItem.setDocumentId(actionRequest.getDocumentId());
        actionItem.setDateAssigned(new Timestamp(new Date().getTime()));
        actionItem.setDocHandlerURL(docType.getResolvedDocumentHandlerUrl());
        actionItem.setDocLabel(docType.getLabel());
        actionItem.setDocTitle(routeHeader.getDocTitle());
        actionItem.setGroupId(actionRequest.getGroupId());
        actionItem.setResponsibilityId(actionRequest.getResponsibilityId());
        actionItem.setDelegationType(actionRequest.getDelegationType());
        actionItem.setRequestLabel(actionRequest.getRequestLabel());

        ActionRequestValue delegatorActionRequest = actionRequestService.findDelegatorRequest(actionRequest);
        if (delegatorActionRequest != null) {
            actionItem.setDelegatorPrincipalId(delegatorActionRequest.getPrincipalId());
            actionItem.setDelegatorGroupId(delegatorActionRequest.getGroupId());
        }

        return actionItem;
    }


    @Override
    public void updateActionItemsForTitleChange(String documentId, String newTitle) {
        Collection<ActionItem> items = findByDocumentId(documentId);
        for ( ActionItem item : items ) {
            item.setDocTitle(newTitle);
            saveActionItem(item);
        }
    }

    @Override
    public ActionItem saveActionItem(ActionItem actionItem) {
        return saveActionItemBase(actionItem);
    }

    @Override
    public OutboxItem saveOutboxItem(OutboxItem outboxItem) {
        return saveActionItemBase(outboxItem);
    }

    protected <T extends ActionItemBase> T saveActionItemBase(T actionItemBase) {
        if (actionItemBase.getDateAssigned() == null) {
            actionItemBase.setDateAssigned(dateTimeService.getCurrentTimestamp());
        }
        return dataObjectService.save(actionItemBase);
    }

    public GroupService getGroupService(){
        return KimApiServiceLocator.getGroupService();
    }

    @Override
    public ActionItem findByActionItemId(String actionItemId) {
        return dataObjectService.find(ActionItem.class, actionItemId);
    }

    @Override
    public int getCount(String principalId) {
        return actionListDAO.getCount(principalId);
    }

    @Override
    public List<Object> getMaxActionItemDateAssignedAndCountForUser(String principalId) {
        return actionListDAO.getMaxActionItemDateAssignedAndCountForUser(principalId);
    }

    /**
     *
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.actionlist.service.ActionListService#getOutbox(java.lang.String, org.kuali.rice.kew.actionlist.ActionListFilter)
     */
    @Override
    public Collection<OutboxItem> getOutbox(String principalId, ActionListFilter filter) {
        boolean filterOn = false;
        List<String> filteredByItems = new ArrayList<String>();

        List<Predicate> crit = handleActionItemCriteria(principalId, filter, filteredByItems);

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("running query to get outbox list for criteria " + crit);
        }
        QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(crit);
        QueryResults<OutboxItem> results = dataObjectService.findMatching(OutboxItem.class, query);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("found " + results.getResults().size() + " outbox items for user " + principalId);
        }

        if ( !filteredByItems.isEmpty() ) {
            filterOn = true;
        }
        filter.setFilterLegend(StringUtils.join(filteredByItems, ", "));
        filter.setFilterOn(filterOn);

        return results.getResults();
    }

    @Override
    public Collection<OutboxItem> getOutboxItemsByDocumentType(String documentTypeName) {
        QueryResults<OutboxItem> results = dataObjectService.findMatching(OutboxItem.class,
                QueryByCriteria.Builder.forAttribute("docName", documentTypeName).build());

        return results.getResults();
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.actionlist.service.ActionListService#removeOutboxItems(String, java.util.List)
     */
    @Override
    public void removeOutboxItems(String principalId, List<String> outboxItems) {
        QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(
                in("id", outboxItems));

        dataObjectService.deleteMatching(OutboxItem.class, query);
    }

    @Override
    public OutboxItem saveOutboxItem(ActionItem actionItem) {
        return saveOutboxItem(actionItem, false);
    }

    /**
     *
     * save the ouboxitem unless the document is saved or the user already has the item in their outbox.
     *
     * @see org.kuali.rice.kew.actionlist.service.ActionListService#saveOutboxItem(org.kuali.rice.kew.actionitem.ActionItem, boolean)
     */
    @Override
    public OutboxItem saveOutboxItem(ActionItem actionItem, boolean forceIntoOutbox) {
        Boolean isUsingOutBox = true;
        List<UserOptions> options = userOptionsService.findByUserQualified(actionItem.getPrincipalId(), KewApiConstants.USE_OUT_BOX);
        if (options == null || options.isEmpty()){
            isUsingOutBox = true;
        } else {
            for ( UserOptions u : options ) {
                if ( !StringUtils.equals(u.getOptionVal(), "yes") ) {
                    isUsingOutBox = false;
                    break;
                }
            }
        }

        if (isUsingOutBox
                && ConfigContext.getCurrentContextConfig().getOutBoxOn()
                && getOutboxItemByDocumentIdUserId(actionItem.getDocumentId(), actionItem.getPrincipalId()) == null
                && !routeHeaderService.getRouteHeader(actionItem.getDocumentId()).getDocRouteStatus().equals(
                KewApiConstants.ROUTE_HEADER_SAVED_CD)) {

            // only create an outbox item if this user has taken action on the document
            ActionRequestValue actionRequest = actionRequestService.findByActionRequestId(
                    actionItem.getActionRequestId());
            ActionTakenValue actionTaken = actionRequest.getActionTaken();
            // if an action was taken...
            if (forceIntoOutbox || (actionTaken != null && actionTaken.getPrincipalId().equals(actionItem.getPrincipalId()))) {
                return dataObjectService.save(new OutboxItem(actionItem));
            }

        }
        return null;
    }

    protected OutboxItem getOutboxItemByDocumentIdUserId(String documentId, String principalId) {
        Map<String,String> criteria = new HashMap<String, String>(2);
        criteria.put( "principalId", principalId );
        criteria.put( "documentId", documentId );
        QueryResults<OutboxItem> results = dataObjectService.findMatching(OutboxItem.class,
                QueryByCriteria.Builder.andAttributes(criteria).build());
        if ( results.getResults().isEmpty() ) {
            return null;
        }
        return results.getResults().get(0);
    }

    @Override
    public Collection<ActionItem> findByPrincipalId(String principalId) {
        QueryResults<ActionItem> results = dataObjectService.findMatching(ActionItem.class,
                QueryByCriteria.Builder.forAttribute("principalId", principalId)
                        .setOrderByAscending("documentId").build());

        return results.getResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentRouteHeaderValue getMinimalRouteHeader(String documentId) {
        return actionListDAO.getMinimalRouteHeader(documentId);
    }

    protected Timestamp beginningOfDay(Date date) {
        if ( date == null ) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return new Timestamp( cal.getTimeInMillis() );
    }

    protected Timestamp endOfDay(Date date) {
        if ( date == null ) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return new Timestamp( cal.getTimeInMillis() );
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setActionRequestService(ActionRequestService actionRequestService) {
        this.actionRequestService = actionRequestService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public void setUserOptionsService(UserOptionsService userOptionsService) {
        this.userOptionsService = userOptionsService;
    }

    public void setRouteHeaderService(RouteHeaderService routeHeaderService) {
        this.routeHeaderService = routeHeaderService;
    }
}
