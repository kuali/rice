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
package org.kuali.rice.kew.actionlist.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.collections.ListUtils;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.OutboxItemActionListExtension;
import org.kuali.rice.kew.actionitem.dao.ActionItemDAO;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.actionlist.dao.ActionListDAO;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.UserService;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.workgroup.WorkgroupMembershipChangeProcessor;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * Default implementation of the {@link ActionListService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListServiceImpl implements ActionListService {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

    private ActionListDAO actionListDAO;

    private ActionItemDAO actionItemDAO;

    public Collection<Recipient> findUserSecondaryDelegators(String principalId) throws KEWUserNotFoundException {
        return getActionItemDAO().findSecondaryDelegators(principalId);
    }

    public Collection<Recipient> findUserPrimaryDelegations(String principalId) throws KEWUserNotFoundException {
        return getActionItemDAO().findPrimaryDelegationRecipients(principalId);
    }

    public Collection<ActionItem> getActionList(String principalId, ActionListFilter filter) {
         return getActionListDAO().getActionList(principalId, filter);
    }

    public Collection<ActionItem> getActionListForSingleDocument(Long routeHeaderId) {
         return getActionListDAO().getActionListForSingleDocument(routeHeaderId);
    }

    public void setActionListDAO(ActionListDAO actionListDAO) {
        this.actionListDAO = actionListDAO;
    }

    public ActionListDAO getActionListDAO() {
        return actionListDAO;
    }

    public boolean refreshActionList(String principalId) {
        return KEWServiceLocator.getUserOptionsService().refreshActionList(principalId);
    }

    public void deleteActionItem(ActionItem actionItem) {
        try {
            KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getPrincipalId());
        } catch (Exception e) {
            LOG.error("error saving refreshUserOption", e);
        }
        getActionItemDAO().deleteActionItem(actionItem);
        // remove notification from KCB
        List<ActionItem> l = new ArrayList<ActionItem>(1);
        l.add(actionItem);
        KEWServiceLocator.getNotificationService().removeNotification(l);
        this.saveOutboxItem(actionItem);
    }

    public void deleteByRouteHeaderId(Long routeHeaderId) {
        Collection<ActionItem> actionItems = findByRouteHeaderId(routeHeaderId);
        for (Iterator<ActionItem> iter = actionItems.iterator(); iter.hasNext();) {
            ActionItem actionItem = iter.next();
            try {
                KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getPrincipalId());
            } catch (Exception e) {
                LOG.error("error saving refreshUserOption", e);
            }
        }
        getActionItemDAO().deleteByRouteHeaderId(routeHeaderId);
    }

    public Collection<ActionItem> findByRouteHeaderId(Long routeHeaderId) {
        return getActionItemDAO().findByRouteHeaderId(routeHeaderId);
    }

    public Collection<ActionItem> findByActionRequestId(Long actionRequestId) {
        return getActionItemDAO().findByActionRequestId(actionRequestId);
    }

    public Collection<ActionItem> findByWorkflowUserRouteHeaderId(String workflowUserId, Long routeHeaderId) {
        return getActionItemDAO().findByWorkflowUserRouteHeaderId(workflowUserId, routeHeaderId);
    }

    /**
     * Determines the difference between the current workgroup membership and the new workgroup membership. It then
     * schedules the action item updates to happen asynchronously.
     */
    public void updateActionItemsForWorkgroupChange(String oldKimGroupId, String newKimGroupId) throws KEWUserNotFoundException
	{
        IdentityManagementService ims = KIMServiceLocator.getIdentityManagementService();
        List<String> oldPrincipalIds = ims.getGroupMemberPrincipalIds(oldKimGroupId);
        List<String> newPrincipalIds = ims.getGroupMemberPrincipalIds(newKimGroupId);
        MembersDiff membersDiff = getMembersDiff(oldPrincipalIds, newPrincipalIds);
        for (String removedPrincipalId : membersDiff.getRemovedPrincipalIds()) {
            KSBXMLService workgroupMembershipChangeProcessor = (KSBXMLService) KSBServiceLocator.getMessageHelper()
            .getServiceAsynchronously(new QName(MessageServiceNames.WORKGROUP_MEMBERSHIP_CHANGE_SERVICE));
            try {
                workgroupMembershipChangeProcessor.invoke(WorkgroupMembershipChangeProcessor
                        .getMemberRemovedMessageContents(removedPrincipalId, newKimGroupId));
            } catch (Exception e) {
                throw new WorkflowRuntimeException(e);
            }
        }
        for (String addedPrincipalId : membersDiff.getAddedPrincipalIds()) {
            KSBXMLService workgroupMembershipChangeProcessor = (KSBXMLService) KSBServiceLocator.getMessageHelper()
            .getServiceAsynchronously(new QName(MessageServiceNames.WORKGROUP_MEMBERSHIP_CHANGE_SERVICE));
            try {
                workgroupMembershipChangeProcessor.invoke(WorkgroupMembershipChangeProcessor.getMemberAddedMessageContents(
                        addedPrincipalId, oldKimGroupId));
            } catch (Exception e) {
                throw new WorkflowRuntimeException(e);
            }

        }
    }


    /**
     * Update the user's Action List to reflect their addition to the given Workgroup.
     */
    public void updateActionListForUserAddedToGroup(String principalId, String groupId) throws KEWUserNotFoundException {
        // first verify that the user is still a member of the workgroup
    	if(KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(principalId, groupId))
    	{
    		List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
    		List<String> allGroupsToCheck = KIMServiceLocator.getIdentityManagementService().getParentGroupIds(groupId);
            allGroupsToCheck.add(0, groupId);
            for (String groupToCheckId : allGroupsToCheck) {
                actionRequests.addAll(getActionRequestService().findActivatedByGroup(groupToCheckId));
            }
            for (Iterator requestIt = actionRequests.iterator(); requestIt.hasNext();) {
                ActionRequestValue request = (ActionRequestValue) requestIt.next();
                ActionItem item = createActionItemForActionRequest(request);
                item.setPrincipalId(principalId);
                saveActionItem(item);
            }
        }
    }

    public ActionItem createActionItemForActionRequest(ActionRequestValue actionRequest) {
        ActionItem actionItem = new ActionItem();

        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(
                actionRequest.getRouteHeaderId());
        DocumentType docType = routeHeader.getDocumentType();

        actionItem.setActionRequestCd(actionRequest.getActionRequested());
        actionItem.setActionRequestId(actionRequest.getActionRequestId());
        actionItem.setDocName(docType.getName());
        actionItem.setRoleName(actionRequest.getQualifiedRoleName());
        actionItem.setPrincipalId(actionRequest.getWorkflowId());
        actionItem.setRouteHeaderId(actionRequest.getRouteHeaderId());
        actionItem.setRouteHeader(routeHeader);
        actionItem.setDateAssigned(new Timestamp(new Date().getTime()));
        actionItem.setDocHandlerURL(docType.getDocHandlerUrl());
        actionItem.setDocLabel(docType.getLabel());
        actionItem.setDocTitle(routeHeader.getDocTitle());
        actionItem.setGroupId(actionRequest.getGroupId());
        actionItem.setResponsibilityId(actionRequest.getResponsibilityId());
        actionItem.setDelegationType(actionRequest.getDelegationType());

        ActionRequestValue delegatorActionRequest = getActionRequestService().findDelegatorRequest(actionRequest);
        if (delegatorActionRequest != null) {
            actionItem.setDelegatorWorkflowId(delegatorActionRequest.getWorkflowId());
            actionItem.setDelegatorGroupId(delegatorActionRequest.getGroupId());
        }

        return actionItem;
    }

    /**
     * Update the user's Action List to reflect their removal from the given Workgroup.
     */
    public void updateActionListForUserRemovedFromGroup(String principalId, String groupId)
    throws KEWUserNotFoundException {
        // first verify that the user is no longer a member of the workgroup
    	if(!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(principalId, groupId))
    	{
    		List<String> allGroupsToCheck = KIMServiceLocator.getIdentityManagementService().getParentGroupIds(groupId);
            allGroupsToCheck.add(0, groupId);
            Collection<ActionItem> actionItems = this.findByPrincipalId(principalId);
    		for (Iterator<ActionItem> itemIt = actionItems.iterator(); itemIt.hasNext();) {
            	ActionItem item = itemIt.next();
            	if (item.isWorkgroupItem()) {
            		for (String groupIdToCheck : allGroupsToCheck) {
            			if (item.getGroupId().equals(groupIdToCheck)) {
            				deleteActionItem(item);
            			}
            		}
            	}
            }
    	}

    }

    public void updateActionItemsForTitleChange(Long routeHeaderId, String newTitle) throws KEWUserNotFoundException {
        Collection<ActionItem> items = getActionItemDAO().findByRouteHeaderId(routeHeaderId);
        for (Iterator<ActionItem> iterator = items.iterator(); iterator.hasNext();) {
            ActionItem item = iterator.next();
            item.setDocTitle(newTitle);
            saveActionItem(item);
        }
    }

    private MembersDiff getMembersDiff(List<String> oldMemberPrincipalIds, List<String> newMemberPrincipalIds) {
        Set<String> addedPrincipalIds = new HashSet<String>(ListUtils.subtract(newMemberPrincipalIds, oldMemberPrincipalIds));
        Set<String> removedPrincipalIds = new HashSet<String>(ListUtils.subtract(oldMemberPrincipalIds, newMemberPrincipalIds));
        return new MembersDiff(addedPrincipalIds, removedPrincipalIds);
    }

    public void saveActionItem(ActionItem actionItem) throws KEWUserNotFoundException {
        KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getPrincipalId());
        getActionItemDAO().saveActionItem(actionItem);
    }

    public ActionItemDAO getActionItemDAO() {
        return actionItemDAO;
    }

    public ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getActionRequestService();
    }

    public GroupService getGroupService(){
    	return KIMServiceLocator.getGroupService();
    }

    public void setActionItemDAO(ActionItemDAO actionItemDAO) {
        this.actionItemDAO = actionItemDAO;
    }

    public UserService getUserService() {
        return (UserService) KEWServiceLocator.getUserService();
    }

    private class MembersDiff {
        private final Set<String> addedPrincipalIds;

        private final Set<String> removedPrincipalIds;

        public MembersDiff(Set<String> addedPrincipalIds, Set<String>removedPrincipalIds) {
            this.addedPrincipalIds = addedPrincipalIds;
            this.removedPrincipalIds = removedPrincipalIds;
        }

        public Set<String> getAddedPrincipalIds() {
            return addedPrincipalIds;
        }

        public Set<String> getRemovedPrincipalIds() {
            return removedPrincipalIds;
        }
    }

    public void validateActionItem(ActionItem actionItem) {
        List errors = new ArrayList();
        String workflowId = actionItem.getPrincipalId();
        if (workflowId == null || workflowId.trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem person null.", "actionitem.personid.empty", actionItem
                    .getActionItemId().toString()));
        } else {
            try {
                getUserService().getWorkflowUser(new WorkflowUserId(workflowId));
            } catch (KEWUserNotFoundException e) {
                errors.add(new WorkflowServiceErrorImpl("ActionItem person invalid.", "actionitem.personid.invalid",
                        actionItem.getActionItemId().toString()));
            }
        }

        if (actionItem.getDateAssigned() == null) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem date assigned empty.", "actionitem.dateassigned.empty",
                    actionItem.getActionItemId().toString()));
        }

        String actionRequestCd = actionItem.getActionRequestCd();
        if (actionRequestCd == null || actionRequestCd.trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem action request cd empty.",
                    "actionitem.actionrequestcd.empty", actionItem.getActionItemId().toString()));
        } else if (!KEWConstants.ACTION_REQUEST_CD.containsKey(actionRequestCd)) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem action request cd invalid.",
                    "actionitem.actionrequestcd.invalid", actionItem.getActionItemId().toString()));
        }

        if (actionItem.getActionRequestId() == null) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem action request id empty.",
                    "actionitem.actionrequestid.empty", actionItem.getActionItemId().toString()));
        }

        if (actionItem.getRouteHeaderId() == null) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem Document id empty.", "actionitem.routeheaderid.empty",
                    actionItem.getActionItemId().toString()));
        } else if (KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getRouteHeaderId()) == null) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem Document id invalid.", "actionitem.routeheaderid.invalid",
                    actionItem.getActionItemId().toString()));
        }

        String docTypeName = actionItem.getDocName();
        DocumentType docType = null;
        if (docTypeName == null || docTypeName.trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem doctypename empty.", "actionitem.doctypename.empty",
                    actionItem.getActionItemId().toString()));
        } else {
            docType = KEWServiceLocator.getDocumentTypeService().findByName(actionItem.getDocName());
            if (docType == null) {
                errors.add(new WorkflowServiceErrorImpl("ActionItem doctypename invalid.", "actionitem.doctypename.invalid",
                        actionItem.getActionItemId().toString()));
            }
        }

        if (actionItem.getDocLabel() == null || actionItem.getDocLabel().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem doctypelabel empty.", "actionitem.doctypelabel.empty",
                    actionItem.getActionItemId().toString()));
        } else if (docType != null && !docType.getLabel().equals(actionItem.getDocLabel())) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem doctypelabel no match.", "actionitem.doctypelabel.nomatch",
                    actionItem.getActionItemId().toString()));
        }

        if (actionItem.getDocHandlerURL() == null || actionItem.getDocHandlerURL().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem doc handler url empty.", "actionitem.dochdrurl.empty",
                    actionItem.getActionItemId().toString()));
        } else if (docType != null && !docType.getDocHandlerUrl().equals(actionItem.getDocHandlerURL())) {
            errors.add(new WorkflowServiceErrorImpl("ActionItem doc handler url no match.", "actionitem.dochdrurl.nomatch",
                    actionItem.getActionItemId().toString()));
        }

        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("ActionItem Validation Error", errors);
        }
    }

    public ActionItem findByActionItemId(Long actionItemId) {
        return getActionItemDAO().findByActionItemId(actionItemId);
    }

    public int getCount(String principalId) {
        return getActionListDAO().getCount(principalId);
    }

    public void saveRefreshUserOption(String principalId) {
        KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(principalId);
    }

    /**
     *
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.actionlist.service.ActionListService#getOutbox(java.lang.String, org.kuali.rice.kew.actionlist.ActionListFilter)
     */
    public Collection<ActionItem> getOutbox(String principalId, ActionListFilter filter) {
        return this.getActionListDAO().getOutbox(principalId, filter);
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.actionlist.service.ActionListService#removeOutboxItems(org.kuali.rice.kew.user.WorkflowUser,
     *      java.util.List)
     */
    public void removeOutboxItems(String principalId, List<Long> outboxItems) {
        this.getActionListDAO().removeOutboxItems(principalId, outboxItems);
    }

    /**
     *
     * save the ouboxitem unless the document is saved or the user already has the item in their outbox.
     *
     * @see org.kuali.rice.kew.actionlist.service.ActionListService#saveOutboxItem(org.kuali.rice.kew.actionitem.OutboxItemActionListExtension)
     */
    public void saveOutboxItem(ActionItem actionItem) {
        try {
            if (KEWServiceLocator.getPreferencesService().getPreferences(actionItem.getPrincipalId()).isUsingOutbox()
                    && ConfigContext.getCurrentContextConfig().getOutBoxOn()
                    && getActionListDAO().getOutboxByDocumentIdUserId(actionItem.getRouteHeaderId(), actionItem.getPrincipalId()) == null
                    && !actionItem.getRouteHeader().getDocRouteStatus().equals(KEWConstants.ROUTE_HEADER_SAVED_CD)) {
                // only create an outbox item if this user has taken action on the document
                ActionRequestValue actionRequest = KEWServiceLocator.getActionRequestService().findByActionRequestId(
                        actionItem.getActionRequestId());
                ActionTakenValue actionTaken = actionRequest.getActionTaken();
                // if an action was taken...
                if (actionTaken != null && actionTaken.getWorkflowUser().getWorkflowId().equals(actionItem.getPrincipalId())) {
                    this.getActionListDAO().saveOutboxItem(new OutboxItemActionListExtension(actionItem));
                }
            }
        } catch (KEWUserNotFoundException eunfe) {
            throw new WorkflowRuntimeException(eunfe);
        }
    }

	/**
	 * This overridden method replaced findByWorkflowUser
	 *
	 * @see org.kuali.rice.kew.actionlist.service.ActionListService#findByPrincipalId(java.lang.String)
	 */
	public Collection<ActionItem> findByPrincipalId(String principalId) {
		return getActionItemDAO().findByPrincipalId(principalId);
	}




}