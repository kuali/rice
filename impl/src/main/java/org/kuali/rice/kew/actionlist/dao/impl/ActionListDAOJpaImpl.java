/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kew.actionlist.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.ActionItemActionListExtension;
import org.kuali.rice.kew.actionitem.OutboxItemActionListExtension;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionlist.dao.ActionListDAO;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueActionListExtension;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * OJB implementation of the {@link ActionListDAO}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionListDAOJpaImpl implements ActionListDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListDAOJpaImpl.class);
	
    @PersistenceContext(unitName="kew-unit")
	private EntityManager entityManager;
	
    public Collection<ActionItem> getActionList(String principalId, ActionListFilter filter) {
        return toActionItemActionListExtensions(getActionItemsInActionList(ActionItem.class, principalId, filter));
    }
    
    /**
	 * This method ...
	 * 
	 * @param actionItems
	 * @return actionItemActionListExtensions
	 */
	private Collection<ActionItem> toActionItemActionListExtensions(
			Collection<ActionItem> actionItems) {
		List<ActionItem> actionItemActionListExtensions = new ArrayList<ActionItem>();
		for(ActionItem actionItem:actionItems){
			actionItemActionListExtensions.add(toActionItemActionListExtension(actionItem));
		}
		return actionItemActionListExtensions;
	}

	/**
	 * This method ...
	 * 
	 * @param actionItem
	 * @return
	 */
	private ActionItemActionListExtension toActionItemActionListExtension(
			ActionItem actionItem) {

		if(actionItem==null){
			return null;
		}
		
		ActionItemActionListExtension actionItemExt = new ActionItemActionListExtension();
		
		actionItemExt.setId(actionItem.getId());
		actionItemExt.setPrincipalId(actionItem.getPrincipalId());
		actionItemExt.setDateAssigned(actionItem.getDateAssigned());
		actionItemExt.setActionRequestCd(actionItem.getActionRequestCd());
		actionItemExt.setActionRequestId(actionItem.getActionRequestId());
		actionItemExt.setDocumentId(actionItem.getDocumentId());
		actionItemExt.setResponsibilityId(actionItem.getResponsibilityId());
		actionItemExt.setGroupId(actionItem.getGroupId());
		actionItemExt.setRoleName(actionItem.getRoleName());
		actionItemExt.setDocTitle(actionItem.getDocTitle());
		actionItemExt.setDocLabel(actionItem.getDocLabel());
		actionItemExt.setDocHandlerURL(actionItem.getDocHandlerURL());
		actionItemExt.setDocName(actionItem.getDocName());
		actionItemExt.setDelegatorPrincipalId(actionItem.getDelegatorPrincipalId());
		actionItemExt.setDelegatorGroupId(actionItem.getDelegatorGroupId());
		actionItemExt.setDelegationType(actionItem.getDelegationType());
		actionItemExt.setLockVerNbr(actionItem.getLockVerNbr());
		actionItemExt.setDocumentId(actionItem.getDocumentId());
		actionItemExt.setRequestLabel(actionItem.getRequestLabel());
		//actionItemExt.setRouteHeader(toDocumentRouteHeaderValueActionListExtension(actionItem.getRouteHeader()));
		
		// These properties are not mapped in OJB-repository-kew.xml
		// actionItemExt.setActionItemIndex(actionItem.getActionItemIndex());
		// actionItemExt.setActionToTake(actionItem.getActionToTake());
		// actionItemExt.setCustomActions(actionItem.getCustomActions());
		// actionItemExt.setDateAssignedString(actionItem.getDateAssignedString());
		// actionItemExt.setDelegatorGroup();
		// actionItemExt.setDisplayParameters();
		// FIXME this causes null pointer - actionItemExt.setGroup(actionItem.getGroup());
		// actionItemExt.setLastApprovedDate(actionItem.getLastApprovedDate());
		// actionItemExt.setRowStyleClass();
		
		return actionItemExt;
	}

	/**
	 * This method ...
	 * 
	 * @param routeHeader
	 * @return
	 */
	private DocumentRouteHeaderValueActionListExtension toDocumentRouteHeaderValueActionListExtension(
			DocumentRouteHeaderValue routeHeader) {

		if(routeHeader==null){
			return null;
		}
		
		DocumentRouteHeaderValueActionListExtension extension = new DocumentRouteHeaderValueActionListExtension();
		
		extension.setDocumentId(routeHeader.getDocumentId());
		extension.setDocumentTypeId(routeHeader.getDocumentTypeId());
		extension.setDocRouteStatus(routeHeader.getDocRouteStatus());
		extension.setDocRouteLevel(routeHeader.getDocRouteLevel());
		extension.setStatusModDate(routeHeader.getStatusModDate());
		extension.setCreateDate(routeHeader.getCreateDate());
		extension.setApprovedDate(routeHeader.getApprovedDate());
		extension.setFinalizedDate(routeHeader.getFinalizedDate());
		extension.setRouteStatusDate(routeHeader.getRouteStatusDate());
		extension.setRouteLevelDate(routeHeader.getRouteLevelDate());
		extension.setDocTitle(routeHeader.getDocTitle());
		extension.setAppDocId(routeHeader.getAppDocId());
		extension.setDocVersion(routeHeader.getDocVersion());
		extension.setInitiatorWorkflowId(routeHeader.getInitiatorWorkflowId());
		extension.setVersionNumber(routeHeader.getVersionNumber());
		extension.setAppDocStatus(routeHeader.getAppDocStatus());
		extension.setAppDocStatusDate(routeHeader.getAppDocStatusDate());

		return extension;
	}

	public Collection<ActionItem> getActionListForSingleDocument(String documentId) {
        LOG.debug("getting action list for document id " + documentId);
        Criteria crit = new Criteria(ActionItem.class.getName());
        crit.eq("documentId", documentId);
        crit.eq("TYPE(__JPA_ALIAS[[0]]__)", ActionItem.class);
        Collection<ActionItem> collection = new QueryByCriteria(entityManager, crit).toQuery().getResultList();
        LOG.debug("found " + collection.size() + " action items for document id " + documentId);
        return toActionItemActionListExtensions(createActionListForRouteHeader(collection));
    }
    
    private Criteria setUpActionListCriteria(Class objectsToRetrieve, String principalId, ActionListFilter filter) {
        LOG.debug("setting up Action List criteria");
        Criteria crit = new Criteria(objectsToRetrieve.getName());
        boolean filterOn = false;
        String filteredByItems = "";
        
        if (filter.getActionRequestCd() != null && !"".equals(filter.getActionRequestCd().trim()) && !filter.getActionRequestCd().equals(KewApiConstants.ALL_CODE)) {
            if (filter.isExcludeActionRequestCd()) {
                crit.ne("actionRequestCd", filter.getActionRequestCd());
            } else {
                crit.eq("actionRequestCd", filter.getActionRequestCd());
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Action Requested";
        }

        if (filter.getCreateDateFrom() != null || filter.getCreateDateTo() != null) {
            if (filter.isExcludeCreateDate()) {
                if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() != null) {
                    crit.notBetween("routeHeader.createDate", new Timestamp(beginningOfDay(filter.getCreateDateFrom()).getTime()), new Timestamp(endOfDay(filter.getCreateDateTo()).getTime()));
                } else if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() == null) {
                    crit.lte("routeHeader.createDate", new Timestamp(beginningOfDay(filter.getCreateDateFrom()).getTime()));
                } else if (filter.getCreateDateFrom() == null && filter.getCreateDateTo() != null) {
                    crit.gte("routeHeader.createDate", new Timestamp(endOfDay(filter.getCreateDateTo()).getTime()));
                }
            } else {
                if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() != null) {
                    crit.between("routeHeader.createDate", new Timestamp(beginningOfDay(filter.getCreateDateFrom()).getTime()), new Timestamp(endOfDay(filter.getCreateDateTo()).getTime()));
                } else if (filter.getCreateDateFrom() != null && filter.getCreateDateTo() == null) {
                    crit.gte("routeHeader.createDate", new Timestamp(beginningOfDay(filter.getCreateDateFrom()).getTime()));
                } else if (filter.getCreateDateFrom() == null && filter.getCreateDateTo() != null) {
                    crit.lte("routeHeader.createDate", new Timestamp(endOfDay(filter.getCreateDateTo()).getTime()));
                }
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Date Created";
        }

        if (filter.getDocRouteStatus() != null && !"".equals(filter.getDocRouteStatus().trim()) && !filter.getDocRouteStatus().equals(KewApiConstants.ALL_CODE)) {
            if (filter.isExcludeRouteStatus()) {
                crit.ne("routeHeader.docRouteStatus", filter.getDocRouteStatus());
            } else {
                crit.eq("routeHeader.docRouteStatus", filter.getDocRouteStatus());
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
                crit.notLike("docTitle", "%" + docTitle + "%");
            } else {
                crit.like("docTitle", "%" + docTitle + "%");
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Document Title";
        }

        if (filter.getDocumentType() != null && !"".equals(filter.getDocumentType().trim())) {
            if (filter.isExcludeDocumentType()) {
                crit.notLike("docName", "%" + filter.getDocumentType() + "%");
            } else {
            	String documentTypeName = filter.getDocumentType();
            	DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
            	if (documentType == null) {
            	    crit.like("docName", "%" + filter.getDocumentType() + "%");
            	} else {
            	    // search this document type plus it's children
            	    Criteria docTypeCrit = new Criteria(objectsToRetrieve.getName());
            	    constructDocumentTypeCriteria(objectsToRetrieve.getName(), docTypeCrit, documentType);
            	    crit.and(docTypeCrit);
            	}
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Document Type";
        }

        if (filter.getLastAssignedDateFrom() != null || filter.getLastAssignedDateTo() != null) {
            if (filter.isExcludeLastAssignedDate()) {
                if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() != null) {
                    crit.notBetween("dateAssigned", new Timestamp(beginningOfDay(filter.getLastAssignedDateFrom()).getTime()), new Timestamp(endOfDay(filter.getLastAssignedDateTo()).getTime()));
                } else if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() == null) {
                    crit.lte("dateAssigned", new Timestamp(beginningOfDay(filter.getLastAssignedDateFrom()).getTime()));
                } else if (filter.getLastAssignedDateFrom() == null && filter.getLastAssignedDateTo() != null) {
                    crit.gte("dateAssigned", new Timestamp(endOfDay(filter.getLastAssignedDateTo()).getTime()));
                }
            } else {
                if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() != null) {
                    crit.between("dateAssigned", new Timestamp(beginningOfDay(filter.getLastAssignedDateFrom()).getTime()), new Timestamp(endOfDay(filter.getLastAssignedDateTo()).getTime()));
                } else if (filter.getLastAssignedDateFrom() != null && filter.getLastAssignedDateTo() == null) {
                    crit.gte("dateAssigned", new Timestamp(beginningOfDay(filter.getLastAssignedDateFrom()).getTime()));
                } else if (filter.getLastAssignedDateFrom() == null && filter.getLastAssignedDateTo() != null) {
                    crit.lte("dateAssigned", new Timestamp(endOfDay(filter.getLastAssignedDateTo()).getTime()));
                }
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Date Last Assigned";
        }

        filter.setGroupId(null);
        if (filter.getGroupIdString() != null && !"".equals(filter.getGroupIdString().trim()) && !filter.getGroupIdString().trim().equals(KewApiConstants.NO_FILTERING)) {
            filter.setGroupId(filter.getGroupId());
            if (filter.isExcludeGroupId()) {
                Criteria critNotEqual = new Criteria(objectsToRetrieve.getName());
                critNotEqual.ne("groupId", filter.getGroupId());
                Criteria critNull = new Criteria(objectsToRetrieve.getName());
                critNull.isNull("groupId");
                critNotEqual.or(critNull);
                crit.and(critNotEqual);
            } else {
                crit.eq("groupId", filter.getGroupId());
            }
            filteredByItems += filteredByItems.length() > 0 ? ", " : "";
            filteredByItems += "Action Request Workgroup";
        }

        if (filteredByItems.length() > 0) {
            filterOn = true;
        }
        
        boolean addedDelegationCriteria = false;
        if (StringUtils.isBlank(filter.getDelegationType()) && StringUtils.isBlank(filter.getPrimaryDelegateId()) && StringUtils.isBlank(filter.getDelegatorId())) {
            crit.eq("principalId", principalId);
            addedDelegationCriteria = true;
        } else if ((StringUtils.isNotBlank(filter.getDelegationType()) && DelegationType.PRIMARY.getCode().equals(filter.getDelegationType()))
                || StringUtils.isNotBlank(filter.getPrimaryDelegateId())) {
            // using a primary delegation
            if ((StringUtils.isBlank(filter.getPrimaryDelegateId())) || (filter.getPrimaryDelegateId().trim().equals(KewApiConstants.ALL_CODE))) {
                // user wishes to see all primary delegations
                Criteria userCrit = new Criteria(objectsToRetrieve.getName());
                Criteria groupCrit = new Criteria(objectsToRetrieve.getName());
                Criteria orCrit = new Criteria(objectsToRetrieve.getName());
                userCrit.eq("delegatorPrincipalId", principalId);
                
                List<String> userGroupIds = new ArrayList<String>();
                for(String id: KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(principalId)){
                	userGroupIds.add(id);
                }
                if (!userGroupIds.isEmpty()) {
                	groupCrit.in("delegatorGroupId", userGroupIds);
                }
                orCrit.or(userCrit);
                orCrit.or(groupCrit);
                crit.and(orCrit);
                crit.eq("delegationType", DelegationType.PRIMARY.getCode());
                filter.setDelegationType(DelegationType.PRIMARY.getCode());
                filter.setExcludeDelegationType(false);
                addToFilterDescription(filteredByItems, "Primary Delegator Id");
                addedDelegationCriteria = true;
                filterOn = true;
            } else if (!filter.getPrimaryDelegateId().trim().equals(KewApiConstants.PRIMARY_DELEGATION_DEFAULT)) {
                // user wishes to see primary delegation for a single user
                crit.eq("principalId", filter.getPrimaryDelegateId());
                Criteria userCrit = new Criteria(objectsToRetrieve.getName());
                Criteria groupCrit = new Criteria(objectsToRetrieve.getName());
                Criteria orCrit = new Criteria(objectsToRetrieve.getName());
                userCrit.eq("delegatorPrincipalId", principalId);
                List<String> userGroupIds = new ArrayList<String>();
                for(String id: KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(principalId)){
                	userGroupIds.add(id);
                }
                if (!userGroupIds.isEmpty()) {
                	groupCrit.in("delegatorGroupId", userGroupIds);
                }
                orCrit.or(userCrit);
                orCrit.or(groupCrit);
                crit.and(orCrit);
                crit.eq("delegationType", DelegationType.PRIMARY.getCode());
                filter.setDelegationType(DelegationType.PRIMARY.getCode());
                filter.setExcludeDelegationType(false);
                addToFilterDescription(filteredByItems, "Primary Delegator Id");
                addedDelegationCriteria = true;
                filterOn = true;
            }
        }
        if (!addedDelegationCriteria && ( (StringUtils.isNotBlank(filter.getDelegationType()) && DelegationType.SECONDARY.getCode().equals(filter.getDelegationType()))
                || StringUtils.isNotBlank(filter.getDelegatorId()) )) {
            // using a secondary delegation
            crit.eq("principalId", principalId);
            if (StringUtils.isBlank(filter.getDelegatorId())) {
                filter.setDelegationType(DelegationType.SECONDARY.getCode());
                // if isExcludeDelegationType() we want to show the default aciton list which is set up later in this method
                if (!filter.isExcludeDelegationType()) {
                    crit.eq("delegationType", DelegationType.SECONDARY.getCode());
                    addToFilterDescription(filteredByItems, "Secondary Delegator Id");
                    addedDelegationCriteria = true;
                    filterOn = true;
                }
            } else if (filter.getDelegatorId().trim().equals(KewApiConstants.ALL_CODE)) {
                // user wishes to see all secondary delegations
                crit.eq("delegationType", DelegationType.SECONDARY.getCode());
                filter.setDelegationType(DelegationType.SECONDARY.getCode());
                filter.setExcludeDelegationType(false);
                addToFilterDescription(filteredByItems, "Secondary Delegator Id");
                addedDelegationCriteria = true;
                filterOn = true;
            } else if (!filter.getDelegatorId().trim().equals(
                    KewApiConstants.DELEGATION_DEFAULT)) {
                // user has specified an id to see for secondary delegation
                filter.setDelegationType(DelegationType.SECONDARY.getCode());
                filter.setExcludeDelegationType(false);
                Criteria userCrit = new Criteria(objectsToRetrieve.getName());
                Criteria groupCrit = new Criteria(objectsToRetrieve.getName());
                if (filter.isExcludeDelegatorId()) {
                    Criteria userNull = new Criteria(objectsToRetrieve.getName());
                    userCrit.ne("delegatorPrincipalId", filter.getDelegatorId());
                    userNull.isNull("delegatorPrincipalId");
                    userCrit.or(userNull);
                    Criteria groupNull = new Criteria(objectsToRetrieve.getName());
                    groupCrit.ne("delegatorGroupId", filter.getDelegatorId());
                    groupNull.isNull("delegatorGroupId");
                    groupCrit.or(groupNull);
                    crit.and(userCrit);
                    crit.and(groupCrit);
                } else {
                    userCrit.eq("delegatorPrincipalId", filter.getDelegatorId());
                    groupCrit.eq("delegatorGroupId", filter.getDelegatorId());
                    userCrit.or(groupCrit);
                    crit.and(userCrit);
                }
                addToFilterDescription(filteredByItems, "Secondary Delegator Id");
                addedDelegationCriteria = true;
                filterOn = true;
            }
        }
        
        // if we haven't added delegation criteria then use the default criteria below
        if (!addedDelegationCriteria) {
            crit.eq("principalId", principalId);
            filter.setDelegationType(DelegationType.SECONDARY.getCode());
            filter.setExcludeDelegationType(true);
            Criteria critNotEqual = new Criteria(objectsToRetrieve.getName());
            Criteria critNull = new Criteria(objectsToRetrieve.getName());
            critNotEqual.ne("delegationType", DelegationType.SECONDARY.getCode());
            critNull.isNull("delegationType");
            critNotEqual.or(critNull);
            crit.and(critNotEqual);
        }

 
        if (! "".equals(filteredByItems)) {
            filteredByItems = "Filtered by " + filteredByItems;
        }
        filter.setFilterLegend(filteredByItems);
        filter.setFilterOn(filterOn);

        LOG.debug("returning from Action List criteria");
        return crit;
    }
    
    private void constructDocumentTypeCriteria(String entityName, Criteria criteria, DocumentType documentType) {
    	// search this document type plus it's children
    	Criteria docTypeBaseCrit = new Criteria(entityName);
    	docTypeBaseCrit.eq("docName", documentType.getName());
    	criteria.or(docTypeBaseCrit);
    	Collection children = documentType.getChildrenDocTypes();
    	if (children != null) {
    	    for (Iterator iterator = children.iterator(); iterator.hasNext();) {
    	    	DocumentType childDocumentType = (DocumentType) iterator.next();
    	    	constructDocumentTypeCriteria(entityName, criteria, childDocumentType);
    	    }
    	}
    }
    
    private void addToFilterDescription(String filterDescription, String labelToAdd) {
        filterDescription += filterDescription.length() > 0 ? ", " : "";
        filterDescription += labelToAdd;
    }

    private static final String ACTION_LIST_COUNT_QUERY = "select count(distinct(ai.doc_hdr_id)) from krew_actn_itm_t ai where ai.PRNCPL_ID = ? and (ai.dlgn_typ is null or ai.dlgn_typ = 'P')";

    public int getCount(final String workflowId) {
    	
    	javax.persistence.Query q = entityManager.createNativeQuery(ACTION_LIST_COUNT_QUERY);
    	q.setParameter(1, workflowId);
    	Number result = (Number)q.getSingleResult();
    	return result.intValue();
    }

    /**
     * Creates an Action List from the given collection of Action Items.  The Action List should
     * contain only one action item per document.  The action item chosen should be the most "critical"
     * or "important" one on the document.
     *
     * @return the Action List as a Collection of ActionItems
     */
    private <T extends ActionItem> Collection<T> createActionListForUser(Collection<T> actionItems) {
        Map<String, T> actionItemMap = new HashMap<String, T>();
        ActionListPriorityComparator comparator = new ActionListPriorityComparator();
        for (T potentialActionItem: actionItems) {
            T existingActionItem = actionItemMap.get(potentialActionItem.getDocumentId());
            if (existingActionItem == null || comparator.compare(potentialActionItem, existingActionItem) > 0) {
                actionItemMap.put(potentialActionItem.getDocumentId(), potentialActionItem);
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
            ActionItem existingActionItem = actionItemMap.get(potentialActionItem.getPrincipalId());
            if (existingActionItem == null || comparator.compare(potentialActionItem, existingActionItem) > 0) {
                actionItemMap.put(potentialActionItem.getPrincipalId(), potentialActionItem);
            }
        }
        return actionItemMap.values();
    }
    
    private <T extends ActionItem> Collection<ActionItem> getActionItemsInActionList(Class<T> objectsToRetrieve, String principalId, ActionListFilter filter) {
        LOG.debug("getting action list for user " + principalId);
        Criteria crit = null;
        if (filter == null) {
            crit = new Criteria(objectsToRetrieve.getName());
            crit.eq("principalId", principalId);
        } else {
            crit = setUpActionListCriteria(objectsToRetrieve, principalId, filter);
        }
        LOG.debug("running query to get action list for criteria " + crit);
        Collection<ActionItem> collection = new QueryByCriteria(entityManager, crit).toQuery().getResultList();
        LOG.debug("found " + collection.size() + " action items for user " + principalId);
        return createActionListForUser(collection);
    }

    public Collection<ActionItem> getOutbox(String principalId, ActionListFilter filter) {
        return getActionItemsInActionList(OutboxItemActionListExtension.class, principalId, filter);
    }

    /**
     * Deletes all outbox items specified by the list of ids
     * 
     * @see org.kuali.rice.kew.actionlist.dao.ActionListDAO#removeOutboxItems(String, java.util.List)
     */
    public void removeOutboxItems(String principalId, List<String> outboxItems) {
        Criteria crit = new Criteria(OutboxItemActionListExtension.class.getName());
        crit.in("id", outboxItems);
        for(Object entity:new QueryByCriteria(entityManager, crit).toQuery().getResultList()){
        	entityManager.remove(entity);
        }
    }

    /**
     * Saves an outbox item
     * 
     * @see org.kuali.rice.kew.actionlist.dao.ActionListDAO#saveOutboxItem(org.kuali.rice.kew.actionitem.OutboxItemActionListExtension)
     */
    public void saveOutboxItem(OutboxItemActionListExtension outboxItem) {
    	if(outboxItem.getId()==null){
    		entityManager.persist(outboxItem);
    	}else{
    	  //TODO, merge will not update the outboxitem pointer to the merged entity
    		OrmUtils.merge(entityManager, outboxItem);
    	}
    	entityManager.flush();
    }

    /**
     * Gets the outbox item associated with the document id
     * 
     * @see org.kuali.rice.kew.actionlist.dao.ActionListDAO#getOutboxByDocumentId(java.lang.Long)
     */
    public OutboxItemActionListExtension getOutboxByDocumentId(String documentId) {
        Criteria crit = new Criteria(OutboxItemActionListExtension.class.getName());
        crit.eq("documentId", documentId);
        return (OutboxItemActionListExtension) new QueryByCriteria(entityManager, crit).toQuery().getSingleResult();
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kew.actionlist.dao.ActionListDAO#getOutboxByDocumentIdUserId(Long, String)
     */
    public OutboxItemActionListExtension getOutboxByDocumentIdUserId(String documentId, String userId) {
        Criteria crit = new Criteria(OutboxItemActionListExtension.class.getName());
        crit.eq("documentId", documentId);
        crit.eq("principalId", userId);
       	return (OutboxItemActionListExtension) new QueryByCriteria(entityManager, crit).toQuery().getSingleResult();
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

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    
}
